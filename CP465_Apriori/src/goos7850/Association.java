package goos7850;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Association {
	Connection connect;
	
	private Set<String> predicate;
	private Set<String> preposition;
	private double support; 
	private double confidence;
	
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
	
	private void calculateConfidence(Connection c) throws SQLException{
		int predCount;
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery("SELECT count(*) FROM ("+createIntersectionQuery(predicate)+") AS T;");
		r.next();
		predCount = r.getInt(1);
		r.close();
		confidence = (double) support/(double) predCount;
	}	
	
	private static String createIntersectionQuery(Set<String> set) {
		Iterator<String> i = set.iterator();
		String nestedQuery = "SELECT DISTINCT Invoice FROM transactions WHERE StockCode=";
		String res = nestedQuery + "'"+i.next()+"'";
		while(i.hasNext()) {
			res = nestedQuery +"'"+ i.next()+"'" + " AND Invoice IN ("+res+")";
		}
		return res;
	}
	
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
