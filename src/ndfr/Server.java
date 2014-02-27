package ndfr;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * This class display the server screen and also perform server functions.
 */
public class Server {

	/** 
	 * The list of checksum along with the client IP address and file name.
	 * The HashMap will store checksum as keys, and in the value it will store a vector list of IP address and file separated by "|"
	 */
	private HashMap<String, Vector<String>> checksumList = new HashMap<String, Vector<String>>();
	
	/** 
	 * This will hold the list of checksum that are duplicates.
	 * Vector was used for thread-safety because this is multi-threaded.
	 *  */
	private Vector<String> duplicateChecksum = new Vector<String>();
	
	/** 
	 * This will hold client IP address as key, and HashSet of duplicate files for each client.
	 * We are are using HashSet because a file with the same name can't be added twice
	 * This will be generated when the report is generated, to make it easier to return results to clients.
	 */
	private HashMap<String, HashSet<String>> clientDuplicates = new HashMap<String, HashSet<String>>();
	
	/** The server socket object */
	private ServerSocket server;
	
	/** The list of client sockets connected to this server. */
	private ArrayList<Socket> clients = new ArrayList<Socket>();
	
	/** This will hold the number of connected clients */
	private int clientsCount;
	
	/** The number of client reports sent to the clients, this will be used to track if the work is done or not */
	private int clientsListSent;
	
	/** This will hold the list of clients for the combo box */
	@SuppressWarnings("rawtypes")
	private DefaultComboBoxModel clientsComboBox = new DefaultComboBoxModel();
	
	/** This is the client connection thread object */
	private Thread clientConnectionThread;
	
	// Swing GUI elements
	private JFrame frame;
	private JLabel lblServerIpAddressText;
	private JButton btnGenerateReport;
	private JMenuItem mntmGenerateReport;
	private JTextArea textArea;
	private JButton btnExportLog;

