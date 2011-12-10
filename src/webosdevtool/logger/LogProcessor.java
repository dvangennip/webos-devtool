/**
 *
 */

package webosdevtool.logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;

public class LogProcessor extends Thread {
	
	// Variables
	
	protected LogFrame logFrame;
	private List<String> commands;
	private Process process;
	private InputStream inputStream;
	private ThreadedLogStreamHandler inputStreamHandler;
	private boolean active;
	
	// Constructor
	
	public LogProcessor (LogFrame lf, List<String> commands) {
		this.logFrame = lf;
		this.commands = commands;
	}
	
	// Methods
	
	public void run() {
	    try { 
	    	ProcessBuilder pb = new ProcessBuilder(commands);
	    	pb = pb.redirectErrorStream(true);
	    	active = true;
			process = pb.start();
			
			inputStream = process.getInputStream();
			inputStreamHandler = new ThreadedLogStreamHandler(this, inputStream);
			inputStreamHandler.setDaemon(true);
			inputStreamHandler.start();
			
			// TODO a better way to do this?
			int exitValue = process.waitFor();
			
			// when returning from the wait prepare for exit
			active = false;
			
			// TODO a better way to do this?
			inputStreamHandler.interrupt();
			inputStreamHandler.join();
	    }
	    catch (java.io.IOException ioe) {
	    	System.out.println("IOException: "+ioe);
	    }
	    catch (java.lang.InterruptedException ie) {
	    	System.out.println("InterruptedException: "+ie);
	    }
	    catch (java.lang.NullPointerException npe) {
	    	System.out.println("NullPointerException: "+npe);
	    }
	    finally {
	    	active = false;
	    	logFrame.notifyOfLogTermination();
	    }
	}
	
	public void stopAll() {
		// stop waiting for process
		process.destroy();
		try {
			// this should cause the streamHandler to exit
			inputStream.close();
		}
		catch (java.io.IOException ioe) {
	    	System.out.println("IOException: "+ioe);
	    }
	}
	
	/**
	 * @return Boolean value that is true if <code>active</code> field is true.
	 */
	public boolean getStatus() {
		return active;
	}
}