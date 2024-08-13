package kappai.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class AddItem extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtItem;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddItem frame = new AddItem();
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
	public AddItem() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAddItem = new JLabel("Add Item");
		lblAddItem.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		lblAddItem.setBounds(166, 6, 112, 46);
		contentPane.add(lblAddItem);
		
		JLabel lblNewLabel = new JLabel("Item Name:");
		lblNewLabel.setBounds(21, 95, 129, 16);
		contentPane.add(lblNewLabel);
		
		txtItem = new JTextField();
		txtItem.setBounds(21, 132, 399, 26);
		contentPane.add(txtItem);
		txtItem.setColumns(10);
		
		final JLabel lblNewLabel_1 = new JLabel("Please enter text, do not leave this field blank");
		lblNewLabel_1.setForeground(new Color(255, 3, 0));
		lblNewLabel_1.setBounds(21, 193, 399, 16);
		contentPane.add(lblNewLabel_1);
		lblNewLabel_1.setVisible(false);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				boolean empty = true;
				//while (empty == true) {
				ItemVO itemVO = new ItemVO();
				ItemDAO itemDAO = new ItemDAO();
				
				
					//System.out.println("In while loop, outside if");
					if (txtItem.getText().equals("")) {
						System.out.println("Empty stuff, no text passed");
						lblNewLabel_1.setVisible(true);
					} else {
						empty=false;
						itemVO.setItemName(txtItem.getText());

						itemDAO.AddItem(itemVO);
						
						
						setVisible(false);
						ManageItem mi = new ManageItem();
						mi.setVisible(true);
					}
			//	}
				
				
				
				//Add Methods and stuff here
				
				
				
			
				
			}
		});
		btnSave.setBounds(108, 237, 117, 29);
		contentPane.add(btnSave);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				setVisible(false);
				ManageItem mi = new ManageItem();
				mi.setVisible(true);
			
			}
		});
		btnBack.setBounds(237, 237, 117, 29);
		contentPane.add(btnBack);
		
		
	}
}
