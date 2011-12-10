package webosdevtool.process;

import webosdevtool.DevSourceItem;

import javax.swing.JOptionPane;

/**
 * Class to support the portrayal of Task reports to users.
 */
public class TaskReportDialog {
	
	// Empty constructor
	
	/**
	 * Empty constructor as all methods are static.
	 */
	public TaskReportDialog () {}
	
	/**
	 * @param task The task to show a dialog for.
	 */
	public static void show (Task task) {
		
		// set title
		final String title = "Devtool - " + task.getName();
		
		// get report to show
		final String info = task.getReport();
		
		// decide on message type based on success
		final int messageType;
		if ( task.isSuccessful() ) {
			messageType = JOptionPane.INFORMATION_MESSAGE;
		} else {
			messageType = JOptionPane.ERROR_MESSAGE;
		}
		
		// show message dialog
		// done in separate thread to avoid execute not returning...
	    new Thread (new Runnable () {
			public void run () {
				JOptionPane.showMessageDialog(null, info, title, messageType);
			}
	    }).start();
	}
}