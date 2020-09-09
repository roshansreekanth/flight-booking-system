/*
 * InsertRecord.java
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
public class InsertRecord {
    
    /** Creates a new instance of InsertRecord */
    public InsertRecord() {
    }
    
     public void insert(Connection con, String table, ArrayList<Object> myList)
     {
    	 try
    	 {
             Statement insertStmt = con.createStatement();
             
    		 String query = "INSERT into " + table + " values (";
    		 
    		 for (int i = 0; i < myList.size(); i++)
    		 {
    			 if (i == myList.size() - 1)
    			 {
    				 query += myList.get(i);
    			 }
    			 
    			 else
    			 {
        			 query += myList.get(i) + ", ";
    			 }
    			 
    			 
    		 }
    		 query += ")";
     	 	int res = insertStmt.executeUpdate(query);
     	 	System.out.println("The Number or records inserted is " +res);
            insertStmt.close();
    	 }
    	 	    	 	
    	 catch(Exception io)
    	 {
    		 System.out.println("Error: " + io);
    	 };
     }
}
