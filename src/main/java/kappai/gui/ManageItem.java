package kappai.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ManageItem extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ManageItem frame = new ManageItem();
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
	public ManageItem() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnView = new JButton("View Items");
		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				ViewItem vi = new ViewItem();
				vi.setVisible(true);
				
			}
		});
		btnView.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnView.setBounds(21, 94, 161, 53);
		contentPane.add(btnView);
		
		JButton btnAdd = new JButton("Add Item");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				AddItem ai = new AddItem();
				ai.setVisible(true);
			
				
			}
		});
		btnAdd.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnAdd.setBounds(270, 94, 161, 53);
		contentPane.add(btnAdd);
		
		JButton btnDelete = new JButton("Delete Item");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		btnDelete.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnDelete.setBounds(270, 193, 161, 53);
		contentPane.add(btnDelete);
		
		JButton btnUpdate = new JButton("Update/Edit Item");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		btnUpdate.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		btnUpdate.setBounds(21, 193, 161, 53);
		contentPane.add(btnUpdate);
		
		JLabel lblManageItems = new JLabel("Manage Items");
		lblManageItems.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		lblManageItems.setBounds(145, 6, 206, 46);
		contentPane.add(lblManageItems);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				MainMenu mm = new MainMenu();
				mm.setVisible(true);
			}
		});
		btnBack.setBounds(168, 243, 117, 29);
		contentPane.add(btnBack);
	}

}
