package kappai.model;
import java.util.ArrayList;
public class OrderVO {
	
	private int orderID;
	
	private String status;
	
	private java.sql.Date orderDate;
	
	private java.sql.Date deliveryDate;
		
	private ArrayList<OrderItemVO> arrOrderItems = new ArrayList<OrderItemVO>();
	public ArrayList<OrderItemVO> getOrderItems() {
		return arrOrderItems;
	}
	public void setOrderItems(ArrayList<OrderItemVO> orderItems) {
		this.arrOrderItems = orderItems;
	}
	
	public OrderItemVO getOrderItem(String orderItemId) {
		
		OrderItemVO orderItemVO = new OrderItemVO();
		orderItemVO.setItemID(orderItemId);
		
		return this.arrOrderItems.get(this.arrOrderItems.indexOf(orderItemVO));
	}
	
	public void addOrderItem(OrderItemVO orderItemVO) {
		
		 this.arrOrderItems.add(orderItemVO);
	}
	public int getOrderID() {
		return orderID;
	}
	public void setStatus(String status) {
		
		 this.status = status;
	}
	public String getStatus() {
		return this.status;
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
	public boolean getIsApproved() {
		
		if (this.status.equals("APPROVED")) {
			return true;
		}
		return false;
	}
	public boolean getIsOrdered() {
		if (this.status.equals("ORDERED")) {
			return true;
		}
		return false;
	}
	public java.sql.Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(java.sql.Date orderDate) {
		this.orderDate = orderDate;
	}
	public boolean equals (OrderVO orderVO ) {
		System.out.println("in equals");
		System.out.println(this);
		System.out.println(orderVO);
		
		if (this.getOrderID()==orderVO.getOrderID()) {	
		System.out.println("In if");
			return true;
		} else {
		
		return false;
		}
	}
	
	public String toString () {
		
		String orderString = "OrderVO: orderID="+orderID+" status="+status+" orderDate="+orderDate+ " deliveryDate="+deliveryDate;
		
		orderString +="\nItems:";
		
		for (int i=0;i<this.arrOrderItems.size(); i++) {
			orderString += "Item"+ i +": "+ this.getOrderItems().get(i).toString() + "\n";
		}
		
		return orderString;
}
	
}
