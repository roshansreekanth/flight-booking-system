/*
 * SQLQuery.java
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
public class SQLQuery {
    
    /** Creates a new instance of SQLQuery */
    public SQLQuery() {
    }
  
    public void queryStatement(Connection con, String table, ArrayList<Object> myList)
    {
    	try {
	    		String columnValues = "";
		    	
		    	for (int i = 0; i < myList.size(); i++)
				 {
					 if (i == myList.size() - 1)
					 {
						 columnValues += myList.get(i);
					 }
					 
					 else
					 {
		   			 	columnValues += myList.get(i) + ", ";
					 }
					 
				}
		    	
	            Statement stmt = con.createStatement();
		    	String query = "SELECT "  + columnValues + " FROM " + table;
		    	ResultSet rs = stmt.executeQuery(query);
		    	
		    	//https://coderwall.com/p/609ppa/printing-the-result-of-resultset
		    	ResultSetMetaData rsmd = rs.getMetaData();
		    	int columnsNumber = rsmd.getColumnCount();
		    	while (rs.next()) {
		    	    for (int i = 1; i <= columnsNumber; i++) {
		    	        if (i > 1) System.out.print(",  ");
		    	        String columnValue = rs.getString(i);
		    	        System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
		    	    }
		    	    System.out.println("");
		    	}
    	}
    	
    	catch(Exception io)
    	{
    		System.out.println("Error: " + io);
    	};
    }
    
    public boolean conditionalQuery(Connection con, String table, String conditionalTable, Object conditionalValue)  
    {
    	try {
    		
	    	if(conditionalValue instanceof String)
	    	{
	    		conditionalValue = "\'" + conditionalValue + "\'";
	    	}
            Statement stmt = con.createStatement();
	    	String query = "SELECT * FROM " + table + " WHERE " + conditionalTable + " = " + conditionalValue;
	    	ResultSet rs = stmt.executeQuery(query);
	    	
	    	//https://coderwall.com/p/609ppa/printing-the-result-of-resultset
	    	ResultSetMetaData rsmd = rs.getMetaData();
	    	int columnsNumber = rsmd.getColumnCount();
	    	while (rs.next()) {
	    	    for (int i = 1; i <= columnsNumber; i++) {
	    	        if (i > 1) System.out.print(",  ");
	    	        String columnValue = rs.getString(i);
	    	        System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
	    	    }
	    	    System.out.println("");
	    	}
	}
	
	catch(Exception io)
	{
		System.out.println("Error: " + io);
	};
	return true;
  }
    
    public boolean ifExists(Connection con, String table, String conditionalTable, Object conditionalValue)  
    {
    	try {
    		
	    	if(conditionalValue instanceof String)
	    	{
	    		conditionalValue = "\'" + conditionalValue + "\'";
	    	}
            Statement stmt = con.createStatement();
	    	String query = "SELECT * FROM " + table + " WHERE " + conditionalTable + " = " + conditionalValue;
	    	ResultSet rs = stmt.executeQuery(query);	    	
	    	if(!rs.next())
	    	{
	    		return false;
	    	}
	}
	
	catch(Exception io)
	{
		System.out.println("Error: " + io);
	};
	return true;
  }
    
    public boolean multipleExists(Connection con, String table, String conditionalTable, Object conditionalValue, String secondConditional, Object secondValue)  
    {
    	try {
    		
    		if(conditionalValue instanceof String)
	    	{
	    		conditionalValue = "\'" + conditionalValue + "\'";
	    	}
    		
    		if(secondValue instanceof String)
	    	{
	    		secondValue = "\'" + secondValue + "\'";
	    	}
    		
            Statement stmt = con.createStatement();
	    	String query = "SELECT * FROM " + table + " WHERE " + conditionalTable + " = " + conditionalValue + " AND " + secondConditional + " = " + secondValue;
	    	ResultSet rs = stmt.executeQuery(query);
	    	if(!rs.next())
	    	{
	    		return false;
	    	}
	}
	
	catch(Exception io)
	{
		System.out.println("Error: " + io);
	};
	return true;
  }
    
    public String getValue(Connection con, String searchQuery,  String table, String conditionalTable, Object conditionalValue)  
    {
    	try {
    		
	    	if(conditionalValue instanceof String)
	    	{
	    		conditionalValue = "\'" + conditionalValue + "\'";
	    	}
            
	    	String value = null;
	    	Statement stmt = con.createStatement();
	    	String query = "SELECT " + searchQuery + " FROM " + table + " WHERE " + conditionalTable + " = " + conditionalValue;
	    	ResultSet rs = stmt.executeQuery(query);
	    	
	    	while(rs.next())
	    	{
	    		value = rs.getString(searchQuery);
	    	}
	    	
	    	return value;
	}
	
	catch(Exception io)
	{
		System.out.println("Error: " + io);
	};
	return null;
  }
}
