package kappai.model;
public class OrderItemVO extends ItemVO{
	//includes all the properties of ItemVO Class, as well as some unique to OrderItemVO
	
	public int quantity;
	
	public String orderItemID;
	
	public String itemStatus;
	//all the accessor and mutator methods for each of the properties
	
	public String getItemStatus() {
		return itemStatus;
	}
	public String getOrderItemID() {
		return orderItemID;
	}
	public void setOrderItemID(String orderItemID) {
		this.orderItemID = orderItemID;
	}
	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
