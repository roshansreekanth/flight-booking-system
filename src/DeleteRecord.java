/*
 * DeleteRecord.java
 *
 * 
 * 
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import java.sql.*;

/**
 *
 * @author roshansreekanth
 */
public class DeleteRecord {
    
    /** Creates a new instance of DeleteRecord */
    public DeleteRecord() {
    }
  
     public void delete(Connection con, String table, String row, Object rowValue)
     {
    	 try
    	 {
			 if(rowValue instanceof String)
			 {
				 rowValue = "\'" + rowValue + "\'";
			 }
    		
			 Statement deleteStmt = con.createStatement();
             String deleteSQL = " Delete from " + table + " where " + row + " = " + rowValue;
             int res = deleteStmt.executeUpdate(deleteSQL);
             System.out.println("The Number or records deleted is " +res);
             deleteStmt.close();
    	 }
    	 catch(Exception io)
    	 {
             System.out.println("error"+io);
    	 };
     }
    
}
