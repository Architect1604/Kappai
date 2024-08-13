package kappai.gui;

import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;

public class LoginPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField pwfPassword;
	JLabel lblWrongLogin = new JLabel("Access denied, you have entered incorrect login details");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginPage frame = new LoginPage();
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
	public LoginPage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 451, 444);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSignUp = new JButton("Sign Up");
		btnSignUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				setVisible(false);
				SignUpPage sup = new SignUpPage();
				sup.setVisible(true);
			}
		});
		btnSignUp.setBounds(170, 342, 117, 29);
		contentPane.add(btnSignUp);
		
		JLabel lblNewLabel = new JLabel("Username:");
		lblNewLabel.setBounds(79, 83, 114, 16);
		contentPane.add(lblNewLabel);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(79, 198, 114, 16);
		contentPane.add(lblPassword);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(160, 78, 219, 26);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);
		
		pwfPassword = new JPasswordField();
		pwfPassword.setBounds(160, 193, 219, 26);
		contentPane.add(pwfPassword);
		
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				UserVO userVO = new UserVO();
				UserDAO userDAO = new UserDAO();
				
				userVO.setUsername(txtUsername.getText());
				
				String strPassword = new String (pwfPassword.getPassword());
				userVO.setPassword(strPassword);
				
				//CreateSQL
				
				userDAO.loginUser(userVO);
				//System.out.println(userDAO.loginUser(userVO));
				
				
				if (userDAO.login==false) {
					lblWrongLogin.setVisible(true);
				} else {
					dispose();
					WelcomePage welcomePage = new WelcomePage (userVO.firstName);

				}
				
				
			
			}
		});
		btnLogin.setBounds(79, 271, 117, 29);
		contentPane.add(btnLogin);
		
		final JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == btnReset) {
					txtUsername.setText("");
					pwfPassword.setText("");
				}
				
				
				
			}
		});
		btnReset.setBounds(262, 271, 117, 29);
		contentPane.add(btnReset);
		
		JLabel lblLoginPage = new JLabel("Login Page");
		lblLoginPage.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		lblLoginPage.setBounds(160, 6, 139, 46);
		contentPane.add(lblLoginPage);
		
		JLabel lblNotAnExisting = new JLabel("Not an existing user? Sign up here");
		lblNotAnExisting.setBounds(128, 314, 224, 16);
		contentPane.add(lblNotAnExisting);
		
		lblWrongLogin.setVisible(false);
		lblWrongLogin.setForeground(Color.RED);
		lblWrongLogin.setBounds(46, 314, 355, 139);
		contentPane.add(lblWrongLogin);
		
		
	}
}
