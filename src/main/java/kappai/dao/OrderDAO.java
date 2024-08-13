package kappai.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import kappai.model.OrderItemVO;
import kappai.model.OrderVO;

public class OrderDAO {
	
	public OrderVO getOrderItem(OrderVO orderVOInput) {
			
		OrderVO orderVO = new OrderVO();

			try {
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
	
				String sql = "SELECT ITEM_NAME, ITEM_CATEGORY, ITEM_BRAND, ITEM_PACKAGING_UNIT, ITEM_QUANTITY FROM TB_ITEM_MASTER im JOIN TB_ORDER_ITEM oi ON im.ITEM_ID = oi.ITEM_ID;";
				
				PreparedStatement ps = conn.prepareStatement(sql);
				
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					OrderItemVO oi = new OrderItemVO();
					
					
					oi.setItemName(rs.getString("ITEM_NAME"));
					oi.setCategory(rs.getString("ITEM_CATEGORY"));
					oi.setBrand(rs.getString("ITEM_BRAND"));
					oi.setPackagingUnit(rs.getString("ITEM_PACKAGING_UNIT"));
					oi.setQuantity(rs.getInt("ITEM_QUANTITY"));
					
									
					
					orderVO.getOrderItem().add(oi);
					
				}
				
				//1 Create SQL
				//2 Prepare Statement
				//3 Make result set || execute query
				//4 Iterate result set using rs.next
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return orderVO;
			
			//return orderVO.orderItem;
		}
	
	
	/*
	public ArrayList<OrderVO> viewOrder () {
		ArrayList<OrderVO> arrOrders = new ArrayList<OrderVO>();

		
		
		
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");

			String sql = "SELECT * FROM TB_ORDER";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				OrderVO orderVO = new OrderVO();

				//orderVO.setOrderID(rs.getInt(0));
				orderVO.setDeliveryDate(rs.getDate("DELIVERY_DATE"));
				orderVO.setIsApproved(rs.getString("IS_APPROVED"));
				orderVO.setIsOrdered(rs.getString("IS_ORDERED"));
				orderVO.setOrderDate(rs.getDate("ORDER_DATE"));
				orderVO.setDeliveryStatus(rs.getString("DELIVERY_STATUS"));
				
				arrOrders.add(orderVO);
				
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrOrders;
	}*/
	
	public void manageOrder (OrderVO orderVO) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");

			String sql = "UPDATE TB_ORDER "
					+ "SET DELIVERY_DATE = ?, IS_APPROVED = ?, IS_ORDERED = ?, ORDER_DATE = ?, DELIVERY_STATUS = ? "
					+ "WHERE ORDER_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			
			ps.setDate(1, orderVO.getDeliveryDate());
			ps.setString(2, orderVO.getIsApproved());
			ps.setString(3, orderVO.getIsOrdered());
			ps.setDate(4, orderVO.getOrderDate());
			ps.setString(5, orderVO.getDeliveryStatus());
			
