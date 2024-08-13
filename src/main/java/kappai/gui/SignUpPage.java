package kappai.gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.sql.*;

public class SignUpPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtFirstName;
	private JTextField txtLastName;
	private JTextField txtEmail;
	private JTextField txtPhNumber;
	private JTextField txtUsername;
	private JTextField txtPassword;
	private JTextField txtConfirmPassword;
	private JTextField txtAddress;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignUpPage frame = new SignUpPage();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SignUpPage() {
				
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 670, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTitle = new JLabel("New User Registration");
		lblTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 36));
		lblTitle.setBounds(161, 6, 390, 59);
		contentPane.add(lblTitle);
		
		JLabel lblFirstName = new JLabel("First Name:");
		lblFirstName.setBounds(50, 90, 105, 16);
		contentPane.add(lblFirstName);
		
		JLabel lblLastName = new JLabel("Last Name:");
		lblLastName.setBounds(50, 137, 105, 16);
		contentPane.add(lblLastName);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(372, 90, 105, 16);
		contentPane.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(372, 137, 105, 16);
		contentPane.add(lblPassword);
		
		JLabel lblConfirmPassword = new JLabel("Confirm Password:");
		lblConfirmPassword.setBounds(372, 186, 155, 16);
		contentPane.add(lblConfirmPassword);
		
		JLabel lblEmailAddress = new JLabel("Email Address:");
		lblEmailAddress.setBounds(50, 186, 105, 16);
		contentPane.add(lblEmailAddress);
		
		JLabel lblPhoneNumber = new JLabel("Phone Number:");
		lblPhoneNumber.setBounds(50, 235, 105, 16);
		contentPane.add(lblPhoneNumber);
		
		JLabel lblResidentialAddress = new JLabel("Residential Address:");
		lblResidentialAddress.setBounds(372, 235, 169, 16);
		contentPane.add(lblResidentialAddress);
		
		txtFirstName = new JTextField();
		txtFirstName.setBounds(161, 85, 130, 26);
		contentPane.add(txtFirstName);
		txtFirstName.setColumns(10);
		
		txtLastName = new JTextField();
		txtLastName.setColumns(10);
		txtLastName.setBounds(161, 132, 130, 26);
		contentPane.add(txtLastName);
		
		txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(161, 181, 130, 26);
		contentPane.add(txtEmail);
		
		txtPhNumber = new JTextField();
		txtPhNumber.setColumns(10);
		txtPhNumber.setBounds(161, 230, 130, 26);
		contentPane.add(txtPhNumber);
		
		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		txtUsername.setBounds(521, 85, 130, 26);
		contentPane.add(txtUsername);
		
		txtPassword = new JTextField();
		txtPassword.setColumns(10);
		txtPassword.setBounds(521, 132, 130, 26);
		contentPane.add(txtPassword);
		
		txtConfirmPassword = new JTextField();
		txtConfirmPassword.setColumns(10);
		txtConfirmPassword.setBounds(521, 181, 130, 26);
		contentPane.add(txtConfirmPassword);
		
		txtAddress = new JTextField();
		txtAddress.setColumns(10);
		txtAddress.setBounds(521, 230, 130, 26);
		contentPane.add(txtAddress);
		
		
		
		
		final JButton btnCreateAccount = new JButton("Create Account");
		btnCreateAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				UserDAO userDAO = new UserDAO();
				UserVO userVO = new UserVO ();
				userVO.setFirstName(txtFirstName.getText());
				userVO.setLastName(txtLastName.getText());
				userVO.setUsername(txtUsername.getText());
				userVO.setPassword(txtPassword.getText());
				userVO.setConfirmPassword(txtConfirmPassword.getText());
				userVO.setAddress(txtAddress.getText());
				userVO.setPhoneNumber(txtPhNumber.getText());
				userVO.setEmailId(txtEmail.getText());

				
				userDAO.createUser(userVO);
				setVisible(false);
				LoginPage lp = new LoginPage();
				lp.setVisible(true);
				/*
				
				String firstName = txtFirstName.getText();
				String lastName = txtLastName.getText();
				String emailID = txtEmail.getText();
				String phoneNumber = txtPhNumber.getText();
				String username = txtUsername.getText();
				String password = txtPassword.getText();
				String confirmPassword = txtConfirmPassword.getText();
				String address = txtAddress.getText();
				
				int phLen = phoneNumber.length();
				int passLen = password.length();
				
				
				
				
				
				if (phLen !=8 ) {
					System.out.println("In phone number length check");
					JOptionPane.showMessageDialog(btnCreateAccount, "Enter a valid mobile number (consisting of 8 digits)");
				} else if (passLen<8) {
					System.out.println("In password length check");
					JOptionPane.showMessageDialog(btnCreateAccount, "Your password must be at least 8 characters long");
				} else if (!password.equals(confirmPassword)) {
					//System.out.println("Password is " + password + " and confirmed password is " + confirmPassword);
					JOptionPane.showMessageDialog(btnCreateAccount, "Your passwords don't match. Try again and please make sure you enter the same password twice.");
				}
				
				
				*/
				try {
					Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/grocerify", "root", "IronMan16");
					
					
					//TODO fix query, something might be wrong with it
					/*String query = "INSERT INTO TB_USER USER_FIRST_NAME, USER_LAST_NAME, USER_TYPE, USER_LOGIN, USER_PASSWORD, EMAIL_ID, PHONE_NUMBER, USER_ADDRESS) VALUES ('\" + USER_FIRST_NAME + \"','\" + USER_LAST_NAME + \"','\" + USER_TYPE + \"','\" + USER_LOGIN + \"','\" + USER_PASSWORD + \"','\" + EMAIL_ID + \"' + \"','\" + PHONE_NUMBER + \"','\" + USER_ADDRESS + \"')";
					
					
					Statement sta = conn.createStatement();
					
					int x = sta.executeUpdate(query);
					
					if (x==0) {
						JOptionPane.showMessageDialog(btnCreateAccount, "This already exists");
					} else {
						JOptionPane.showMessageDialog(btnCreateAccount, "Welcome " + firstName + ", your account has been created under the username" + username);

					}*/
					conn.close();
					
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				
				
				
				
				
				
			}
			/*
			public void duplicateEntry () {
				
			}*/
			
			
		});
		btnCreateAccount.setBounds(145, 295, 151, 29);
		contentPane.add(btnCreateAccount);
		
		JButton btnReset = new JButton("Reset");
		btnReset.setBounds(360, 295, 151, 29);
		contentPane.add(btnReset);
		
		JLabel lblExistingUserLogin = new JLabel("Existing User? Login");
		lblExistingUserLogin.setBounds(186, 351, 181, 16);
		contentPane.add(lblExistingUserLogin);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				LoginPage lp = new LoginPage();
				lp.setVisible(true);
			}
		});
		btnLogin.setBounds(360, 346, 151, 29);
		contentPane.add(btnLogin);
	
	
	}

	
}
