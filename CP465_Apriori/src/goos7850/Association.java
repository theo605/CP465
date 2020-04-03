package goos7850;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/**
 * Association.java
 * @author Theodore Goossens
 * A container class for an association made for an itemset ( {itemsA} => {itemsB} ). Also calculates the 
 * confidence and support for the rule.
 */
public class Association {
	Connection connect;
	
	private Set<String> predicate;
	private Set<String> preposition;
	private double support; 
	private double confidence;
	/**
	 * Constructor for the Association object.
	 * @param pred: The itemset serving as the predicate of the association.
	 * @param prep: The itemset serving as the preposition of the association.
	 * @param sup: The count of transactions that have the union of these itemsets.
	 * @param noTransactions: The total number of transactions in the database.
	 * @param c
	 */
	public Association(Set<String> pred, Set<String> prep, int sup, int noTransactions, Connection c) {

		connect = c;
		
		predicate = new HashSet<String>(pred);
		preposition = new HashSet<String>(prep);
		support = (double) sup / (double) noTransactions;
		try {
			calculateConfidence(c);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Calculates the confidence of this rule.
	 * @param c: connection to a MySQL database
	 * @throws SQLException
	 */
	private void calculateConfidence(Connection c) throws SQLException{
		int predCount;
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery("SELECT count(*) FROM ("+createIntersectionQuery(predicate)+") AS T;");
		r.next();
		predCount = r.getInt(1);
		r.close();
		confidence = (double) support/(double) predCount;
	}	
	/**
	 * String createIntersectionQuery(Set<String> set, DatabaseFields df)
	 * @param set: An itemset.
	 * @return A query for the intersection of TIDs that contain an item within the itemset.
	 */
	private static String createIntersectionQuery(Set<String> set) {
		Iterator<String> i = set.iterator();
		String nestedQuery = "SELECT DISTINCT Invoice FROM transactions WHERE StockCode=";
		String res = nestedQuery + "'"+i.next()+"'";
		while(i.hasNext()) {
			res = nestedQuery +"'"+ i.next()+"'" + " AND Invoice IN ("+res+")";
		}
		return res;
	}
	/**
	 * Grabs the names of the items associated with the product codes for printing output
	 * @param items: An itemset where the items are given as their product codes
	 * @param c: A MySQL database connection
	 * @return: The itemset with the product codes exchanged with the names of the items
	 * @throws SQLException
	 */
	public static Set<String> idToNames(Set<String> items, Connection c) throws SQLException {
		Set<String> res = new HashSet<String>();
		Statement s = c.createStatement();
		for(String itemID: items) {
			ResultSet r = s.executeQuery("SELECT DISTINCT Description FROM transactions WHERE StockCode='"+itemID+"';");
			r.next();
			res.add(r.getString(1));
			r.close();
		}
		s.close();
		return new HashSet<String>(res);
	}
	/**
	 * Overwritten methods
	 */
	public boolean equals(Object o) {
		boolean res = false;
		
		if(o instanceof Association) {
			Association a = (Association) o;
			if(a.predicate.equals(predicate) && a.preposition.equals(preposition) ) res=true;
		}
		return res;
	}
	
	public String toString() {
		try {
			return idToNames(predicate, connect)+" => "+idToNames(preposition, connect)+"\r\n"+"Support: "+support+"\r\n"+"Confidence: "+confidence;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
