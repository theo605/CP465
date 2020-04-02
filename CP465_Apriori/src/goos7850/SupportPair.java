package goos7850;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
/**
 * SupportPair.java
 * @author Theodore Goossens
 * Container object for itemsets and their associated supports for convenience. When using AprioriAlgorithm.java or 
 * PartitionAlgorithm.java to generate frequent itemsets, it is an ArrayList of these objects that is returned.
 */
public class SupportPair {
	private Set<String> items;
	private int support;
	/**
	 * SupportPair(Set<String> i, int s)
	 * @param i: An itemset.
	 * @param s: The itemset's associated frequency in the database.
	 */
	public SupportPair(Set<String> i, int s) {
		items = new HashSet<String>();
		items.addAll(i);
		support = s;
	}
	/**
	 * SupportPair(SupportPair p)
	 * @param p: A SupportPair object to copy.
	 */
	public SupportPair(SupportPair p) {
		items = new HashSet<String>();
		items.addAll(p.items);
		support = p.support;
	}
	/**
	 * Set<String> getItems()
	 * @return: The itemset.
	 */
	public Set<String> getItems(){
		return new HashSet<String>(items);
	}
	/**
	 * int getSupport()
	 * @return: The itemset's associated frequency
	 */
	public int getSupport() {
		return support;
	}
	public Set<String> idToNames(Connection c) throws SQLException {
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
	 * Overwritten methods.
	 */
	public boolean equals(Object o) {

        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof SupportPair)) { 
            return false; 
        } 
        
        SupportPair p = (SupportPair) o;
        
        return this.items.containsAll(p.items);
	}
	/**
	 * 
	 */
	public String toString() {		
		return items+": "+support;
	}
}
