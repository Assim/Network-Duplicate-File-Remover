package ndfr;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

public class About {

	private JFrame frame;

	/**
	 * Create the application.
	 */
	public About() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("About");
		frame.setBounds(100, 100, 500, 300);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("<html><font size=6>Network Duplicate File Remover</font></html");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 464, 29);
		frame.getContentPane().add(lblTitle);
		
		JLabel lblAbout = new JLabel("<html><font size=4>About Us</font></html>");
		lblAbout.setHorizontalAlignment(SwingConstants.CENTER);
		lblAbout.setBounds(20, 51, 454, 29);
		frame.getContentPane().add(lblAbout);
		
		JTextPane txtpn = new JTextPane();
		txtpn.setText("The Network Duplicate File Remover is an application capable of finding duplicate files on the network from a set of files.\n\nThis application was created as a Course Project for the Higher College of Technology by the following students:\nAssim Al-Marhuby\nAmal Aljabri\nMaryam Al-Hashmi\nSalwa Al-Tobi");
		txtpn.setEditable(false);
		//txtpn.setBounds(10, 92, 484, 180);
		//frame.getContentPane().add(txtpn);
		txtpn.setCaretPosition(0);
		
		// Create a scroll pane taking a JTextPane as the parameter
		JScrollPane scrollPane = new JScrollPane(txtpn);
		scrollPane.setBounds(10, 92, 484, 180);
		frame.getContentPane().add(scrollPane);
	}
}