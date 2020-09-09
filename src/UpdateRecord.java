/*
 * UpdateRecord.java
 *
 * 
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.sql.*;

/**
 *
 * @author roshansreekanth
 */
public class UpdateRecord {
    
    /** Creates a new instance of UpdateRecord */
    public UpdateRecord() {
    }
 
     public void update(Connection con, String table, String columnName, Object columnValue, String primaryColumn, Object primaryValue)
     {
    	 try
    	 {
	    	 if(columnValue instanceof String)
	    	 {
	    		 columnValue = "\'" + columnValue + "\'";
	    	 }
	    	 
	    	 if(primaryValue instanceof String)
	    	 {
	    		 primaryValue = "\'" + primaryValue + "\'";
	    	 }
	    	 
	    	 Statement updateStmt = con.createStatement();
	    	 String updateSQL = "UPDATE " + table + " set " + columnName + " = " + columnValue + " WHERE " + primaryColumn + " = " + primaryValue;
	    	 
	    	 int res = updateStmt.executeUpdate(updateSQL);
	    	 System.out.println("The number of records updated is " + res);
	    	 updateStmt.close();
    	 }
    	 
    	 catch(Exception io)
    	 {
    		 System.out.println("Error: " + io);
    	 }
     }
    
}
