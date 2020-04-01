package goos7850;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		/**
		 * Start import implementation
		 */
		String setGlobalTrue = "SET GLOBAL local_infile=true;";
		String setGlobalFalse = "SET GLOBAL local_infile=false;";
		String dropTable = "DROP TABLE tumors;";
		// Fields in table should match up to those in the file
		String makeTable = "CREATE TABLE tumors ( Radius DECIMAL(4, 2), Texture DECIMAL(4,2) );";

		
		String address;
		String username="root";
		String password;
		String database;
		String answeryn;		
		
		String localDir = doubledSlashes(System.getProperty("user.dir"));

		
		Scanner input = new Scanner(System.in);
		System.out.print("Server Address: ");
		address = input.nextLine();
		//TODO: Password masking
		System.out.print("Root Password: ");
		password = input.nextLine();
		System.out.print("Database Name: ");
		database = input.nextLine();
		// Generated using HeidiSQL
		String loadQuery = "LOAD DATA LOW_PRIORITY LOCAL INFILE '"+localDir+"\\\\tumors.csv' "
				+ "REPLACE INTO TABLE `"+database+"`.`tumors` CHARACTER SET utf8 FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY "
				+ "'\"' LINES TERMINATED BY '\\r\\n' (@ColVar0, @ColVar1) SET `Radius` = REPLACE(REPLACE(@ColVar0, ',', ''), '.', '.'), "
				+ "`Texture` = REPLACE(REPLACE(@ColVar1, ',', ''), '.', '.');";
		Connection connect;
		try {
			connect = DriverManager.getConnection("jdbc:mysql://"+address+"/"+database+"?allowLoadLocalInfile=true",username,password);
	
			System.out.println("Connection Successful!");
			
			answeryn="";
			while(!(answeryn.equals("y")||answeryn.equals("n"))) {
				System.out.print("Do you need to import the dataset to the database? (y/n) ");
				answeryn = input.nextLine();
			}
			input.close();
			if(answeryn.equals("y")) {
				Statement s = connect.createStatement();
				if(hasTumorsTable(s)) {
					s.executeUpdate(dropTable);
					System.out.println("Dropped table: tumors");
				}
				s.executeUpdate(makeTable);
				System.out.println("Created table: tumors");
				s.executeUpdate(setGlobalTrue);
				s.executeQuery(loadQuery);
				System.out.println("Loaded table: tumors");
				s.executeUpdate(setGlobalFalse);
				s.close();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/**
		 * End import implementation
		 */
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		ArrayList<KMCluster> clusters = new ArrayList<KMCluster>();
		
		try {
			File file = new File("sampledata.txt");
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] pointData = line.split(",");
				Point2D point = new Point2D.Double(Double.valueOf(pointData[0]), Double.valueOf(pointData[1]));
				points.add(point);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * End import
		 */
		
		int k=points.size()/3;
                
		//select cluster centers
		Random r;
		int index;
		boolean selected;
		ArrayList<Integer> centers= new ArrayList<Integer>();
		for (int i=0;i<k;i++) {
			selected = false;
			while (selected==false) {
				r = new Random();
				index = r.nextInt(k + 1);
                               
				if (!centers.contains(index)) {
					centers.add(index);
					selected = true;
				}
			}
		}
              
		KMCluster c = new KMCluster();
		for (int i=0;i<k;i++) {
			c = new KMCluster(points.get(centers.get(i)));
			clusters.add(c);
		}
		double closestDist;
		int closest;
		Point2D point;
                //assign points
		for (int i=0;i<points.size();i++) {
			point = points.get(i);
			closest=0;
                        //System.out.print(clusters.size());
			closestDist=clusters.get(0).distanceFromCenter(point);
			//find closest cluster
			for (int j=0;j<k;j++) {
				if(clusters.get(j).distanceFromCenter(point)<closestDist) {
					closest=j;
					closestDist=clusters.get(0).distanceFromCenter(point);
				}
			}
			clusters.get(closest).addPoint(point);
		}
	
		for (int i=0;i<5; i++){
                    //recalculate centers
                    for (int j=0;j<clusters.size(); j++){
                        clusters.get(j).setCenter();
                        clusters.get(j).clearPoints();
                    }
                    //assign points
                    for (int j=0;j<points.size();j++) {
			point = points.get(j);
			closest=0;
                        //System.out.print(clusters.size());
			closestDist=clusters.get(0).distanceFromCenter(point);
			//find closest cluster
			for (int m=0;m<k;m++) {
				if(clusters.get(m).distanceFromCenter(point)<closestDist) {
					closest=m;
					closestDist=clusters.get(0).distanceFromCenter(point);
				}
			}
			clusters.get(closest).addPoint(point);
		}
                }
		System.out.println("Variance C1: "+clusters.get(0).getVariance());
		System.out.println("Variance C2: "+clusters.get(1).getVariance());
		
		JFrame f = new JFrame("Test");
		f.setSize(700,500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(new GraphView(points, clusters));
		f.setVisible(true);
	}
	
	
	private static boolean hasTumorsTable(Statement s) throws SQLException {
		boolean res = false;
		ResultSet r = s.executeQuery("SHOW TABLES;");
		while(r.next()) {
			if(r.getString(1).equals("tumors")) res = true;
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
