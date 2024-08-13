package kappai.model;

public class ItemVO {

	public String itemID;
	
	public String itemName;

	public String category;
		
	public String brand;
	
	public String packagingUnit;

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
		System.out.println("in equals");
		System.out.println(this);
		System.out.println(itemVO);
		
//		System.out.println
	//	if (this.getItemID()==itemVO.getItemID() && this.getItemName().equals(itemVO.getItemName())&&this.category.equals(itemVO.getCategory())&&this.brand.equals(itemVO.getBrand()) && this.packagingUnit.equals(itemVO.getPackagingUnit())) {
		if (this.getItemID()==itemVO.getItemID()) {	
		System.out.println("In if");
			return true;
		} else {
		
		return false;
		}
	}
	
}
