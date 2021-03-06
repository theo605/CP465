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
	 * Prints the associations that can be made from the frequent itemsets, as wells as their support and
	 * confidence.
	 * @param freqItemsets: An ArrayList of the generated frequent itemsets
	 * @param c: A MySQL connection
	 * @throws SQLException: Connection to the database was lost.
	 */
	public static void printAssociations(ArrayList<SupportPair> freqItemsets, Connection c) throws SQLException {
		int noTransactions;
		
		try {
			Statement s = c.createStatement();
			ResultSet r  = s.executeQuery("SELECT COUNT(DISTINCT Invoice) FROM transactions;");
			r.next(); noTransactions = r.getInt(1);
			r.close();
			s.close();		
			ArrayList<Association> associations;
			System.out.println("---------------------------");
			for(SupportPair pair: freqItemsets) {
				if(pair.getItems().size()>1) {
					System.out.println("Itemset: "+pair.idToNames(c));
					associations = generateRules(pair, noTransactions, c);
					for(Association rule: associations) {
						System.out.println(rule);
						System.out.println();
					}
					System.out.println("---------------------------");
				}
			
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Subroutine to help create the rules for a particular freq. itemset.
	 * @param itemset: An itemset (SupportPair object)
	 * @param noTransactions: Total number of transactions in the database.
	 * @param c: A MySQL connection.
	 * @return: An ArrayList of the associations made from this itemset.
	 */
	private static ArrayList<Association> generateRules(SupportPair itemset, int noTransactions, Connection c){
		int sup = itemset.getSupport();
		ArrayList<Association> res = new ArrayList<Association>();
		Set<String> emptySet = new HashSet<String>();
		generateRulesAux(res, itemset.getItems(), emptySet, sup, noTransactions, c);
		return new ArrayList<Association>(res);
	}
	
	private static void generateRulesAux(ArrayList<Association> result, Set<String> predicate, Set<String> preposition,int sup, int noTransactions, Connection c) {
		if(!predicate.isEmpty()) {
			Set<String> pred;
			Set<String> prep;
			Association a;
			for(String item: predicate) {
				prep = new HashSet<String>(preposition);
				pred = new HashSet<String>(predicate);
				prep.add(item);
				pred.remove(item);
				a = new Association(prep, pred, sup, noTransactions, c);
				if(!pred.isEmpty() && !result.contains(a)) result.add(a);
				generateRulesAux(result, pred, prep, sup, noTransactions, c);
			}
		}
	}
	/**
	 * ArrayList<SupportPair> generateFrequentItemsets(double support, Connection connect, DatabaseFields df) 
	 * @param support: Minimum support% of the itemsets mined (freq(itemset)/size of database > support => accepted itemset).
	 * @param connect: Connection object to a MySQL database.
	 * @param df: Database parameters, refer to DatabaseFields.java.
	 * @return An ArrayList object filled with SupportPair objects. These SupportPair objects contain an itemset and its 
	 * 		associated frequency.
	 * @throws SQLException if connection to database is lost or database parameters are incorrect.
	 */
	public static ArrayList<SupportPair> generateFrequentItemsets(double support, Connection connect) throws SQLException{
		ArrayList<SupportPair> res = new ArrayList<SupportPair>();
		ArrayList<Set<String>> rawItemsets;
		int n, sup;
		
		Statement s = connect.createStatement();
		ResultSet r = s.executeQuery("SELECT count(DISTINCT Invoice) FROM transactions;");
		r.next();
		n = r.getInt(1);
		r.close();
		s.close();
		sup = (int) Math.ceil(support * ((double) n));
		
		ArrayList<SupportPair> pairs = generateInitialFreqItemsets(connect);
		prune(pairs, sup);
		while(!pairs.isEmpty()) {	
			res.addAll(pairs);
			rawItemsets = joinFreqItemsets(pairs);
			pairs = calculateSetFrequencies(rawItemsets, connect);
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
	private static ArrayList<SupportPair> generateInitialFreqItemsets(Connection c) throws SQLException{
		ArrayList<SupportPair> res = new ArrayList<SupportPair>();	
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery("SELECT StockCode, COUNT(Invoice) FROM transactions GROUP BY StockCode");
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
	private static ArrayList<SupportPair> calculateSetFrequencies(ArrayList<Set<String>> sets, Connection c) throws SQLException{
		ArrayList<SupportPair> res = new ArrayList<SupportPair>();
		Statement s = c.createStatement();
		for(Set<String> set: sets) {
			ResultSet r = s.executeQuery("SELECT count(*) FROM ("+createIntersectionQuery(set)+") AS T;");
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
