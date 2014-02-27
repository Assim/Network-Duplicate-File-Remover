package ndfr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerReport {

	// Swing GUI elements
	private JFrame frame;
	private JTextArea textArea;

	/**
	 * Create the application.
	 */
	public ServerReport(Collection<Vector<String>> duplicates) {
		initialize();
		frame.setVisible(true);
		
		int counter = 1;
		StringBuffer stringBuffer = new StringBuffer();
		String[] itemSplit;
		
		for(Collection<String> list : duplicates) {
			stringBuffer.append("Duplicate Occurence "+counter+":\n");
			for(String item : list) {
				itemSplit = item.split("\\|");
				stringBuffer.append("Client "+itemSplit[0]+" - "+itemSplit[1]+"\n");
			}
			// Add more line breaks after each occurrence
			stringBuffer.append("\n");
			
			// Add to text area
			textArea.setText(textArea.getText()+stringBuffer.toString());
			
			// Increase counter
			counter++;
			
			// Clear buffer
			stringBuffer.setLength(0);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Network Duplicate File Remover (Server Report)");
		frame.setBounds(100, 100, 700, 630);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
	
		JLabel lblTitle = new JLabel("<html><font size=6>Network Duplicate File Remover</font></html");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 652, 29);
		frame.getContentPane().add(lblTitle);
		
		JLabel lblServerReport = new JLabel("<html><font size=4>Server Report</font></html>");
		lblServerReport.setHorizontalAlignment(SwingConstants.CENTER);
		lblServerReport.setBounds(20, 51, 642, 29);
		frame.getContentPane().add(lblServerReport);
		
		JLabel lblDescription = new JLabel("<html><font color=red>These files were found to be duplicates on the network:</font></html>");
		lblDescription.setBounds(10, 91, 664, 14);
		frame.getContentPane().add(lblDescription);
		
		textArea = new JTextArea();
		textArea.setBounds(10, 116, 664, 435);
		frame.getContentPane().add(textArea);
		
		// Create a scroll pane taking a JTextArea as the parameter
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(10, 116, 664, 435);
		frame.getContentPane().add(scrollPane);
		
		JButton btnExportReport = new JButton("Export Report");
		btnExportReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportReport();
			}
		});
		btnExportReport.setBounds(494, 562, 180, 23);
		frame.getContentPane().add(btnExportReport);
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
	 * Gets the text from the log
	 * 
	 * @return The log
	 */
	public String getReport() {
		return textArea.getText();
	}
	
	/** 
	 * This will save the log to a file
	 */
	public void exportReport() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select save location for report...");
		chooser.setSelectedFile(new File("Server Report.txt"));
		// Demonstrate "Open" dialog:
		int rVal = chooser.showSaveDialog(frame);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, true)); // true for append
				textArea.write(writer); // Use the text area writer
				writer.close();
				showMessage("Report saved: "+file.toString());
			} catch (IOException ex) {
				showError("Could not save report.");
			}
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {
			// Action to be done when cancel is pressed
		}
	}
}