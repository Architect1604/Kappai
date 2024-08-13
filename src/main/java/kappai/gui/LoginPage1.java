package kappai.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginPage1 extends JFrame implements ActionListener{
	
	
	JButton loginButton = new JButton("Login");
	JButton resetButton = new JButton("Reset");
	JTextField userIDField = new JTextField();
	JPasswordField userPasswordField = new JPasswordField();
	JLabel userIDLabel = new JLabel ("userID:");
	JLabel userPasswordLabel = new JLabel ("password:");
	JLabel messageLabel = new JLabel ("This is a test!!!");
	
	
	HashMap<String, String> logininfo;

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IDandPasswords idandPasswords = new IDandPasswords();
					
					
					LoginPage1 frame = new LoginPage1(idandPasswords.getLoginInfo());
					
					//LoginPage frame = new LoginPage();
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
	public LoginPage1(HashMap <String, String> loginInfoOriginal) {
		
		logininfo = loginInfoOriginal;
		
		//this.logininfo = logininfo;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 440, 532);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		
			
		userIDLabel.setBounds(50,100,75,25);
		userPasswordLabel.setBounds(50,150,75,25);
		
		loginButton.setBounds(125,200,100,25);
		loginButton.setFocusable(false);
		loginButton.addActionListener(this);
		
		resetButton.setBounds(225,200,100,25);
		resetButton.setFocusable(false);
		resetButton.addActionListener(this);
		
			
		contentPane.add(userIDLabel);
		contentPane.add(userPasswordLabel);
		contentPane.add(messageLabel);
		contentPane.add(userIDField);
		contentPane.add(userPasswordField);	
		contentPane.add(loginButton);
		contentPane.add(resetButton);
			
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane.setSize(420,420);
		contentPane.setLayout(null);
		contentPane.setVisible(true);
			
		
		messageLabel.setBounds(125,250,250,35);
		messageLabel.setFont(new Font(null, Font.ITALIC, 25));
		
		userIDField.setBounds(125,100,200,25);
		userPasswordField.setBounds(125,150,200,25);
		

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == resetButton) {
			userIDField.setText("");
			userPasswordField.setText("");
		}
		
		if (e.getSource() == loginButton) {
			String userID = userIDField.getText();
			String password = String.valueOf(userPasswordField.getPassword());
		
		
			if (logininfo.containsKey(userID)) {
				if (logininfo.get(userID).equals(password)) {
					messageLabel.setForeground(Color.green);
					messageLabel.setText("Login Successful");
					dispose();
					WelcomePage welcomePage = new WelcomePage (userID);
				} else {
					messageLabel.setForeground(Color.red);
					messageLabel.setText("Wrong Password");
				}
			} else {
				messageLabel.setForeground(Color.red);
				messageLabel.setText("Username not found");
			}
		}
		
		
		
		// TODO Auto-generated method stub
		
	}
	
}
