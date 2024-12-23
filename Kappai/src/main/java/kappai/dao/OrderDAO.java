package kappai.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import kappai.model.OrderItemVO;
import kappai.model.OrderVO;

public class OrderDAO extends BaseDAO{
	
	//String connURL = "jdbc:mysql://localhost:3306/grocerify";
	//String dbuser = "root";
	//String dbpassword ="*********"; //Data hidden for security reasons
	
	
	// This method retrieves all items associated with a given orderID by querying the database.
	// It uses the DAO pattern to abstract the database logic from the rest of the system.
	public OrderVO getOrderAndItems(OrderVO orderVOInput) {
	    // Initialize a new OrderVO object to store the order and its items
		OrderVO orderVO = new OrderVO();

			try {
		        // Step 1: Establish a database connection using the utility method
				Connection conn = getConnection();
	
				// Step 2: Prepare the SQL query to fetch order items by joining order and item tables
		        // The query retrieves item details (name, category, brand, etc.) and the quantity from the order
		       
				String sql = "SELECT oi.ORDER_ITEM_ID, im.ITEM_ID, im.ITEM_NAME, im.ITEM_CATEGORY, im.ITEM_BRAND, im.ITEM_PACKAGING_UNIT, oi.ITEM_QUANTITY "
						+ "FROM TB_ITEM_MASTER im JOIN TB_ORDER_ITEM oi ON im.ITEM_ID = oi.ITEM_ID and oi.ORDER_ID = ?";
				
		        // Prepare the SQL statement with the provided order ID from orderVOInput
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, orderVOInput.getOrderID());
				
		        // Execute the SQL query and get the result set
				ResultSet rs = ps.executeQuery();
				
		        // Step 3: Loop through the result set and populate OrderItemVO objects with data
				while (rs.next()) {
		            // Create a new OrderItemVO object to hold individual item details
					OrderItemVO oi = new OrderItemVO();
					
		            // Extract each column value from the result set and set it in the OrderItemVO
					oi.setOrderItemID(rs.getString("ORDER_ITEM_ID"));
					oi.setItemID(rs.getString("ITEM_ID"));
					oi.setItemName(rs.getString("ITEM_NAME"));
					oi.setCategory(rs.getString("ITEM_CATEGORY"));
					oi.setBrand(rs.getString("ITEM_BRAND"));
					oi.setPackagingUnit(rs.getString("ITEM_PACKAGING_UNIT"));
					oi.setQuantity(rs.getInt("ITEM_QUANTITY"));
					
					
		            // Add the populated OrderItemVO to the OrderVO object			
					orderVO.addOrderItem(oi);
				}
				
		        // Step 4: Set the order ID in the OrderVO
				orderVO.setOrderID(orderVOInput.getOrderID());
		        // Close the prepared statement and the database connection
				ps.close();
				conn.close();
				
				
			} catch (Exception e) {
		        // Handle any exceptions that occur during the database interaction
				e.printStackTrace();
			}
		    // Return the fully populated OrderVO, which contains the order items
			return orderVO;
		}
	
	
	public OrderVO createOrder() {
		
		
		try {
			Connection conn = getConnection();
			String sql = "INSERT INTO TB_ORDER (ORDER_STATUS, ORDER_DATE) VALUES ('PENDING', SYSDATE())";
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.executeUpdate();

			ps.close();
			conn.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		//return the first order which was just created
		 
		return getOrderByStatusPending("PENDING");
			
		
	}
	
	public int updateOrderStatus (OrderVO orderVO) {
		
		System.out.println("OrderDAO.updateOrderStatus orderVO="+orderVO);
		
		int noOfRecordsUpdated=0;
		try {
			Connection conn = getConnection();

			String sql = "UPDATE TB_ORDER SET ORDER_STATUS =?";
		
			/*
			if (orderVO.getStatus().equals("ORDERED")) {
				//also update the ordered date
				sql += ", ORDER_DATE  =?";
			}
			*/
			
			//only append if delivery date is not null
			if (orderVO.getDeliveryDate() != null) {
			
				sql += ", DELIVERY_DATE  =?";
			}
			
			/*
			 * 
			 */
			
			sql += " WHERE ORDER_ID =?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			int psCounter = 0;
			ps.setString(++psCounter, orderVO.getStatus());
			
			/*
			if (orderVO.getStatus().equals("ORDERED")) {

				ps.setDate(++psCounter, java.sql.Date.valueOf(java.time.LocalDate.now()));
			}
			
			*/
			if (orderVO.getDeliveryDate() != null) {
				ps.setDate(++psCounter, orderVO.getDeliveryDate());
			}
			ps.setInt(++psCounter, orderVO.getOrderID());
			
			//if updated status = ORDERED, update all ITEM STAUS IN ORDER ITEMS TABLE TO ORDER
			if (orderVO.getStatus().equals("ORDERED")) {
				updateAllOrderItemsStatus( orderVO.getOrderID(), "ORDERED");
			}

			
			noOfRecordsUpdated = ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Completed updateOrderStatus() in OrderDAO");
		
		return noOfRecordsUpdated;
	}
	
	public int updateOrderStatusToDelivered (OrderVO orderVO) {
		
		System.out.println("OrderDAO.updateOrderStatus orderVO="+orderVO);
		
		int noOfRecordsUpdated=0;
		try {
			Connection conn = getConnection();

			String sql = "UPDATE TB_ORDER SET ORDER_STATUS =?";
		
			/*
			if (orderVO.getStatus().equals("ORDERED")) {
				//also update the ordered date
				sql += ", ORDER_DATE  =?";
			}
			*/
			
			//only append if delivery date is not null
			if (orderVO.getDeliveryDate() != null) {
			
				sql += ", DELIVERY_DATE  =?";
			}
			
			/*
			 * 
			 */
			
			sql += " WHERE ORDER_ID =?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			int psCounter = 0;
			ps.setString(++psCounter, orderVO.getStatus());
			
			/*
			if (orderVO.getStatus().equals("ORDERED")) {

				ps.setDate(++psCounter, java.sql.Date.valueOf(java.time.LocalDate.now()));
			}
			
			*/
			if (orderVO.getDeliveryDate() != null) {
				ps.setDate(++psCounter, orderVO.getDeliveryDate());
			}
			ps.setInt(++psCounter, orderVO.getOrderID());
			
			//if updated status = ORDERED, update all ITEM STAUS IN ORDER ITEMS TABLE TO ORDER
			if (orderVO.getStatus().equals("DELIVERED")) {
				updateAllOrderItemsStatus( orderVO.getOrderID(), "DELIVERED");
			}

			
			noOfRecordsUpdated = ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Completed updateOrderStatus() in OrderDAO");
		
		return noOfRecordsUpdated;
	}
	
	
	private int updateAllOrderItemsStatus(int orderID, String itemStatus) {
		int numberOfRecordsUpdated = 0;
		try {
			Connection conn = getConnection();
			String sql = "UPDATE TB_ORDER_ITEM SET ITEM_STATUS =  ? WHERE ORDER_ID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setString(1, itemStatus);
			ps.setInt(2, orderID);

			numberOfRecordsUpdated= ps.executeUpdate();

			ps.close();
			conn.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return numberOfRecordsUpdated;
		 			
	}

	// This method is part of OrderDAO and is responsible for adding an item to an order.
	// It performs an "upsert" operation (INSERT if the item doesn't exist, UPDATE if it does) based on a flag.
	public int addItemToOrder (OrderVO orderVO) {
	    // Log the incoming OrderVO for debugging
		System.out.println("OrderDAO addItemToOrder"+ orderVO);
		
	    // Variable to track how many items were added to the order
		int numberOfItemsAdded = 0;
		
	    // Flag to check if the item already exists in the order
		boolean itemExists = false;
		
		
		
		try {
	        // Step 1: Establish a connection to the database
			Connection conn = getConnection();

			// Step 2: Check if the item already exists in the order
	        // This query selects the item from TB_ORDER_ITEM based on ORDER_ID and ITEM_ID
			String sqlSelect = "SELECT * FROM TB_ORDER_ITEM WHERE ITEM_ID = ? AND ORDER_ID = ?";
	        // Prepare the SQL statement with the ORDER_ID and ITEM_ID from the OrderVO
			PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
			pstmt.setInt(1, Integer.parseInt(orderVO.getOrderItems().get(0).getItemID()));
			pstmt.setInt(2, orderVO.getOrderID());
	        // Execute the query to check if the item exists in the order
			ResultSet rs = pstmt.executeQuery();
	        // If the item exists, set the flag to true
			if (rs.next()) {
				itemExists = true;
			}
	        // Step 3: If the item does NOT exist, insert it into the order
			if (!itemExists) {
			
	            // SQL INSERT query to add the item to TB_ORDER_ITEM if it doesn't already exist
				String sql = "INSERT INTO TB_ORDER_ITEM (ORDER_ID, ITEM_ID, ITEM_QUANTITY, ITEM_STATUS) VALUES (?,?,?,?)";
				PreparedStatement ps = conn.prepareStatement(sql);
	            // Loop through the list of order items and insert each one into the database
				for (int i=0; i<orderVO.getOrderItems().size();i++) {
					
					ps.setInt(1, orderVO.getOrderID()); // Set the ORDER_ID
					ps.setString(2, orderVO.getOrderItems().get(i).getItemID()); // Set the ITEM_ID
					ps.setInt(3, orderVO.getOrderItems().get(i).getQuantity()); // Set the quantity
					ps.setString(4, orderVO.getOrderItems().get(i).getItemStatus()); // Set the item status
	                // Execute the insert statement and increment the count of added items
					numberOfItemsAdded += ps.executeUpdate();
					
				}	
	            // Close the PreparedStatement
				ps.close();
		
			} else {
				// Step 4: If the item already exists, update its quantity
	            // SQL UPDATE query to increase the quantity of the existing item in the order
				String sql = "UPDATE TB_ORDER_ITEM SET ITEM_QUANTITY = ITEM_QUANTITY + 1 WHERE ITEM_ID = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				
	            // Loop through the list of order items and update each one in the database
				for (int i=0; i<orderVO.getOrderItems().size();i++) {
					
					ps.setInt(1, Integer.parseInt(orderVO.getOrderItems().get(i).getItemID()));
					
	                // Execute the update statement and increment the count of added items
					numberOfItemsAdded += ps.executeUpdate();
				}
				// Close the PreparedStatement
				ps.close();
			}
			// Close the PreparedStatement, ResultSet, and Connection
			pstmt.close();
			conn.close();

		} catch (Exception e){
	        // Handle any exceptions that occur during database operations
			e.printStackTrace();
		}
	    // Return the number of items added or updated in the order
		return numberOfItemsAdded;	
	}
	
	/* Update item delivery status in order one at a time
	 * 
	 */
	
	public int updateItemForOrder (OrderVO orderVO) {
		
		System.out.println("OrderDAO updateItemForOrder"+ orderVO);
		
		int numberOfItemsAdded = 0;
		
		try {
			
			Connection conn = getConnection();
			
			String sql = "UPDATE TB_ORDER_ITEM SET ITEM_STATUS = ? WHERE ORDER_ITEM_ID = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
				
			ps.setInt(1, Integer.parseInt(orderVO.getOrderItems().get(0).getItemStatus()));
			ps.setInt(2, Integer.parseInt(orderVO.getOrderItems().get(0).getItemID()));
					
			numberOfItemsAdded += ps.executeUpdate();
			
			ps.close();
			conn.close();

		} catch (Exception e){
			e.printStackTrace();
		}
		
		return numberOfItemsAdded;	
	}
	
	public OrderVO getOrderByStatusPending(String orderstatus) {
		
		OrderVO orderVOOut = null;
		
		
		PreparedStatement ps;
		
		try {
			Connection conn = getConnection();
			
			String sql = "SELECT ORDER_ID, ORDER_STATUS, ORDER_DATE, DELIVERY_DATE FROM TB_ORDER "
					+ "WHERE ORDER_STATUS = ? ORDER BY ORDER_ID DESC";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, orderstatus);
			
			ResultSet rs = ps.executeQuery();
		
			while (rs.next()) {
				orderVOOut = new OrderVO();
				orderVOOut.setOrderID(rs.getInt("ORDER_ID"));
				orderVOOut.setDeliveryDate(rs.getDate("DELIVERY_DATE"));
				orderVOOut.setOrderDate(rs.getDate("ORDER_DATE"));
				orderVOOut.setStatus(rs.getString("ORDER_STATUS"));
			}
			
			ps.close();
			conn.close();
			
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println("getOrderByStatus orderVOOut:");		
		return orderVOOut;
		
	}

	/* public ArrayList <OrderVO> getOrderByStatus (String orderstatus) {
	 *  need to return ArrayList, populate by doing arraylist.add(OrderVOObject or wtv)
	 * OrderVO orderVOOut = null;
		
		Connection conn;
		PreparedStatement ps;
		
		try {
			conn = DriverManager.getConnection(connURL, dbuser, dbpassword);
			
			
	 * } catch (Exception e) {
	 * e.printStacktrace();
	 * }
	 */
		
	public ArrayList <OrderVO> getOrderByStatus (String orderstatus) {
		
		// Declare a variable to hold each OrderVO object and initialize an ArrayList to store multiple orders

		OrderVO orderVOOut = null;
		ArrayList <OrderVO> orders = new ArrayList <OrderVO>();
		
		PreparedStatement ps;
		
		try {
			// Obtain a connection to the database using the custom getConnection() method
			Connection conn = getConnection();
			
			// SQL query to retrieve order details for orders that are either 'ORDERED' or 'DELIVERED',
	        // and sort them by ORDER_ID in descending order
			String sql = "SELECT ORDER_ID, ORDER_STATUS, ORDER_DATE, DELIVERY_DATE FROM TB_ORDER "
					+ "WHERE ORDER_STATUS = 'ORDERED' OR ORDER_STATUS = 'DELIVERED' ORDER BY ORDER_ID DESC;";
			
			// Prepare the SQL statement
			ps = conn.prepareStatement(sql);
			
			// Execute the query and store the results in a ResultSet
			ResultSet rs = ps.executeQuery();
		
			// Iterate through each row in the ResultSet
			while (rs.next()) {
				// Create a new OrderVO object for each record in the result set
				orderVOOut = new OrderVO();
				
				// Set the properties of the OrderVO object using the values from the ResultSet
				orderVOOut.setOrderID(rs.getInt("ORDER_ID"));
				orderVOOut.setDeliveryDate(rs.getDate("DELIVERY_DATE"));
				orderVOOut.setOrderDate(rs.getDate("ORDER_DATE"));
				orderVOOut.setStatus(rs.getString("ORDER_STATUS"));
				
				// Add the populated OrderVO object to the list of orders
				orders.add(orderVOOut);
			}
			
			// Close the PreparedStatement and the database connection to free up resources
			ps.close();
			conn.close();
			
		} 
		catch (Exception e) {
			// Handle any exceptions that occur during the database operations
			e.printStackTrace();
		}
		
		// Return the list of orders
		return orders;
		
	}
	
	
	/*public OrderItemVO updatePastOrderStatus (OrderItemVO oi) {
	 * 
	 * Write sql
	 * 
	 * String sql = "UPDATE TB_ORDER_ITEM SET ITEM_STATUS = 'DELIVERED' WHERE ORDER_ITEM_ID = ?"
	 * 
	 * 
	 * Make prepared statement
	 * Ps.executeUpdate();
	 * 
	 * return oi
	 * 
	 * 
	 * }
	 */
	
	public int updateOrderItemStatus (int orderitemid) {
		
		int numberOfRowsUpdated = 0;
		try {
			
			Connection conn = getConnection();

			String sql = "UPDATE TB_ORDER_ITEM SET ITEM_STATUS = 'DELIVERED' WHERE ORDER_ITEM_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, orderitemid);
			
			numberOfRowsUpdated = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return numberOfRowsUpdated;
	}
	
	public boolean deleteOrderItem (int orderItemID) {
		int count = 0;

		OrderVO orderVOOut = new OrderVO();
		
		System.out.println("orderVO in deleteRetrieveItem is " + orderVOOut);
		
		
		try {
			Connection conn = getConnection();
			
			
			String sql = "DELETE FROM TB_ORDER_ITEM WHERE ORDER_ITEM_ID = ?";
			
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ps.setInt(1, orderItemID);
			//ps.setInt(2, itemID);
			
			ps.executeUpdate();

			
			ps.close();
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
	
	
}
