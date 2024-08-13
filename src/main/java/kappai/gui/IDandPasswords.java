package kappai.gui;

import java.util.HashMap;

public class IDandPasswords {
	
	
		
		HashMap <String, String> logininfo = new HashMap<String,String>();
		
		
		
		
		 IDandPasswords () {
			
			logininfo.put("abc","123");
			logininfo.put("def","456");
			logininfo.put("ghi","789");
		}
		
	protected HashMap getLoginInfo(){
		return logininfo;
	}

}
