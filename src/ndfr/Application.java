package ndfr;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This class display the main screen and allows users to select whether they want to run as a client or server.
 */
public class Application {

	// Swing GUI elements
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		new Application();
	}

	/**
	 * Create the application.
	 */
	public Application() {
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Network Duplicate File Remover");
		frame.setBounds(100, 100, 500, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("<html><font size=6>Network Duplicate File Remover</font></html");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 464, 29);
		frame.getContentPane().add(lblTitle);
		
		JLabel lblRunApplicationAs = new JLabel("<html><font size=4>Run application as:</font><html>");
		lblRunApplicationAs.setHorizontalAlignment(SwingConstants.CENTER);
		lblRunApplicationAs.setBounds(10, 80, 464, 29);
		frame.getContentPane().add(lblRunApplicationAs);
		
		JButton btnClient = new JButton("Client");
		btnClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startClient();
			}
		});
		btnClient.setBounds(153, 120, 89, 30);
		frame.getContentPane().add(btnClient);
		
		JButton btnServer = new JButton("Server");
		btnServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startServer();
			}
		});
		btnServer.setBounds(252, 120, 89, 30);
		frame.getContentPane().add(btnServer);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmRunAsClient = new JMenuItem("Run as Client");
		mntmRunAsClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startClient();
			}
		});
		mnFile.add(mntmRunAsClient);
		
		JMenuItem mntmRunAsServer = new JMenuItem("Run as Server");
		mntmRunAsServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startServer();
			}
		});
		mnFile.add(mntmRunAsServer);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new About();
			}
		});
		mnHelp.add(mntmAbout);
		
		JMenuItem mntmHowToUse = new JMenuItem("How to use");
		mntmHowToUse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HowToUse();
			}
		});
		mnHelp.add(mntmHowToUse);
	}
	
	/**
	 * Starts the client and closes the current application window.
	 */
	public void startClient() {
		new Client();
		frame.dispose();
	}
	
	/**
	 * Starts the server and closes the current application window.
	 */
	public void startServer() {
		new Server();
		frame.dispose();
	}
}