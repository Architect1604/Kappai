package kappai.model;
public class ItemVO {
	private String itemID;
	
	private String itemName;
	private String category;
		
	private String brand;
	
	private String packagingUnit;
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getPackagingUnit() {
		return packagingUnit;
	}
	public void setPackagingUnit(String packagingUnit) {
		this.packagingUnit = packagingUnit;
	}
	
	public String toString () {
			
			return "ItemID = " + itemID + ", Name = " + itemName + ", Category = " + category + ", Brand = " + brand +  ", Packaging Unit = " + packagingUnit;
	}
	
	
	public boolean equals (ItemVO itemVO ) {
			
		if (this.getItemID()==itemVO.getItemID()) {	
				return true;
			} else {
			
			return false;
			}
		}
	
}
