package kappai.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import kappai.model.ItemVO;

public class ItemDAO {
	
	public ItemVO AddItem (ItemVO itemVO) {
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			/*
			String sql = "INSERT INTO TB_ITEM_MASTER "
					+ "(USER_FIRST_NAME, USER_LAST_NAME, USER_LOGIN, USER_PASSWORD, EMAIL_ID, PHONE_NUMBER, USER_ADDRESS) "
					+ "VALUES ("+"'"+userVO.getFirstName()+"'"+","+"'"+userVO.getLastName()+"'"+","+"'"+userVO.getUsername()+"'"+","+"'"+userVO.getPassword()+"'"+","+"'"+userVO.getEmailId()+"'"+","+"'"+userVO.getPhoneNumber()+"'"+","+"'"+userVO.getAddress()+"'"+");";
				
					","+"'"+userVO.getEmailId()+"'"+","+"'"+userVO.getPhoneNumber()+"'"+","+"'"+userVO.getAddress()+"'"+
					
				*/	
					
			String sql = "INSERT INTO TB_ITEM_MASTER "
					+ "(ITEM_NAME, ITEM_CATEGORY, ITEM_BRAND, ITEM_PACKAGING_UNIT) "
					+ "VALUES ("+"'"+itemVO.getItemName()+"'"+","+"'"+itemVO.getCategory()+"'"+","+"'"+itemVO.getBrand()+"'"+","+"'"+itemVO.getPackagingUnit()+"'"+");";
					
					
			
			
			
			//String itemNameInsert = "INSERT INTO TB_ITEM_MASTER (ITEM_NAME) "
			//		+ "VALUES ("+"'"+itemVO.getItemName()+"'"+")";
			
			
			
			//String test = "INSERT INTO TB_ITEM_MASTER (ITEM_NAME, ITEM_CATEGORY, ITEM_BRAND, ITEM_PACKAGING_UNIT) VALUES ("+"'"+itemVO.getItemName()+"', '"+itemVO.getCategory()+"', '"+itemVO.getBrand()+"'+")"";
			
			//String test = "INSERT INTO TB_ITEM_MASTER (ITEM_NAME, ITEM_CATEGORY, ITEM_BRAND, ITEM_PACKAGING_UNIT) VALUES ("+"'"+itemVO.getItemName()+"', '"+itemVO.getCategory()+"', '"+itemVO.getBrand()+"'+")"";
			
			//System.out.println(test);
			System.out.println(sql);
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.executeUpdate();
			
			ps.close();
			//rs.close();
			conn.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return itemVO;
	}
	
	
	
	
	public ArrayList<ItemVO> getItemsList() {
		
		ArrayList<ItemVO> arrItems = new ArrayList<ItemVO>();
		
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");

			String sql = "SELECT * FROM TB_ITEM_MASTER";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				ItemVO itemVO = new ItemVO();
				
				itemVO.setItemID(rs.getString("ITEM_ID"));
				itemVO.setItemName(rs.getString("ITEM_NAME"));
				itemVO.setCategory(rs.getString("ITEM_CATEGORY"));
				itemVO.setBrand(rs.getString("ITEM_BRAND"));
				itemVO.setPackagingUnit(rs.getString("ITEM_PACKAGING_UNIT"));
				
				arrItems.add(itemVO);
				
			}
			
			//1 Create SQL
			//2 Prepare Statement
			//3 Make result set || execute query
			//4 Iterate result set using rs.next
			ps.close();
			rs.close();
			conn.close();
		
		} catch (Exception e) {
			try {
				conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			
		}
		
		return arrItems;
	}




	public void updateItem(ItemVO itemVO) {
		
		 try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			String sql = "UPDATE TB_ITEM_MASTER "
					+ "SET ITEM_NAME = ?, ITEM_CATEGORY = ?, ITEM_BRAND = ?, ITEM_PACKAGING_UNIT = ? "
					+ "WHERE ITEM_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			//ps.setInt(1, itemVO.getItemID());
			ps.setString(1, itemVO.getItemName());
			ps.setString(2, itemVO.getCategory());
			ps.setString(3, itemVO.getBrand());
			ps.setString(4, itemVO.getPackagingUnit());
			ps.setString(5, itemVO.getItemID());
			
			ps.executeUpdate();
			ps.close();
		//	rs.close();
			conn.close();
		
		} catch (Exception e) {
		
		e.printStackTrace();
		
		} 
		 
		 
		
	}




	public ItemVO updateRetrieveItem(ItemVO itemVO) {
		ItemVO itemVOOut = new ItemVO();
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			
			String sql = "SELECT ITEM_ID, ITEM_NAME, ITEM_CATEGORY, ITEM_BRAND, ITEM_PACKAGING_UNIT FROM TB_ITEM_MASTER WHERE ITEM_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setString(1, itemVO.getItemID());
			
			ResultSet rs = ps.executeQuery();
			
			
			while (rs.next()) {
				itemVOOut.setItemID(rs.getString(1));
				itemVOOut.setItemName(rs.getString(2));
				itemVOOut.setCategory(rs.getString(3));
				itemVOOut.setBrand(rs.getString(4));
				itemVOOut.setPackagingUnit(rs.getString(5));
			}
			
			System.out.println("itemVO in retrieveItem is " + itemVO);
			ps.close();
			rs.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return itemVOOut;
		// TODO Auto-generated method stub
		
	}


	public boolean deleteRetrieveItem (ItemVO itemVO) {
		int count = 0;

		ItemVO itemVOOut = new ItemVO();
		
		System.out.println("itemVO in deleteRetrieveItem is " + itemVOOut);
		
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			
			String sql = "SELECT ITEM_ID, ITEM_NAME, ITEM_CATEGORY, ITEM_BRAND, ITEM_PACKAGING_UNIT FROM TB_ITEM_MASTER WHERE ITEM_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setString(1, itemVO.getItemID());
			
			ResultSet rs = ps.executeQuery();
			
			
			while (rs.next()) {
				count++;
				/*itemVOOut.setItemID(rs.getInt(1));
				itemVOOut.setItemName(rs.getString(2));
				itemVOOut.setCategory(rs.getString(3));
				itemVOOut.setBrand(rs.getString(4));
				itemVOOut.setPackagingUnit(rs.getString(5));*/
			}
			
			
			
			
			ps.close();
			rs.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (count == 0) {
			return true;
		} else {
			return false;
		}
		//return itemVOOut;
	}

	public int deleteItem(ItemVO itemVO) {
		System.out.println("In ItemDAO deleteItem");
		int noOfRecordsDeleted = 0;
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			String sql = "DELETE FROM TB_ITEM_MASTER WHERE ITEM_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			System.out.println("Delete sql="+ sql + "ITEM_ID="+itemVO.getItemID());
			
			ps.setInt(1, Integer.parseInt(itemVO.getItemID()));
			
			
			//ps.setInt(1, itemVO.getItemID());
			
			noOfRecordsDeleted = ps.executeUpdate();
			System.out.println("noOfRecordsDeleted="+noOfRecordsDeleted);
			ps.close();
			//rs.close();
			conn.close();
		
		} catch (Exception e) {
		
			e.printStackTrace();
		
		} 
		
		return noOfRecordsDeleted;
		
	}
	
	
	
	
	
	
	
}
