package kappai.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DBUserPassword {

	
	static Connection conn = null;
	static String url = "jdbc:mysql://localhost/grocerify";
	
	
	/*
	
	public static Connection getConnection (){
		
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(url, "root", "IronMan16");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		try {
			if (!conn.isValid(1000)) {
				conn = DriverManager.getConnection(url, "root", "IronMan16");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		return conn;
	}*/
	/*
	
	public static DefaultTableModel buildTableModel(ResultSet rs)
	    	throws SQLException {
	ResultSetMetaData metaData = rs.getMetaData();
	// names of columns
		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnName(column));
		}
	// data of the table
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
	    	Vector<Object> vector = new Vector<Object>();
	    	for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	        	vector.add(rs.getObject(columnIndex));
	    	}
	    	data.add(vector);
		}
	return new DefaultTableModel(data, columnNames);
	}
	*/
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
		try {
			
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");

			
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("select * from TB_USER");
			
			while (rs.next()) {
				System.out.println(rs.getString("USER_LOGIN")+ ", " + rs.getString("USER_PASSWORD"));
			}
			
			
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
	}

}
