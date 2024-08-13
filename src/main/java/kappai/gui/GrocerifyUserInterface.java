package kappai.gui;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

public class GrocerifyUserInterface extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable RENAMETABLE;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GrocerifyUserInterface frame = new GrocerifyUserInterface();
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
	public GrocerifyUserInterface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 438, 260);
		contentPane.add(scrollPane);
		
		RENAMETABLE = new JTable();
		scrollPane.setViewportView(RENAMETABLE);
		
		
		
		loadTable();
		Connection conn;
		
		try {
			conn = renameCLASS.getConnection();
			
			PreparedStatement pstmt = conn.prepareStatement("SELECT ITEM_ID, ITEM_NAME FROM TB_ITEM_MASTER tim WHERE ITEM_NAME LIKE 'c%';");
			ResultSet rs = pstmt.executeQuery();
			
		} catch (Exception e){
			e.printStackTrace();
			
		}
		
		
		
		
	}
	
	
	
	
	public void loadTable () {
		
	}
}
