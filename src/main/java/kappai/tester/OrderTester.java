package kappai.tester;

import java.sql.Date;
import java.util.ArrayList;

import kappai.dao.OrderDAO;
import kappai.model.OrderItemVO;
import kappai.model.OrderVO;

public class OrderTester {
	
	
	public static void main (String args[]) {
		
		OrderTester OT = new OrderTester();
		//System.out.println("viewOrderTester is " + OT.viewOrderTester());
		//System.out.println("viewOrderTester is " + OT.deleteOrderTester());
		//OT.updateOrderTester();
		//OT.updateOrderItem();
		OT.addItemToOrder();
		System.out.println("Kaam done");
		

	}
	
	
	public void addItemToOrder () {
		OrderDAO orderDAO = new OrderDAO();
		OrderVO orderVO = new OrderVO();
		OrderItemVO OI = new OrderItemVO();
		
		
		
		orderVO.setOrderID(3);
		OI.setItemID("51");
		OI.setQuantity(3);
		OI.setItemStatus("Not received");
		
		orderVO.getOrderItem().add(OI);
		
		orderDAO.addItemToOrder(orderVO);
		//return "Kaam ho gaya";
	}
	
	public void updateOrderItem () {
		OrderDAO orderDAO = new OrderDAO();
		OrderVO orderVO = new OrderVO();
		
		
		ArrayList <OrderItemVO> orderItem = orderDAO.getOrderItem(orderVO).getOrderItem();
			
		
		System.out.println(orderItem);
				
				
		
	}
	
	
	public void updateOrderTester () {
		//java.sql.Date deliveryDate = new java.sql.Date(123,1,1);
		//java.sql.Date orderDate = new java.sql.Date(123,1,5);

		boolean status = false;
		
		OrderVO orderVO = new OrderVO();
		
		//orderVO.setDeliveryDate(deliveryDate);
		orderVO.setOrderID(3);
		orderVO.setDeliveryDate(Date.valueOf("2023-06-03"));
		orderVO.setIsApproved("Y");
		orderVO.setIsOrdered("N");
		orderVO.setOrderDate(Date.valueOf("2023-03-28"));
		orderVO.setDeliveryStatus("N/O");
		
		
		
		OrderDAO orderDAO = new OrderDAO();
		
		
		orderDAO.manageOrder(orderVO); 
		
	/*	OrderVO orderVOOut = orderDAO.updateRetrieveOrder(orderVO); 
		System.out.println("OrderVOOutput="+ orderVOOut);

		
		
		
		if (orderVO.equals(orderVOOut)) {
			status = true;
		} else {
			status = false;
		}
		
		return status;*/
	}
	
	
	public boolean deleteOrderTester() {
			
			boolean status = false;
			
			OrderVO orderVO = new OrderVO();
			
			orderVO.setOrderID(2);
			orderVO.setDeliveryDate(Date.valueOf("2024-06-01"));
			orderVO.setIsApproved("Y");
			orderVO.setIsOrdered("Y");
			orderVO.setOrderDate(Date.valueOf("2024-05-31"));
			orderVO.setDeliveryStatus("Delivered");
			
			/*itemVO.setItemID(35);
			itemVO.setItemName("Orange Juice");
			itemVO.setCategory("Beverage");
			itemVO.setBrand("Tropicana");
			itemVO.setPackagingUnit("3L");*/
			
			//Create dao object
			OrderDAO orderDAO = new OrderDAO();
			
			
			//orderDAO.deleteOrder(orderVO);
			return status;
		}

}
