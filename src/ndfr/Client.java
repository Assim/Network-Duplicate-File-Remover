package ndfr;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class display the client screen and also perform client functions.
 */
public class Client {

	/** Will be used to store the list of all the files and their checksums in the form of "IP|file|checksum" */
	private ArrayList<String> fileList = new ArrayList<String>();
	
	/** 
	 * Will be used to store all files that are found to be duplicates, this will come from the server 
	 * This will be a HashSet because it can't hold the same file twice.
	 * */
	private HashSet<String> duplicateFileList = new HashSet<String>();
	
	/** The client socket object */
	private Socket clientSocket;
	
	/** This will be used to write to the server */
	private PrintWriter writer;
	
	/** This will be used to read from the server */
	private BufferedReader reader;
	
	// Swing GUI elements
	private JFrame frame;
	private JTextField txtServerIpAddress;
	private JTextField txtWorkingDirectory;
	private JButton btnBrowse;
	private JButton btnSendToServer;
	private JLabel lblYourIpAddressText;
	private JMenuItem mntmSendToServer;
	private JMenuItem mntmChangeDirectory;
	private JLabel lblStatusText;

	/**
	 * Create the application.
	 */
	public Client() {
		initialize();
		frame.setVisible(true);
		
		setStatus("Not connected", "red");
		refresh();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Network Duplicate File Remover (Client)");
		frame.setBounds(100, 100, 500, 294);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("<html><font size=6>Network Duplicate File Remover</font></html");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 464, 29);
		frame.getContentPane().add(lblTitle);
		
		JLabel lblClientApplication = new JLabel("<html><font size=4>Client Application</font></html>");
		lblClientApplication.setHorizontalAlignment(SwingConstants.CENTER);
		lblClientApplication.setBounds(20, 51, 454, 29);
		frame.getContentPane().add(lblClientApplication);
		
		JLabel lblServerIpAddress = new JLabel("Server IP Address:");
		lblServerIpAddress.setBounds(10, 91, 120, 14);
		frame.getContentPane().add(lblServerIpAddress);
		
		JLabel lblWorkingDirectory = new JLabel("Working Directory:");
		lblWorkingDirectory.setBounds(10, 116, 120, 14);
		frame.getContentPane().add(lblWorkingDirectory);
		
		txtServerIpAddress = new JTextField();
		txtServerIpAddress.setBounds(140, 88, 235, 20);
		frame.getContentPane().add(txtServerIpAddress);
		txtServerIpAddress.setColumns(10);
		
