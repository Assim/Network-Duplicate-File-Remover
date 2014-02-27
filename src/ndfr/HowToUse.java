package ndfr;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

public class HowToUse {

	private JFrame frame;

	/**
	 * Create the application.
	 */
	public HowToUse() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("How To Use");
		frame.setBounds(100, 100, 500, 300);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("<html><font size=6>Network Duplicate File Remover</font></html");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 464, 29);
		frame.getContentPane().add(lblTitle);
		
		JLabel lblHowToUse = new JLabel("<html><font size=4>How To Use</font></html>");
		lblHowToUse.setHorizontalAlignment(SwingConstants.CENTER);
		lblHowToUse.setBounds(20, 51, 454, 29);
		frame.getContentPane().add(lblHowToUse);
		
		JTextPane txtpn = new JTextPane();
		txtpn.setEditable(false);
		txtpn.setText("1) Select Role:\nThere are two roles; the client role, and the server role. The server is where all the clients connect to. There can be one server, and there can be one or more clients. The server receives the file lists from the clients and identifies the duplicates, and send the results back to all the clients. The client sends the file list to the server and waits to know the duplicates.\n\nWhen you start, you should select either to run the machine as a server or as a client. If one machine wants to have both the roles of the client and server, then you should run the program twice and select once as server and then as client in the second instance of the program.\n\n2) Server:\nWhen running as the server, there will be a server IP address where you can provide all the clients. Everytime a client connects to the server, you will be able to view in the clients list, and it will be shown in the server log. Once all the clients have connected, you can click on \"Generate Report\" to show the reports. One report will be shown on the server side for all the clients, and each client will get their own report with the list of their files that are found to be duplicates on the network.\n\n3) Client:\nWhen running as the client, you will have to provide the \"Server IP address\" which is provided by the server machine, and you should select the \"Working Directory\" where the files will be sent for checking to the server. Once sent to the server, the client program will be in a waiting state and will wait for list of duplicates from the server. Once the server returns the duplicates, the client will then have an option to select the unwanted duplicate files and delete them from the system.");
		//txtpn.setBounds(10, 92, 484, 180);
		//frame.getContentPane().add(txtpn);
		txtpn.setCaretPosition(0);
		
		// Create a scroll pane taking a JTextPane as the parameter
		JScrollPane scrollPane = new JScrollPane(txtpn);
		scrollPane.setBounds(10, 92, 484, 180);
		frame.getContentPane().add(scrollPane);
	}
}