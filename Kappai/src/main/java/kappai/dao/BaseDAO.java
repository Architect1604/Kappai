package kappai.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import kappai.util.ConfigReader;

public class BaseDAO {
	
	public Connection getConnection() {
		
		Connection conn = null;
		
		try {
			
			//read the properties file to get parameters
			ConfigReader cr = new ConfigReader();
			
			conn = DriverManager.getConnection(cr.getJdbcUrl(), cr.getJdbcUsername(), cr.getJdbcPassword());
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return conn;
		
	}

}
