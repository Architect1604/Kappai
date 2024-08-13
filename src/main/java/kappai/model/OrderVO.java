package kappai.model;

import java.sql.Date;
import java.util.ArrayList;
public class OrderVO {
	
	public int orderID;
	
	public Date deliveryDate;
	
	public String isApproved;
	
	public String isOrdered;
	
	public java.sql.Date orderDate;
	
	public String deliveryStatus;
	
	public ArrayList<OrderItemVO> orderItem = new ArrayList<OrderItemVO>();

	public ArrayList<OrderItemVO> getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(ArrayList<OrderItemVO> orderItem) {
		this.orderItem = orderItem;
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}

	public java.sql.Date getDeliveryDate() {
		return deliveryDate;
	}
	
	public void setDeliveryDate(java.sql.Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(String isApproved) {
		this.isApproved = isApproved;
	}

	public String getIsOrdered() {
		return isOrdered;
	}

	public void setIsOrdered(String isOrdered) {
		this.isOrdered = isOrdered;
	}

	public java.sql.Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(java.sql.Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public boolean equals (OrderVO orderVO ) {
		System.out.println("in equals");
		System.out.println(this);
		System.out.println(orderVO);
		
//		System.out.println
	//	if (this.getItemID()==itemVO.getItemID() && this.getItemName().equals(itemVO.getItemName())&&this.category.equals(itemVO.getCategory())&&this.brand.equals(itemVO.getBrand()) && this.packagingUnit.equals(itemVO.getPackagingUnit())) {
		if (this.getOrderID()==orderVO.getOrderID()) {	
		System.out.println("In if");
			return true;
		} else {
		
		return false;
		}
	}
	
	public String toString () {
		
		return "Hello World";
}
	
}
