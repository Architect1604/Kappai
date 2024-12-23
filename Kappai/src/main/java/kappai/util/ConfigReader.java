package kappai.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConfigReader {
	
	private final String propertiesFilePath = "src/main/resources/init.properties";


	private Properties properties;

	public ConfigReader() throws IOException {
	        properties = new Properties();
	        try (FileInputStream input = new FileInputStream(propertiesFilePath)) {
	            properties.load(input);
	        }
	    }

	public String getJdbcUrl() {
		return properties.getProperty("jdbc.url");
	}

	public String getJdbcUsername() {
		return properties.getProperty("jdbc.username");
	}

	public String getJdbcPassword() {
		return properties.getProperty("jdbc.password");
	}
	
	public String getSolaceUrl() {
		return properties.getProperty("solace.url");
	}

	public String getSolaceVPN() {
		return properties.getProperty("solace.vpn");
	}

	public String getSolaceUsername() {
		return properties.getProperty("solace.username");
	}

	public String getSolacePassword() {
		return properties.getProperty("solace.password");
	}

	
	
	public Connection getJdbcConnection() throws SQLException {
		String url = getJdbcUrl();
		String username = getJdbcUsername();
		String password = getJdbcPassword();
		return DriverManager.getConnection(url, username, password);
	}

	public static void main(String[] args) {
		try {
			ConfigReader configReader = new ConfigReader();

			// Test JDBC connection
			try (Connection connection = configReader.getJdbcConnection()) {
				System.out.println("JDBC Connection successful!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Solace settings,  url="+ configReader.getSolaceUrl());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
