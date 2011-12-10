package webosdevtool.logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ThreadedLogStreamHandler extends Thread {
	
	// Variables
	
	private LogProcessor logProc;
	private InputStream inputStream;
	private BufferedReader bufferedReader;
  
	// Constructor
	
	/** 
	 * @param inputStream Stream from process which will be used to feed the log editor pane
	 */
	public ThreadedLogStreamHandler (LogProcessor lp, InputStream inputStream) {
		this.logProc = lp;
		this.inputStream = inputStream;
	}
    
	// Methods
	
	public void run() {
	
	  bufferedReader = null;
	  
	  try {
	    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    String line = null;
	    
	    // TODO figure out how to break the IO lock here
	    while ( (line = bufferedReader.readLine()) != null) {
	    	
	    	// add new content to editor pane of LogFrame
	    	logProc.logFrame.addContent(line);
	    	
	    	// check if this thread should exit
	    	// do so if processor is no longer active
	    	if ( !logProc.getStatus() ) {
	    		break;
	    	}
	    }
	  }
	  catch (java.io.IOException ioe) {
	    // TODO handle this better
	    ioe.printStackTrace();
	  }
	  catch (java.lang.Throwable t) {
	    // TODO handle this better
	  	t.printStackTrace();
	  }
	  finally {
	    try {
	    	bufferedReader.close();
	    }
	    catch (java.io.IOException ioe) {
	    	// ignore this one
	    }
	  }
	} // end of run()
}