	/**
	 * Create the application.
	 */
	public Server() {
		initialize();
		frame.setVisible(true);
		refresh();
		
		addToLog("Server started. Waiting for clients at port 2901.");
		
		// Start the client connection thread
		clientConnectionThread = new Thread(new ClientConnectionThread());
		clientConnectionThread.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("unchecked")
	private void initialize() {
		frame = new JFrame("Network Duplicate File Remover (Server)");
		frame.setBounds(100, 100, 600, 457);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("<html><font size=6>Network Duplicate File Remover</font></html");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 564, 29);
		frame.getContentPane().add(lblTitle);
		
		JLabel lblServerApplication = new JLabel("<html><font size=4>Server Application</font></html>");
		lblServerApplication.setHorizontalAlignment(SwingConstants.CENTER);
		lblServerApplication.setBounds(20, 51, 554, 29);
		frame.getContentPane().add(lblServerApplication);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmGenerateReport = new JMenuItem("Generate Report");
		mntmGenerateReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		mnFile.add(mntmGenerateReport);
		
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
		
		JLabel lblServerIpAddress = new JLabel("Server IP Address:");
		lblServerIpAddress.setBounds(10, 91, 120, 14);
		frame.getContentPane().add(lblServerIpAddress);
		
		lblServerIpAddressText = new JLabel("Unknown");
		lblServerIpAddressText.setBounds(142, 90, 432, 16);
		frame.getContentPane().add(lblServerIpAddressText);
		
		JLabel lblClients = new JLabel("Clients:");
		lblClients.setBounds(10, 116, 120, 16);
		frame.getContentPane().add(lblClients);
		
		@SuppressWarnings("rawtypes")
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(140, 117, 434, 27);
		comboBox.setModel(clientsComboBox);
		frame.getContentPane().add(comboBox);
		
		btnGenerateReport = new JButton("Generate Report");
		btnGenerateReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateReport();
			}
		});
		btnGenerateReport.setBounds(302, 366, 141, 23);
		frame.getContentPane().add(btnGenerateReport);
		
		JLabel lblServerLog = new JLabel("Server Log:");
		lblServerLog.setBounds(10, 143, 120, 14);
		frame.getContentPane().add(lblServerLog);
		
		textArea = new JTextArea();
		textArea.setEnabled(false);
		textArea.setEditable(false);
		textArea.setBounds(142, 155, 432, 200);
		frame.getContentPane().add(textArea);
		
		btnExportLog = new JButton("Export Log");
		btnExportLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportLog();
			}
		});
		btnExportLog.setBounds(453, 366, 121, 23);
		frame.getContentPane().add(btnExportLog);
		
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
		btnGenerateReport.setEnabled(false);
		mntmGenerateReport.setEnabled(false);
	}

	/** 
	 * Enables the controls
	 */
	public void enableControls() {
		btnGenerateReport.setEnabled(true);
		mntmGenerateReport.setEnabled(true);
	}
	
	/** 
	 * Refresh network details 
	 */
	public void refresh() {
		lblServerIpAddressText.setText(Utilities.getIpAddress());
		
		if(clientsCount == clientsListSent && clientsCount != 0) {
			enableControls();
		}
		else {
			disableControls();
		}
	}
	
	/**
	 * Adds a client to the client list, and add the client to the combo box
	 * 
	 * @param clientSocket The client socket
	 */
	@SuppressWarnings("unchecked")
	public void addClient(Socket clientSocket) {
		// Add client socket to list of connected clients
		clients.add(clientSocket);
		
		// If client already exists in Combo Box (duplicate), then remove first
		// Client may have reconnected, that's why
		clientsComboBox.removeElement(clientSocket.getInetAddress().getHostAddress());
		
		// Add client to combo box
		clientsComboBox.addElement(clientSocket.getInetAddress().getHostAddress());
		
		// Update the clients count based on the size of the HashMap
		clientsCount = clients.size();
		
		// Add to log
		addToLog("Client "+ clientSocket.getInetAddress().getHostAddress() +" has connected");
		
		// Refresh will check for clients and will enable or disable based on connected clients
		refresh();
	}
	
	/**
	 * Adds a file name along with the checksum and IP address of the client to the list of checksums.
	 * If checksum already exists, it will add the checksum to the duplicateChecksum list. 
	 * 
	 * @param clientIpAddress The IP address of the client
	 * @param fileName The name of the file
	 * @param checksum The checksum of the file
	 */
	public synchronized void addToChecksumList(String clientIpAddress, String fileName, String checksum) {
		boolean checksumExists = checksumList.containsKey(checksum);
		
        // If checksum doesn't exist
        if(!checksumExists) {    
            // Add current file to a vector
            Vector<String> fileContainer = new Vector<String>();
            fileContainer.add(clientIpAddress+"|"+fileName);
            
            checksumList.put(checksum, fileContainer);
        }
        else {
        	// If checksum already exists
        	
        	// Get the value vector from the checksumList HashMap and reference it as fileContainer
            Vector<String> fileContainer = checksumList.get(checksum);
            
            // Add the current file to the Vector
            fileContainer.add(clientIpAddress+"|"+fileName);
            
            // Add current checksum to duplicateChecksum to keep track of duplicates
            duplicateChecksum.add(checksum);
        }
	}
	
	public synchronized void clientFinishedSending() {
		clientsListSent++;
		refresh();
	}
	
	/**
	 * This just adds text to the log
	 * 
	 * @param text Takes a line of text to add to the log
	 */
	public void addToLog(String text) {
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		textArea.setText(sdf.format(cal.getTime())+" - "+text+"\n"+textArea.getText());
	}
	
	/**
	 * Gets the text from the log
	 * 
	 * @return The log
	 */
	public String getLog() {
		return textArea.getText();
	}
	
	/** 
	 * This will save the log to a file
	 */
	public void exportLog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select save location for log...");
		chooser.setSelectedFile(new File("Server Log.txt"));
		// Demonstrate "Open" dialog:
		int rVal = chooser.showSaveDialog(frame);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, true)); // true for append
				textArea.write(writer); // Use the text area writer
				writer.close();
				
				showMessage("Log saved: "+file.toString());
			} catch (IOException ex) {
				showError("Could not save log.");
			}
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {
			// Action to be done when cancel is pressed
		}
	}

	
	/**
	 * This will populate the clientDuplicates HashMap, it will put every client as the key, and a Vector list of files as the value.
	 * It will also start the ClientWriterThread for each client
	 */
	public void generateReport() {
		
		// Disable controls so it won't be clicked more than once
		disableControls();
		
		// Clear clientDuplicates just in case it was previous populated
		clientDuplicates.clear();
		
		// Loop through every checksum in the list of duplicate checksums
		for(String checksum : duplicateChecksum) {
			// For current checksum, get the Vector that holds IP|filename from the checksum list and store it.
			Vector<String> ipWithFile = checksumList.get(checksum);
			
			// Loop through every single IP|file from that vector which we got
			for(String ipFile : ipWithFile) {
				String[] ipFileSplit = ipFile.split("\\|");
				String ipAddress = ipFileSplit[0];
				String fileName = ipFileSplit[1];
				
				boolean ipExists = clientDuplicates.containsKey(ipAddress);
				
		        // If client doesn't exist
		        if(!ipExists) {    
		            // Add current file to a vector
		            HashSet<String> fileContainer = new HashSet<String>();
		            fileContainer.add(fileName);
		            
		            // Add to client duplicates
		            clientDuplicates.put(ipAddress, fileContainer);
		        }
		        else {
		        	// If client already exists in the list
		        	
		        	// Get the value vector from the clientDuplicates HashMap and reference it as fileContainer
		            HashSet<String> fileContainer = clientDuplicates.get(ipAddress);
		            
		            // Add the current file to the Vector
		            fileContainer.add(fileName);
		        }
			}
			
			// Now that the data is nicely arranged in the clientDuplicates map.
			
			// Iterate through the clients HashMap values (sockets)
			// We will iterate to all the available client sockets (in other words)
			for (Socket socket : clients) {
			    // Start a ClientWriterThread for each client for writing back the results to the clients
				new Thread(new ClientWriterThread(socket)).start();
			}
		}
		
		showReport();
	}
	
	public void showReport() {
		// A collection holding a collection of strings
		// This will store all the duplicates occurences
		Vector<Vector<String>> reportDuplicates = new Vector<Vector<String>>();
		
	
		// Loop through every value in the checksumList
		for(Vector<String> duplicate : checksumList.values()) {
			// If one of those values has more than 1 item, then it's a duplicate
			if(duplicate.size() > 1) {
				// So add it to the reportDuplicates list which we created
				reportDuplicates.add(duplicate);
			}
		}
				
		// If the reportDuplicates is not empty, meaning we added items to it
		// In that case, show the serverReport and give it the data
		if(reportDuplicates.size() > 0) {
			new ServerReport(reportDuplicates);			
		}
		else {
			showMessage("There isn't any duplicates in the network.");
		}
	}
	
	/**
	 * This thread will accept client connections to this server.
	 */
	class ClientConnectionThread implements Runnable {

		@Override
		public void run() {
			try {
				// Set up a server to listen at port 2901
				server = new ServerSocket(2901);
				
				// Keep on running and accept client connections
				while(true) {
					// Wait for a client to connect
					Socket client = server.accept();
					addClient(client);
					
					// Start a new client reader thread for that socket
					new Thread(new ClientReaderThread(client)).start();
				}
			} catch (IOException e) {
				showError("Could not set up server on port 2901. Application will terminate now.");
				System.exit(0);
			}
		}
	}
	
	/**
	 * This thread will read from a single client and add the results to the list. 
	 * It takes a socket connection as a constructor parameter.
	 * Thread will die automatically once the socket is closed.
	 */
	class ClientReaderThread implements Runnable {

		private Socket clientSocket;
		private String clientIpAddress;
		
		public ClientReaderThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
			this.clientIpAddress = clientSocket.getInetAddress().getHostAddress();
		}
		
		@Override
		public void run() {
			// Initialize the reader for this client
			InputStreamReader streamReader = null;
			BufferedReader reader = null;
			
			try {
				streamReader = new InputStreamReader(clientSocket.getInputStream());
				reader = new BufferedReader(streamReader);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String clientResponse;
			try {
				while((clientResponse = reader.readLine()) != null) {
					if(clientResponse.equals("##EOL##")) {
						clientFinishedSending();
						return;
					}
					// Add each file and checksum to checksum list
					// The clientReponse will be in the form of filename|checksum
					// two \\ is required because "\" itself require escaping
					String clientResponseSplit[] = clientResponse.split("\\|");
					
					// Add to checksum list
					addToChecksumList(clientIpAddress, clientResponseSplit[0], clientResponseSplit[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// Once reading is done, whether finished or failed, just wait for client after the above operation is done.
			}
		}
	}
	
	/** 
	 * This thread will run when the generate report has been requested, this will write the report data to the clients
	 */
	class ClientWriterThread implements Runnable {

		private Socket clientSocket;
		
		public ClientWriterThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		@Override
		public void run() {	
			// Initialize the writer for this client
			PrintWriter writer = null;
			
			try {
				writer = new PrintWriter(clientSocket.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Get IP address of this socket
			String clientIpAddress = clientSocket.getInetAddress().getHostAddress();
			
			if(clientDuplicates.containsKey(clientIpAddress)) {
				// Then this client has duplicates
				
				// Get the Vector list of files from the HashMap of clientDuplicates
				HashSet<String> fileContainer = clientDuplicates.get(clientIpAddress);
				
				// Loop through each and every single file from the fileContainer
				for(String file : fileContainer) {
					// Write the file back to the client
					writer.println(file);
				}
				
			}

			writer.println("##EOL##");
			
			// Close socket so client knows there's nothing else to receive from server
			// So that the client can generate it's report too
			// Otherwise, send a special text to tell client nothing more to receive
			// And the client has to know how to handle that specific text
			try {
				clientSocket.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}