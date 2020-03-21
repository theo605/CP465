package goos7850;
/**
 * DatabaseFields.java
 * @author Theodore Goossens
 * A wrapping object, made to contain information about the database schema. This serves to reduce arguments through the
 * subroutines in AprioriAlgorithm.java and PartitionAlgorithm.java.
 */
public class DatabaseFields {
	private String itemsTable;
	private String transactionsTable;
	private String itemIDAttr;
	private String itemNameAttr;
	private String TIDAttr;
	/**
	 * DatabaseFields(String it, String tt, String iid, String in, String tid) 
	 * @param it: The name of the table where items are stored in the database.
	 * @param tt: The name of the table where transactions are stored in the database.
	 * @param iid: The name of the item ID attribute.
	 * @param in: The name of the item name attribute.
	 * @param tid: The name of the transaction ID attribute.
	 */
	public DatabaseFields(String it, String tt, String iid, String in, String tid) {
		itemsTable = it;
		transactionsTable = tt;
		itemIDAttr = iid;
		itemNameAttr = in;
		TIDAttr = tid;
	}
	/**
	 * String getItemsTableName()
	 * @return: The name of the table where items are stored in the database.
	 */
	public String getItemsTableName() {
		return itemsTable;
	}
	/**
	 * String getTransactionsTableName()
	 * @return: The name of the table where transactions are stored in the database.
	 */
	public String getTransactionsTableName() {
		return transactionsTable;
	}
	/**
	 * String getItemIDAttrName() 
	 * @return: The name of the item ID attribute.
	 */
	public String getItemIDAttrName() {
		return itemIDAttr;
	}
	/**
	 * String getItemNameAttrName()
	 * @return: The name of the item name attribute.
	 */
	public String getItemNameAttrName() {
		return itemNameAttr;
	}
	/**
	 * String getTIDAttrName()
	 * @return: The name of the transaction ID attribute.
	 */
	public String getTIDAttrName() {
		return TIDAttr;
	}
}