			ps.setInt(6, orderVO.getOrderID());
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Completed manage order");
	}
	
	
	public void addItemToOrder (OrderVO orderVO) {
		
		/*
		 for loop
		 	insert into query
		 	prepare statement
		 	set blank values
		 	execute ps
		 */
		
		
		
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			
			for (int i=0; i<orderVO.getOrderItem().size();i++) {
				String sql = "INSERT INTO TB_ORDER_ITEM (ORDER_ID, ITEM_ID, ITEM_QUANTITY, ITEM_STATUS) VALUES (?,?,?,?);";
				
				PreparedStatement ps = conn.prepareStatement(sql);
				
				ps.setInt(1, orderVO.getOrderID());
				ps.setString(2, orderVO.getOrderItem().get(i).getItemID());
				ps.setInt(3, orderVO.getOrderItem().get(i).getQuantity());
				ps.setString(4, orderVO.getOrderItem().get(i).getItemStatus());
				
				ps.executeUpdate();
				
				conn.close();
			}		
		} catch (Exception e){
			e.printStackTrace();
		}
			
		
	}
	
	
	
	public void updateItemStatus (OrderVO orderVO) {
		
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");

			
			
			
			String sql = "UPDATE TB_ORDER "
					+ "SET DELIVERY_DATE = ?, IS_APPROVED = ?, IS_ORDERED = ?, ORDER_DATE = ?, DELIVERY_STATUS = ? "
					+ "WHERE ORDER_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			
			ps.setDate(1, orderVO.getDeliveryDate());
			ps.setString(2, orderVO.getIsApproved());
			ps.setString(3, orderVO.getIsOrdered());
			ps.setDate(4, orderVO.getOrderDate());
			ps.setString(5, orderVO.getDeliveryStatus());
			
			ps.setInt(6, orderVO.getOrderID());
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("Completed manage order");
	
		
		
	}
	/*
	public void updateOrder (OrderVO orderVO) {
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");

			String sql = "UPDATE TB_ORDER "
					+ "SET DELIVERY_DATE = ?, IS_APPROVED = ?, IS_ORDERED = ?, ORDER_DATE = ?, DELIVERY_STATUS = ? "
					+ "WHERE ORDER_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			
			ps.setDate(1, orderVO.getDeliveryDate());
			ps.setString(2, orderVO.getIsApproved());
			ps.setString(3, orderVO.getIsOrdered());
			ps.setDate(4, orderVO.getOrderDate());
			ps.setString(5, orderVO.getDeliveryStatus());
			
			ps.setInt(6, orderVO.getOrderID());
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}*/
	
	public OrderVO updateRetrieveOrder(OrderVO orderVO) {
		OrderVO orderVOOut = new OrderVO();
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			String sql = "SELECT ORDER_ID, DELIVERY_DATE, IS_APPROVED, IS_ORDERED, ORDER_DATE, DELIVERY_STATUS FROM TB_ORDER WHERE ORDER_ID = ?";
			//String sql = "SELECT ITEM_ID, ITEM_NAME, ITEM_CATEGORY, ITEM_BRAND, ITEM_PACKAGING_UNIT FROM TB_ITEM_MASTER WHERE ITEM_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setInt(1, orderVO.getOrderID());
			
			ResultSet rs = ps.executeQuery();
			
			
			while (rs.next()) {
				orderVOOut.setOrderID(rs.getInt(1));
				orderVOOut.setDeliveryDate(rs.getDate(2));
				orderVOOut.setIsApproved(rs.getString(3));
				orderVOOut.setIsOrdered(rs.getString(4));
				orderVOOut.setOrderDate(rs.getDate(5));
				orderVOOut.setDeliveryStatus(rs.getString(6));
			}
			
		//	System.out.println("itemVO in retrieveItem is " + );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return orderVOOut;
		// TODO Auto-generated method stub
		
	}
		/*
	public void deleteItem(OrderVO orderVO) {
			
			try {
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
				
				String sql = "DELETE FROM TB_ORDER WHERE ORDER_ID = ?";
				
				PreparedStatement ps = conn.prepareStatement(sql);
				
				ps.setInt(1, orderVO.getOrderID());
				
				
				ps.executeUpdate();
				
			
			} catch (Exception e) {
			
			e.printStackTrace();
			
			} 
			
		}*/
		
	public boolean deleteRetrieveItem (OrderVO orderVO) {
		int count = 0;

		OrderVO orderVOOut = new OrderVO();
		
		System.out.println("orderVO in deleteRetrieveItem is " + orderVOOut);
		
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
			
			
			String sql = "SELECT ORDER_ID, DELIVERY_DATE, IS_APPROVED, IS_ORDERED, ORDER_DATE, DELIVERY_STATUS FROM TB_ORDER WHERE ORDER_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setInt(1, orderVO.getOrderID());
			
			ResultSet rs = ps.executeQuery();
			
			
			while (rs.next()) {
				count++;
				/*itemVOOut.setItemID(rs.getInt(1));
				itemVOOut.setItemName(rs.getString(2));
				itemVOOut.setCategory(rs.getString(3));
				itemVOOut.setBrand(rs.getString(4));
				itemVOOut.setPackagingUnit(rs.getString(5));*/
			}
			
			
			
			
			
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
	
	
}
