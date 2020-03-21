package goos7850;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {
		String DB_ADDRESS = "localhost";
		String DB_NAME = "assignment_2b";
		String DB_USER = "theo";
		String DB_PASS = "password123";
		
		String TRANS_TABLE = "transactions";
		String ITEMS_TABLE = "items";
		String ITEM_ID = "itemID";
		String ITEMNAME = "itemName";
		String TRANS_ID = "TID";
		
		double MIN_SUPPORT = 0.5;
		
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://"+DB_ADDRESS+"/"+DB_NAME,DB_USER,DB_PASS);
			DatabaseFields df = new DatabaseFields(ITEMS_TABLE, TRANS_TABLE, ITEM_ID, ITEMNAME, TRANS_ID);		
			ArrayList<SupportPair> pairs = AprioriAlgorithm.generateFrequentItemsets(MIN_SUPPORT, connect, df);
			
			for(SupportPair pair: pairs) {
				System.out.println(pair);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
