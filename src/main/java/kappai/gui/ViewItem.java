package kappai.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import java.awt.Font;

public class ViewItem extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tblData;
	DefaultTableModel model;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewItem frame = new ViewItem();
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
	public ViewItem() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 545, 317);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 56, 533, 197);
		contentPane.add(scrollPane);
		
		tblData = new JTable();
		model = (DefaultTableModel) tblData.getModel(); 
		
		scrollPane.setViewportView(tblData);
		
		JLabel lblListOfItems = new JLabel("List of items");
		lblListOfItems.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		lblListOfItems.setBounds(183, 6, 155, 46);
		contentPane.add(lblListOfItems);
		
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				ManageItem mi = new ManageItem();
				mi.setVisible(true);
			}
		});
		btnBack.setBounds(196, 254, 117, 29);
		contentPane.add(btnBack);
		
		ItemDAO itemDAO = new ItemDAO();
		
		//model.setColumnIdentifiers(UserDAO.colName);
		ArrayList <ItemVO> itemsList = itemDAO.getItemsList();
		
		model.setColumnCount(4);
		model.setRowCount(itemsList.size());
		
		//Iterate array list
		//Get ItemVO
		//Set model values from ItemVO
		
		String [] arr = {"Item Name","Item Category","Item Brand","Item Packaging Unit"};
		
		
		model.setColumnIdentifiers(arr);
		
		for (int i=0; i<itemsList.size();i++) {
			System.out.println("In for loop, ItemVO: " + itemsList.get(i));	
			
			model.setValueAt(itemsList.get(i).getItemName(), i,0);
			model.setValueAt(itemsList.get(i).getCategory(), i,1);
			model.setValueAt(itemsList.get(i).getBrand(), i,2);
			model.setValueAt(itemsList.get(i).getPackagingUnit(), i,3);
			
			
		}
	}
}
