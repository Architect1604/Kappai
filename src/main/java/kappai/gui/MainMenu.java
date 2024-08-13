package kappai.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu frame = new MainMenu();
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
	public MainMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 317);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Main Menu");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		lblNewLabel.setBounds(158, 6, 139, 46);
		contentPane.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Frequently Ordered Items");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				
			}
		});
		btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnNewButton.setBounds(18, 94, 161, 53);
		contentPane.add(btnNewButton);
		
		JButton btnApproveOrTrack = new JButton("Approve or Track Order");
		btnApproveOrTrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				
			}
		});
		btnApproveOrTrack.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnApproveOrTrack.setBounds(267, 94, 161, 53);
		contentPane.add(btnApproveOrTrack);
		
		JButton btnManage = new JButton("Manage Items");
		btnManage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				ManageItem mi = new ManageItem();
				mi.setVisible(true);
				
				
			}
		});
		btnManage.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnManage.setBounds(18, 193, 161, 53);
		contentPane.add(btnManage);
		
		JButton btnAnalytics = new JButton("Analytics");
		btnAnalytics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				
				
				
			}
		});
		btnAnalytics.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnAnalytics.setBounds(267, 193, 161, 53);
		contentPane.add(btnAnalytics);
		
		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				LoginPage lp = new LoginPage();
				lp.setVisible(true);
			}
		});
		btnLogout.setBounds(167, 254, 117, 29);
		contentPane.add(btnLogout);
	}

}
