package webosdevtool;

// Import packages

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

public class DevtoolLogPane extends JScrollPane {

	private JTextArea logTextPane;

	private boolean loggingEnabled;
	private boolean logFresh;

	private PrintStream logOut;
	private PrintStream originalStdOut;

	DevtoolLogPane () {
		// Log text pane
		logTextPane = new JTextArea();
		logTextPane.setFont(new Font("Menlo", Font.PLAIN, 11));
		logTextPane.setLineWrap(true);
		logTextPane.setWrapStyleWord(false);
		logTextPane.setEditable(false);
		logTextPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Put the editor pane in this scroll pane.
  		//logScrollPane = new JScrollPane(logTextPane);
  		this.setViewportView(logTextPane);
  		IAppWidgetFactory.makeIAppScrollPane(this);
  		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

  		logTextPane.setForeground(new Color(0.95f, 0.95f, 0.95f));
  		logTextPane.setBackground(new Color(0.1f, 0.1f, 0.15f));

  		// start logging
  		originalStdOut = System.out; // for reverting the stream
	    logFresh = true;
	    loggingEnabled = false; // init as false
	}

	protected void clearLogContent() {
		logFresh = true;
		logTextPane.setText(null);
	}
	
	protected void setContent(String c) {
		clearLogContent();
		addContent( c );
	}
	
	protected void addContent(String addition) {
		// parse new content
		addition += "\n";
				
		// add new content
		logFresh = false;
		String content = logTextPane.getText();
		content += addition;
		logTextPane.setText(content);
		
		// make scrollpane focus on the bottom after setting content
		// invokeLater method fires after all events have returned
		final JScrollPane that = this; // reference otherwise gets borked
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int maxValue = that.getVerticalScrollBar().getMaximum();
				that.getVerticalScrollBar().setValue( maxValue );
			}
		});
	}

	void setLogState(boolean inState) {
		loggingEnabled = !inState; // inverse
		this.toggleLogging(); // ...and flip once more
	}

	void toggleLogging() {
		loggingEnabled = !loggingEnabled;

		if (loggingEnabled) {
			this.addContent("\n--- webOS DEVTOOL LOGGER STARTED -----------------\n");
			 // printstream args: outputstream, autoflush
			System.setOut(new PrintStream(new LoggingOutputStream(this), true));
		} else {
			System.setOut(originalStdOut);
			this.addContent("\n--- webOS DEVTOOL LOGGER STOPPED -----------------\n");
		}
	}
}

class LoggingOutputStream extends ByteArrayOutputStream {

	private String lineSeparator;
	private DevtoolLogPane logPane;

	public LoggingOutputStream(DevtoolLogPane inLogPane) {
		super();
		lineSeparator = System.getProperty("line.separator");
		logPane = inLogPane;
	}

	public void flush() throws java.io.IOException {

		String record;
		synchronized(this) {
			super.flush();
			record = this.toString();
			super.reset();
 
			if (record.length() == 0 || record.equals(lineSeparator)) {
				// avoid empty records
				return;
			}

			logPane.addContent(record);
		}
	}
}