		txtWorkingDirectory = new JTextField();
		txtWorkingDirectory.setEnabled(false);
		txtWorkingDirectory.setBounds(140, 113, 235, 20);
		frame.getContentPane().add(txtWorkingDirectory);
		txtWorkingDirectory.setColumns(10);
		
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directoryChooser();
			}
		});
		btnBrowse.setBounds(385, 112, 89, 23);
		frame.getContentPane().add(btnBrowse);
		
		btnSendToServer = new JButton("Send to Server");
		btnSendToServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendToServer();
			}
		});
		btnSendToServer.setBounds(10, 166, 200, 23);
		frame.getContentPane().add(btnSendToServer);
		
		JLabel lblYourIpAddress = new JLabel("Your IP Address:");
		lblYourIpAddress.setBounds(10, 141, 120, 14);
		frame.getContentPane().add(lblYourIpAddress);
		
		lblYourIpAddressText = new JLabel("Unknown");
		lblYourIpAddressText.setBounds(140, 141, 235, 14);
		frame.getContentPane().add(lblYourIpAddressText);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(10, 200, 120, 14);
		frame.getContentPane().add(lblStatus);
		
		lblStatusText = new JLabel("");
		lblStatusText.setBounds(140, 200, 334, 14);
		frame.getContentPane().add(lblStatusText);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmSendToServer = new JMenuItem("Send to Server");
		mntmSendToServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendToServer();
			}
		});
		mnFile.add(mntmSendToServer);
		
		mntmChangeDirectory = new JMenuItem("Change directory");
		mntmChangeDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				directoryChooser();
			}
		});
		mnFile.add(mntmChangeDirectory);
		
		JMenuItem mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		mnFile.add(mntmRefresh);
		
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
	 * Display an message dialogue box
	 * 
	 * @param message The message to be displayed
	 */
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(frame,
			    message);
	}
	
	/**
	 * Displays an error dialogue box
	 *
	 * @param error The error to be displayed 
	 */
	public void showError(String error) {
		JOptionPane.showMessageDialog(frame,
			    error,
			    "Error",
			    JOptionPane.ERROR_MESSAGE);
	}
	
	/** 
	 * Disables the controls 
	 */
	public void disableControls() {
		txtServerIpAddress.setEnabled(false);
		btnBrowse.setEnabled(false);
		btnSendToServer.setEnabled(false);
		mntmSendToServer.setEnabled(false);
		mntmChangeDirectory.setEnabled(false);
	}

	/** 
	 * Enables the controls
	 */
	public void enableControls() {
		txtServerIpAddress.setEnabled(true);
		btnBrowse.setEnabled(true);
		btnSendToServer.setEnabled(true);
		mntmSendToServer.setEnabled(true);
		mntmChangeDirectory.setEnabled(true);
	}
	
	/** 
	 * Refresh network details 
	 */
	public void refresh() {
		lblYourIpAddressText.setText(Utilities.getIpAddress());
	}
	
	/**
	 * Sets status message
	 * 
	 * @param status Takes the status to set
	 * @param color The color of the status
	 */
	public void setStatus(String status, String color) {
		lblStatusText.setText("<html><font color="+color+">"+status+"</font></html>");
	}
	
	/** 
	 * Procedure of selecting a directory 
	 */
	public void directoryChooser() {
		String directory = directoryChooserDialogue();
		txtWorkingDirectory.setText(directory);
	}
	
	/** Display a directory chooser dialogue 
	 * 
	 * @return The selected directory
	 */
	public String directoryChooserDialogue() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Choose working directory...");
		// Demonstrate "Open" dialog:
		int rVal = chooser.showOpenDialog(frame);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			return file.getAbsolutePath();
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {
			// Action to be done when cancel is pressed
		}
		return "";
	}
	
	/** 
	 * This is for form validation, returns false if an error was found, returns true if no error was found 
	 * 
	 * @return True if form is validated, and returns false with an error if there is a validation error
	 */
	public boolean formValidation() {
		// Check if IP address isn't blank
		if(txtServerIpAddress.getText().length() == 0) {
			// Display error message and exit method
			showError("Server IP Address can't be blank.");
			return false;
		}
		
		// Check if working directory isn't blank
		if(txtWorkingDirectory.getText().length() == 0) {
			// Display error message and exit method
			showError("You should select a working directory before connecting.");
			return false;
		}
		
		// No errors found
		return true;
	}
	
	/** 
	 * Connect to server, returns true if connected, and returns false with an error if not connected
	 * 
	 * @return True if connected, false if not connected along with an error dialogue
	 */
	public boolean establishConnection() {
		// Try connecting to server
				try {
					clientSocket = new Socket(txtServerIpAddress.getText(), 2901);
					InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
					reader = new BufferedReader(streamReader);
					writer = new PrintWriter(clientSocket.getOutputStream(), true);
					return true;
				} catch (UnknownHostException e) {
					// Could not connect
					showError("Could not connect to server");
					return false;
				} catch (IOException e) {
					showError("Couldn't get I/O for the connection");
					return false;
				}
	}
	
	/** 
	 * Populates the fileList with the files from the working directory 
	 */
	public void createFileList() {
		// Make sure list is empty by clearing it
		fileList.clear();
		
		// NOTE: If working directory was null, you will probably get an error
		
        File directory = new File(txtWorkingDirectory.getText());
        File[] files = directory.listFiles();
        
        String fileName, checksum = "";
		
        // Loop through all the files
        for(int i=0; i<files.length; i++) {
        	// Check if it is a file not a directory
        	if(files[i].isFile()) {
        		fileName = files[i].toString(); // File name of current file
        	
        		// Checksum of current file
            	try {
            		checksum = Utilities.getChecksum(fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
            
            	// Add current file to ArrayList in the form of "filename|checksum"
            	fileList.add(fileName+"|"+checksum);
        	}
        }
	}
	
	/**
	 * Closes the socket and closes the streams.
	 */
	public void closeSocket() {
		// Disconnect from server and close the streams
		try {
			clientSocket.close();
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Does the form validation, and then starts the server writer thread.
	 */
	public void sendToServer() {
		// Check form for errors. If form has error, then don't proceed
		if(!formValidation()) return;
					
		// Try establishing a connection to the server, if can't connect exit
		if(!establishConnection()) return;
					
		// Disable the controls
		disableControls();
		
		// Start the server writer thread
		new Thread(new ServerWriterThread()).start();
	}
	
	/**
	 * The thread that will send the data to the server.
	 */
	class ServerWriterThread implements Runnable {

		@Override
		public void run() {
			setStatus("Sending to server...", "orange");
			
			// After connecting to server, populate file list
			createFileList();
			
			// Send each line of fileList to server
			for(String item : fileList) {
				writer.println(item);
			}
			
			// Print that there is no more results
			writer.println("##EOL##");
			
			// Fire up the server reading thread
			new Thread(new ServerReaderThread()).start();
		}
	}
	
	/** 
	 * This thread reads duplicates from server and stores it in duplicateFileList.
	 * When reading is done, it closes the socket and streams, and shows the report. 
	 */
	class ServerReaderThread implements Runnable {

		@Override
		public void run() {			
			// Clear duplicateFileList
			duplicateFileList.clear();

			setStatus("Waiting for server...", "green");
			
			String serverResponse;
			try {
				while((serverResponse = reader.readLine()) != null) {
					// If server said ##EOL## then there's nothing more to add, so exit loop
					if(serverResponse.equals("##EOL##")) {
						// Read complete list without any errors
						break;
					}
					else
					{
						// Add each and every file to the duplicateFileList
						duplicateFileList.add(serverResponse);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// When reading is done
			
			// Close socket and streams
			closeSocket();
			
			// Check if any duplicates were sent, otherwise this client has no duplicates
			if(duplicateFileList.size() > 0) {
				// Client has duplicates
				
				// Show report and allow user to select files for deletion
				// Open a new window and send the duplicateFileList as the parameter
				new ClientReport(duplicateFileList);
			}
			else {
				// Otherwise just tell the user that there is no duplicates found and close application
				showMessage("Your computer has no duplicate files.");
			}
			
			// Close this frame
			frame.dispose();
		}
	}
}