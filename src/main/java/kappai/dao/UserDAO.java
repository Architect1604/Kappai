package kappai.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.time.LocalDate;

import kappai.model.UserVO;

public class UserDAO {

	boolean login;
	static String[] colName;
	
	
	
	public void createUser (UserVO userVO) {
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			
			//TODO fix query, something might be wrong with it
			//String query = "INSERT INTO TB_USER USER_FIRST_NAME, USER_LAST_NAME, USER_TYPE, USER_LOGIN, USER_PASSWORD, EMAIL_ID, PHONE_NUMBER, USER_ADDRESS) VALUES ('\" + USER_FIRST_NAME + \"','\" + USER_LAST_NAME + \"','\" + USER_TYPE + \"','\" + USER_LOGIN + \"','\" + USER_PASSWORD + \"','\" + EMAIL_ID + \"' + \"','\" + PHONE_NUMBER + \"','\" + USER_ADDRESS + \"')";
			
			
			
			String sql = "INSERT INTO TB_USER "
					+ "(USER_FIRST_NAME, USER_LAST_NAME, USER_LOGIN, USER_PASSWORD, EMAIL_ID, PHONE_NUMBER, USER_ADDRESS) "
					+ "VALUES ("+"'"+userVO.getFirstName()+"'"+","+"'"+userVO.getLastName()+"'"+","+"'"+userVO.getUsername()+"'"+","+"'"+userVO.getPassword()+"'"+","+"'"+userVO.getEmailId()+"'"+","+"'"+userVO.getPhoneNumber()+"'"+","+"'"+userVO.getAddress()+"'"+");";
					
			
			/*String sql = "INSERT INTO TB_USER "
					+ "(USER_FIRST_NAME, USER_LAST_NAME, USER_LOGIN, USER_PASSWORD, EMAIL_ID, PHONE_NUMBER, USER_ADDRESS) "
					+ "VALUES ("+"'"+userVO.getFirstName()+"'"+","+userVO.getLastName()+", "+userVO.getUsername()+", "+userVO.getPassword()+", "+userVO.getEmailId()+", "+userVO.getPhoneNumber()+", "+userVO.getAddress()+");";
			*/
			
			
			
			
			System.out.println(sql);
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			
			
			
			ps.executeUpdate();
			/*
			SignUpPage sup = new SignUpPage ();
						
			int x = ps.executeUpdate();
						
						if (x==0) {
							sup.duplicateEntry();
						}*/
						
			/*Statement sta = conn.createStatement();
			
			int x = sta.executeUpdate(query);
			
			if (x==0) {
				JOptionPane.showMessageDialog(btnCreateAccount, "This already exists");
			} else {
				JOptionPane.showMessageDialog(btnCreateAccount, "Welcome " + firstName + ", your account has been created under the username" + username);

			}*/
			conn.close();
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		System.out.println("In method createUser(), userVO="+userVO);
		
		
		
	}
	
	/*
	public void loginUser (UserVO userVO) {
		
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");

												
			String dbUsername = "SELECT USER_LOGIN FROM TB_USER WHERE USER_LOGIN = ";
						
			
			
			PreparedStatement ps = conn.prepareStatement(dbUsername + userVO.username);
			ps.executeUpdate();
			
			
			
			
		} catch (Exception e) {
			
		}
		*/
	
	/* Method to check successfuel login
	 * Accepts userVO as an input parameter
	 * Returns UserVO with the set username if successful
	 * Returns UserVO with null user name if failed
	 * 
	 */
	
	public UserVO loginUser (UserVO userVO) {
		int ghusa = 0;
		try {

			System.out.println(userVO);
			//create connection
			
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");

			//create SQL
			String str = "SELECT USER_LOGIN, USER_FIRST_NAME "
					+ "FROM TB_USER "
					+ "WHERE USER_LOGIN = ? AND USER_PASSWORD = ?";
			//create prepared statement
			System.out.println(str);
			PreparedStatement ps = conn.prepareStatement(str);
			ps.setString(1, userVO.getUsername());
			ps.setString(2, userVO.getPassword());
			//execute query
			ResultSet rs = ps.executeQuery();
			//iterate result set
				//if result set is of the size zero, throw login failed exception
			while (rs.next()) {
				ghusa++;
				System.out.println("Now here in while loop");

				userVO.setFirstName(rs.getString("USER_FIRST_NAME"));		
			}
			
			if (ghusa==0) {
				System.out.println("Wrong login");
				login = false;
				userVO = null;
				/*
				loginPage.lblWrongLogin.setVisible(true);
				loginPage.dispose();
				RejectedLogin rejectedLogin = new RejectedLogin();*/
			} else {
				login=true;
				//dispose not working
				//loginPage.dispose();
				//WelcomePage welcomePage = new WelcomePage (userVO.firstName);
				
			}
			
			
			/*
			if (rs.next()==false) {
				System.out.println("Wrong login");
				
			}*/
				//else, set first name value in userVO
			//return userVO
			
			
			//	ResultSet rs = ;
			
			conn.close();
			System.out.println(userVO);

			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		return userVO;
	}
	
	/* method to check login
	 * returns true if login successful
	 * else return falose
	 */
	
	
	/*public UserVO AddItem (UserVO userVO) {
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			
			String sql = "INSERT INTO TB_ITEM "
					+ "(USER_FIRST_NAME, USER_LAST_NAME, USER_LOGIN, USER_PASSWORD, EMAIL_ID, PHONE_NUMBER, USER_ADDRESS) "
					+ "VALUES ("+"'"+userVO.getFirstName()+"'"+","+"'"+userVO.getLastName()+"'"+","+"'"+userVO.getUsername()+"'"+","+"'"+userVO.getPassword()+"'"+","+"'"+userVO.getEmailId()+"'"+","+"'"+userVO.getPhoneNumber()+"'"+","+"'"+userVO.getAddress()+"'"+");";
			
			
			
			String itemNameInsert = "INSERT INTO TB_ITEM_MASTER (ITEM_NAME) "
					+ "VALUES ("+"'"+userVO.getItem()+"'"+")";
			
			
			
			System.out.println(itemNameInsert);
			
			PreparedStatement ps = conn.prepareStatement(itemNameInsert);
			
			ps.executeUpdate();
			
			conn.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return userVO;
	}
	*/
	
	
	/*
	public UserVO viewItem (UserVO userVO) {
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			String sql = "SELECT * FROM TB_ITEM_MASTER";
			
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			
			ViewItem vi = new ViewItem();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int cols = rsmd.getColumnCount();
			colName = new String[cols];
			for (int i=0;i<cols; i++) {
				colName[i]=rsmd.getColumnName(i+1);
				
			}
			String itemName;
			while (rs.next()) {
				itemName=rs.getString(1);
				String[] items = {itemName};
				vi.model.addRow(items);
			}
			
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return userVO;
	}*/
	
}
