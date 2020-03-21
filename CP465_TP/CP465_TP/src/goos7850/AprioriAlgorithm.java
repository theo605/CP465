package goos7850;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * AprioriAlgorithm.java
 * @author Theodore Goossens
 * Class provides an implementation of the Apriori algorithm for mining frequent itemsets within a database.
 */
public class AprioriAlgorithm {
	
	/**
	 * ArrayList<SupportPair> generateFrequentItemsets(double support, Connection connect, DatabaseFields df) 
	 * @param support: Minimum support% of the itemsets mined (freq(itemset)/size of database > support => accepted itemset).
	 * @param connect: Connection object to a MySQL database.
	 * @param df: Database parameters, refer to DatabaseFields.java.
	 * @return An ArrayList object filled with SupportPair objects. These SupportPair objects contain an itemset and its 
	 * 		associated frequency.
	 * @throws SQLException if connection to database is lost or database parameters are incorrect.
	 */
	public static ArrayList<SupportPair> generateFrequentItemsets(double support, Connection connect, DatabaseFields df) throws SQLException{
		ArrayList<SupportPair> res = new ArrayList<SupportPair>();
		ArrayList<Set<String>> rawItemsets;
		int n, sup;
		
		Statement s = connect.createStatement();
		ResultSet r = s.executeQuery("SELECT count(*) FROM (SELECT DISTINCT "+df.getTIDAttrName()+" FROM "+df.getTransactionsTableName()+") AS T;");
		r.next();
		n = r.getInt(1);
		r.close();
		s.close();
		sup = (int) Math.ceil(support * ((double) n));
		
		ArrayList<SupportPair> pairs = generateInitialFreqItemsets(connect, df);
		prune(pairs, sup);
		while(!pairs.isEmpty()) {	
			res.addAll(pairs);
			rawItemsets = joinFreqItemsets(pairs);
			pairs = calculateSetFrequencies(rawItemsets, connect, df);
			prune(pairs, sup);
		}
		return new ArrayList<SupportPair>(res);
	}
	/**
	 * ArrayList<SupportPair> generateInitialFreqItemsets(Connection c, DatabaseFields df)
	 * @param c: Connection object to a MySQL database. 
	 * @param df: Database parameters, refer to DatabaseFields.java.
	 * @return ArrayList of SupportPair objects, containing all singleton itemsets mined from the database.
	 * @throws SQLException if connection to database is lost or database parameters are incorrect.
	 */
	private static ArrayList<SupportPair> generateInitialFreqItemsets(Connection c, DatabaseFields df) throws SQLException{
		ArrayList<SupportPair> res = new ArrayList<SupportPair>();	
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery("SELECT "+df.getItemNameAttrName()+", c FROM (SELECT "+df.getItemIDAttrName()+", count("+df.getItemIDAttrName()+") AS c FROM "+df.getTransactionsTableName()+" GROUP BY "+df.getItemIDAttrName()+" ORDER BY "+df.getItemIDAttrName()+") AS t, "+df.getItemsTableName()+" i WHERE i."+df.getItemIDAttrName()+"=t."+df.getItemIDAttrName()+";");
		Set<String> tempSet;
		while(r.next()) {
			tempSet = new HashSet<String>();
			tempSet.add(r.getString(1));
			res.add(new SupportPair(tempSet, r.getInt(2)));
		}
		r.close();
		s.close();
		return new ArrayList<SupportPair>(res);
	}
	/**
	 * ArrayList<Set<String>> joinFreqItemsets(ArrayList<SupportPair> itemsets)
	 * @param itemsets: An ArrayList k-itemsets with their associated frequencies
	 * @return An ArrayList of sets of strings. These are the k+1 itemsets that adhere the downward closure property.
	 */
	private static ArrayList<Set<String>> joinFreqItemsets(ArrayList<SupportPair> itemsets) {
		ArrayList<Set<String>> res = new ArrayList<Set<String>>();
		Set<String> intersection;
		Set<String>union = new HashSet<String>(); 
		
		for(int i=0; i<itemsets.size(); i++) {
			for(int k=i+1;k<itemsets.size();k++) {	
				intersection = new HashSet<>(itemsets.get(i).getItems());
				intersection.retainAll(itemsets.get(k).getItems());
				// Potential candidate if 2 k-itemsets share k-1 items
				if(intersection.size()==itemsets.get(i).getItems().size()-1) {
					union = new HashSet<String>(itemsets.get(i).getItems());
					union.addAll(itemsets.get(k).getItems());
					// must verify all subsets of potential k+1 itemset are large itemsets
					if(isCandidate(union, itemsets, k)) {						
						res.add(union);
						union = new HashSet<String>();
					}
				}
			}
		}
		return new ArrayList<Set<String>>(res);
	}
	/**
	 * isCandidate(Set<String> candidateItem, ArrayList<SupportPair> itemsets, int compareFrom)
	 * @param candidateItem: Potential k+1-itemset, made because 2 distinct k-itemsets shared k-1 items.
	 * @param itemsets: ArrayList of the k-itemsets
	 * @param compareFrom: It is only necessary to compare with the subsequent itemsets from where the potential candidate 
	 * 		was found. 
	 * @return returns true only if the k+1-itemset shares k items with k-1 other k-1-itemsets. All other subsets would 
	 * 		be within all smaller itemsets if this is true due the downward closure.
	 */
	private static boolean isCandidate(Set<String> candidateItem, ArrayList<SupportPair> itemsets, int compareFrom) {
		boolean isValid = false;
		Set<String> intersection;
		int count = 0;
		for(int i=compareFrom; i<itemsets.size(); i++) {
			intersection = new HashSet<String>(candidateItem);
			intersection.retainAll(itemsets.get(i).getItems());
			if(intersection.size()==candidateItem.size()-1) {
				count++;
			}
		}
		if(count == candidateItem.size()-1) {
			isValid = true;
		}
		return isValid;
	}
	/**
	 * calculateSetFrequencies(ArrayList<Set<String>> sets, Connection c, DatabaseFields df)
	 * @param sets: An ArrayList of k-itemsets
	 * @param c: Connection object to a MySQL database. 
	 * @param df: Database parameters, refer to DatabaseFields.java.
	 * @return Pairs all input k-itemsets with their frequencies within the database as a SupportPair object. Returns as a new 
	 * 		ArrayList of the objects
	 * @throws SQLException if connection to database is lost or database parameters are incorrect.
	 */
	private static ArrayList<SupportPair> calculateSetFrequencies(ArrayList<Set<String>> sets, Connection c, DatabaseFields df) throws SQLException{
		ArrayList<SupportPair> res = new ArrayList<SupportPair>();
		Statement s = c.createStatement();
		for(Set<String> set: sets) {
			ResultSet r = s.executeQuery("SELECT count(*) FROM ("+createIntersectionQuery(set, df)+") AS T;");
			r.next();
			res.add(new SupportPair(set, r.getInt(1)));
			r.close();
		} 
		s.close();
		return new ArrayList<SupportPair>(res);
	}		
	/**
	 * String createIntersectionQuery(Set<String> set, DatabaseFields df)
	 * @param set: An itemset.
	 * @param df: Database parameters, refer to DatabaseFields.java.
	 * @return A query for the intersection of TIDs that contain an item within the itemset.
	 */
	private static String createIntersectionQuery(Set<String> set, DatabaseFields df) {
		Iterator<String> i = set.iterator();
		String nestedQuery = "SELECT DISTINCT "+df.getTIDAttrName()+" FROM "+df.getTransactionsTableName()+" T, "+df.getItemsTableName()+" I WHERE T."+df.getItemIDAttrName()+"=I."+df.getItemIDAttrName()+" AND itemName=";
		String res = nestedQuery + "'"+i.next()+"'";
		while(i.hasNext()) {
			res = nestedQuery +"'"+ i.next()+"'" + " AND "+df.getTIDAttrName()+" IN ("+res+")";
		}
		return res;
	}
	/**
	 * void prune(ArrayList<SupportPair> in, int freq)
	 * @param in: An ArrayList of k-itemsets and their associated frequencies.
	 * @param freq: The minimum frequency the k-itemsets must have.
	 * Itemsets with frequency<minimum frequency are removed from the ArrayList.
	 */
	private static void prune(ArrayList<SupportPair> in, int freq) {
		for(int i=in.size()-1; i>=0; i--) {
			if(in.get(i).getSupport()<freq) {
				in.remove(i);
			}
		}
	}
}
