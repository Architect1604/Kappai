package kappai.gui;

public class Main {
	
	public static void main (String args[]) {
		System.out.println("Now here in Main class");
		
		IDandPasswords idandPasswords = new IDandPasswords();
		
		
		LoginPage1 loginPage = new LoginPage1(idandPasswords.getLoginInfo());
		
		
		
		//loginPage.setVisible(true);
	}

}
