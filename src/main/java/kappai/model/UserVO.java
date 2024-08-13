package kappai.model;

import java.sql.Date;

public class UserVO {

	
	//SignUpPage sup = new SignUpPage ();
	
	public String firstName;
	
	public String lastName;
	
	public String username;
	
	public String password;
	
	public String confirmPassword;
	
	public String emailId;
	
	public String phoneNumber;
	
	public String address;	

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
	
	
	
	
	public String toString () {		
			
		return "First Name = " + firstName + ", Last Name = " + lastName + ", Username = " + username + ", Password = " + password + ", Confirm Password = " + confirmPassword + ", Email ID = " + emailId + ", Phone Number = " + phoneNumber + ", Residential Address = " + address;
	}
		
	
	
}
