package goos7850;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		String setGlobalTrue = "SET GLOBAL local_infile=true;";
		String setGlobalFalse = "SET GLOBAL local_infile=false;";
		String dropTable = "DROP TABLE transactions;";
		// Fields in table should match up to those in the file
		String makeTable = "CREATE TABLE transactions ( Invoice VARCHAR(8), StockCode VARCHAR(20), "
				+ "Description VARCHAR(255), Quantity INT, InvoiceDate DATE, Price DECIMAL, CustomerID VARCHAR(10), "
				+ "Country VARCHAR(32) );";
		String localDir = doubledSlashes(System.getProperty("user.dir"));
		// Generated using HeidiSQL
		String loadQuery = "LOAD DATA LOW_PRIORITY LOCAL INFILE '"+localDir+"\\\\data.csv' REPLACE INTO"
				+ " TABLE `import_test`.`transactions` CHARACTER SET utf8 FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY "
				+ "'\"' LINES TERMINATED BY '\\r\\n' IGNORE 1 LINES (`Invoice`, `StockCode`, `Description`, @ColVar3, "
				+ "`InvoiceDate`, @ColVar5, `CustomerID`, `Country`) SET `Quantity` = REPLACE(REPLACE(@ColVar3, ',', ''),"
				+ " '.', '.'), `price` = REPLACE(REPLACE(@ColVar5, ',', ''), '.', '.');";

		
		String TRANS_TABLE = "transactions";
		String ITEM_ID = "StockCode";
		String ITEMNAME = "Description";
		String TRANS_ID = "Invoice";
		
		double MIN_SUPPORT = 0.05;
		
		String address;
		String username="root";
		String password;
		String database;
		String answeryn;
		
		Scanner input = new Scanner(System.in);
		System.out.print("Server Address: ");
		address = input.nextLine();
		//TODO: Password masking
		System.out.print("Root Password: ");
		password = input.nextLine();
		System.out.print("Database Name: ");
		database = input.nextLine();
		
		
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://"+address+"/"+database+"?allowLoadLocalInfile=true",username,password);
			System.out.println("Connection Successful!");
			
			answeryn="";
			while(!(answeryn.equals("y")||answeryn.equals("n"))) {
				System.out.print("Do you need to import the dataset to the database? (y/n) ");
				answeryn = input.nextLine();
			}
			input.close();
			if(answeryn.equals("y")) {
				Statement s = connect.createStatement();
				if(hasTransactionTable(s)) {
					s.executeUpdate(dropTable);
					System.out.println("Dropped table: transactions.");
				}
				s.executeUpdate(makeTable);
				System.out.println("Created table: transactions.");
				s.executeUpdate(setGlobalTrue);
				s.executeQuery(loadQuery);
				System.out.println("Loaded table: transactions.");
				s.executeUpdate(setGlobalFalse);
				s.close();
			}
			
			DatabaseFields df = new DatabaseFields(TRANS_TABLE, ITEM_ID, ITEMNAME, TRANS_ID);
			System.out.println("Running Apriori algorithm...");
			ArrayList<SupportPair> pairs = AprioriAlgorithm.generateFrequentItemsets(MIN_SUPPORT, connect, df);

			for(SupportPair pair: pairs) {
				System.out.println(pair.idToNames(connect, df));
			}
			
			connect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static boolean hasTransactionTable(Statement s) throws SQLException {
		boolean res = false;
		ResultSet r = s.executeQuery("SHOW TABLES;");
		while(r.next()) {
			if(r.getString(1).equals("transactions")) res = true;
		}
		r.close();
		return res;
	}
	
	private static String doubledSlashes(String str) {
		String res="";
		String[] pathArray = str.split("\\\\");
		for(int i=0; i<=pathArray.length-2;i++) {
			res = res + pathArray[i] + "\\\\";
		}
		res = res + pathArray[pathArray.length-1];
		return res;
	}
}
