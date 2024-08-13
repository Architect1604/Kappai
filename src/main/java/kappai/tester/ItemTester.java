package kappai.tester;

import kappai.dao.ItemDAO;
import kappai.model.ItemVO;

public class ItemTester {

	public static void main(String[] args) {
		
		ItemTester itemTester = new ItemTester();
		//System.out.println("Item test's success = " + itemTester.updateItemTester());
		System.out.println("Item test's success = " + itemTester.deleteItemTester());

	}
	
	
	public boolean updateItemTester () {
		
		boolean status = false;
		
		
		
		
		//ItemVO iv = new ItemVO();
		
		
		//Create vo test data
		ItemVO itemVO = new ItemVO();
		itemVO.setItemID("35");
		itemVO.setItemName("Orange Juice");
		itemVO.setCategory("Beverage");
		itemVO.setBrand("Tropicana");
		itemVO.setPackagingUnit("3L");
		
		//Create dao object
		ItemDAO itemDAO = new ItemDAO();
		
		//Call updateItem method
		//Call retrieveItem method
		//itemDAO.retrieveItem();
		
		System.out.println("itemVO contains: " + itemVO);
		System.out.println("Before itemDAO.updateItem(itemVO), itemDAO.retrieveItem() contains: " + itemDAO.updateRetrieveItem(itemVO));
		System.out.println("ItemVOInput="+ itemVO);
		itemDAO.updateItem(itemVO);

		System.out.println("After updateItem(itemVO): itemVO contains: " + itemVO);
		
		System.out.println("After updateItem(itemVO): itemDAO.retrieveItem(itemVO) contains: " + itemDAO.updateRetrieveItem(itemVO));
		
		
		
		
		//Compare retrieved Item VO with original item VO
		
		//System.out.println("Status is "+ status);
		ItemVO itemVOOut = itemDAO.updateRetrieveItem(itemVO); 
		System.out.println("ItemVOOutput="+ itemVOOut);
		
		if (itemVO.equals(itemVOOut)) {
			status = true;
		} else {
			status = false;
		}
		//If both vos match return true, else return false
		return status;

	}
	
	public boolean deleteItemTester() {
		
		boolean status = false;
		
		ItemVO itemVO = new ItemVO();
		itemVO.setItemID("35");
		itemVO.setItemName("Orange Juice");
		itemVO.setCategory("Beverage");
		itemVO.setBrand("Tropicana");
		itemVO.setPackagingUnit("1L");
		
		//Create dao object
		ItemDAO itemDAO = new ItemDAO();
		
		
		itemDAO.deleteItem(itemVO);
		
		if (itemDAO.deleteRetrieveItem(itemVO)==true) {
			status = true;
		} else {
			status = false;
		}
		return status;
	}

}
