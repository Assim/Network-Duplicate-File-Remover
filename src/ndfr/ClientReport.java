package ndfr;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JButton;

import java.util.ArrayList;
import java.util.Collection;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;

public class ClientReport {

	/** This will hold all the checkBoxes controls */
	private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	
	// Swing GUI elements
	private JFrame frame;
	private JPanel panel;
	
	/**
	 * Create the application.
	 */
	public ClientReport(Collection<String> duplicateFileList) {
		initialize();
		frame.setVisible(true);
		
		// For every file in the collection of strings
		for(String fileName : duplicateFileList) {
			// Make a new checkbox with the file name
			JCheckBox checkBox = new JCheckBox(fileName);
			
			// Add the checkbox to the checkboxes list and to the panel
			checkBoxes.add(checkBox);
			panel.add(checkBox);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Network Duplicate File Remover (Client Report)");
		frame.setBounds(100, 100, 500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("<html><font size=6>Network Duplicate File Remover</font></html");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 464, 29);
		frame.getContentPane().add(lblTitle);
		
		JLabel lblClientReport = new JLabel("<html><font size=4>Client Report</font></html>");
		lblClientReport.setHorizontalAlignment(SwingConstants.CENTER);
		lblClientReport.setBounds(20, 51, 454, 29);
		frame.getContentPane().add(lblClientReport);
		
		JLabel lblDescription = new JLabel("<html><font color=red>These files were found to be duplicates on the network:</font></html>");
		lblDescription.setBounds(10, 91, 464, 14);
		frame.getContentPane().add(lblDescription);
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		// Create a scroll pane taking a JPanel as the parameter
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setBounds(10, 116, 464, 200);
		frame.getContentPane().add(scrollPane);
		
		JButton btnDeleteSelectedFiles = new JButton("Delete Selected Files");
		btnDeleteSelectedFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteFiles();
			}
		});
		btnDeleteSelectedFiles.setBounds(10, 327, 220, 23);
		frame.getContentPane().add(btnDeleteSelectedFiles);
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
	 * Deletes a given file
	 * 
	 * @param fileName The full path of the file to be deleted
	 */
	public void delete(String fileName) {
		File file = new File(fileName);
		file.delete();
	}
	
	public void deleteFiles() {
		int filesDeleted = 0;
		
		// Iterate over all check boxes
		for(JCheckBox checkBox : checkBoxes) {
			// If check box is selected
			if(checkBox.isSelected()) {
				// Delete the selected file
				delete(checkBox.getText());
				filesDeleted++;
			}
		}
		
		// Show result and close application
		if(filesDeleted == 1) {
			// 1 file deleted
			showMessage(filesDeleted+" file has been deleted. Application will terminate now.");
		}
		else {
			// n number of files deleted
			showMessage(filesDeleted+" files have been deleted. Application will terminate now.");
		}
		System.exit(0);
	}
}