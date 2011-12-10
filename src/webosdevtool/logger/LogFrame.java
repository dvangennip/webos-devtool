package webosdevtool.logger;

import webosdevtool.AMenuItem;

import java.util.ArrayList;
import java.util.List;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.widgets.WindowUtils;

/**
 * Panel holder for a palm-log stream of an <app id>
 */
public class LogFrame extends JFrame {
	
	// variables
	private JPanel logPanel;
	private JMenuBar logMenuBar;
	private BottomBar logBottomBar;
	private JButton closeButton;
	private JButton toggleButton;
	private JTextArea logTextPane;
	private JScrollPane logScrollPane;
	private JCheckBoxMenuItem logMenuToggleButton;
	private JCheckBoxMenuItem logMenuClearOnRestart;
	
	protected boolean loggingEnabled;
	private boolean clearLogOnStart;
	private boolean logFresh;
	protected String appID;
	protected String deviceID;
	private LogProcessor logProcessor;
	
	// constructor
	public LogFrame (String appID, String deviceID) {
		
		super("palm-log: "+appID);
		
		this.appID = appID;
		this.deviceID = deviceID;
		
		this.clearLogOnStart = false;
		
		// prevent the JFrame from disappearing on clicking the close button
		// rather keep it open and show what happens
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		// Install code to execute for certain window events.
	    // Adapter class WindowAdapter...
	    addWindowListener (new WindowAdapter () {
			// If the windows is closed, shut dow.
			public void windowClosing (WindowEvent e) {
				windowClose();
			}
			// If we are minimized or maximized, keep working.
			public void windowDeiconified (WindowEvent e) {}
			public void windowIconified (WindowEvent e) {}
	    });
		
		// May be required for macwidgets
		//MacUtils.makeWindowLeopardStyle(this.getRootPane());
		WindowUtils.createAndInstallRepaintWindowFocusListener(this);
		
		// Menu bar
		logMenuBar = new JMenuBar();
		// Log menu
		JMenu logMenu = new JMenu("Log");
		// Log > Toggle logging
		logMenuToggleButton = new JCheckBoxMenuItem("Enable Logging", false);
		logMenuToggleButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ));
	    logMenuToggleButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				toggleLogging();
			}
	    });
	    logMenu.add(logMenuToggleButton);
		// Separator
	    logMenu.addSeparator();
		// Log > Clear on restart
		logMenuClearOnRestart = new JCheckBoxMenuItem("Clear Log On Restart", clearLogOnStart);
	    logMenuClearOnRestart.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				toggleClearOnRestartStatus();
			}
	    });
	    logMenu.add(logMenuClearOnRestart);
		// Log > Clear
		AMenuItem logMenuClear = new AMenuItem("Clear Log");
	    logMenuClear.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				clearContent();
			}
	    });
	    logMenu.add(logMenuClear);
		// Window menu
		JMenu windowMenu = new JMenu("Window");
		// Window > Minimize
	    AMenuItem windowMenuMinimize = new AMenuItem("Minimize", KeyEvent.VK_M);
	    windowMenuMinimize.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				windowMinimize();
			}
	    });
	    windowMenu.add(windowMenuMinimize);
	    // Window > Zoom
	    AMenuItem windowMenuZoom = new AMenuItem("Zoom");
	    windowMenuZoom.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				windowZoom();
			}
	    });
	    windowMenu.add(windowMenuZoom);
	    // Window > Close
	    AMenuItem windowMenuClose = new AMenuItem("Close", KeyEvent.VK_W);
	    windowMenuClose.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				windowClose();
			}
	    });
	    windowMenu.add(windowMenuClose);
	    
	    logMenuBar.add(logMenu);
	    logMenuBar.add(windowMenu);
	    // Set menubar
	    setJMenuBar(logMenuBar);
		
		// Log text pane
		logTextPane = new JTextArea();
        logTextPane.setFont(new Font("Menlo", Font.PLAIN, 11));
        logTextPane.setLineWrap(true);
        logTextPane.setWrapStyleWord(false);
		logTextPane.setEditable(false);
		logTextPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		        
		//Put the editor pane in a scroll pane.
		logScrollPane = new JScrollPane(logTextPane);
		IAppWidgetFactory.makeIAppScrollPane(logScrollPane);
		logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		logScrollPane.setPreferredSize(new Dimension(650, 300));
		logScrollPane.setMinimumSize(new Dimension(300, 100));
		
		// Bottom bar
		logBottomBar = new BottomBar(BottomBarSize.LARGE);
		logBottomBar.installWindowDraggerOnWindow(this);
		
		// close button
		closeButton = new JButton("Close");
        closeButton.putClientProperty("JButton.buttonType", "textured");
        closeButton.setToolTipText("Close this window (\u2318W)");
        closeButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				windowClose();
			}
	    });
	    logBottomBar.addComponentToCenter(closeButton);
	    // start/stop button
		toggleButton = new JButton("Stop logging");
        toggleButton.putClientProperty("JButton.buttonType", "textured");
        toggleButton.setToolTipText("Start or stop the palm-log activity (\u2318L)");
        toggleButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				toggleLogging();
			}
	    });
	    logBottomBar.addComponentToCenter(toggleButton);
		
		logPanel = new JPanel(new BorderLayout());
		logPanel.add(logScrollPane, BorderLayout.CENTER);
		logPanel.add(logBottomBar.getComponent(), BorderLayout.SOUTH);
		setContentPane(logPanel);
		
		// Do qualitative layout
	    pack ();
	
	    // The window location (x,y).
	    setLocation (25, 400);
	    // Show it.
	    setVisible(true);
	    
	    // start logging
	    logFresh = true;
	    toggleClearOnRestartStatus(); // sets clearance boolean
	    loggingEnabled = false; // init as false
	    toggleLogging(); // and flip it immediately
	}
	
	// Methods
	
	private void windowMinimize() {
		// check state and act accordingly
	  	if (getExtendedState() == Frame.ICONIFIED) {
	  		setExtendedState(Frame.NORMAL);
	  	} else {
	  		setExtendedState(Frame.ICONIFIED);
	  	}
	}
	
	private void windowZoom() {
		// check state and act accordingly
	  	if (getExtendedState() == Frame.MAXIMIZED_BOTH) {
	  		setExtendedState(Frame.NORMAL);
	  	} else {
	  		setExtendedState(Frame.MAXIMIZED_BOTH);
	  	}
	}
	
	private void windowClose() {
		// stop everything
		loggingEnabled = false;
		stopLogStream();
		
		// finally dispose
		dispose();
	}
	
	private void toggleClearOnRestartStatus() {
		clearLogOnStart = logMenuClearOnRestart.isSelected();
		// TODO notify preference class of change
	}
	
	private void toggleLogging() {
		// toggle
		loggingEnabled = !loggingEnabled;
		
		// take action + uppdate button label
		if (loggingEnabled) {
			if (clearLogOnStart) {
				clearContent();
			} else {
				if (!logFresh) {
					addContent("\n"); // just mark a clear break in the output
				}
			}
			addContent("--- STARTING LOGGER, THIS MAY TAKE SEVERAL SECONDS...\n"); // notify activity
			startLogStream();
			// update buttons
			toggleButton.setText("Stop logging");
			logMenuToggleButton.setSelected(true);
			
		} else {
			// stop logging
			stopLogStream();
			// update buttons
			toggleButton.setText("Restart logging");
			logMenuToggleButton.setSelected(false);		
		}
	}
	
	protected void clearContent() {
		logFresh = true;
		logTextPane.setText(null);
	}
	
	protected void setContent(String c) {
		clearContent();
		addContent( c );
	}
	
	protected void addContent(String addition) {
		
		// parse new content
		addition += "\n";
		// add a linefeed at the beginning if content signals an app restart
		/*
		if (addition.contains("/usr/palm/frameworks")) {
			addition = "\n" + addition;
		}*/
		
		// add new content
		logFresh = false;
		String content = logTextPane.getText();
		content += addition;
		logTextPane.setText(content);
		
		// make scrollpane focus on the bottom after setting content
		// invokeLater method fires after all events have returned
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
		   public void run() {
		   		int maxValue = logScrollPane.getVerticalScrollBar().getMaximum();
		   		logScrollPane.getVerticalScrollBar().setValue( maxValue );
		   }
		});
	}
	
	private void startLogStream() {
				
		// palm-log -f --device=<device> --system-log-level {error,warning,info} <appid>
		
		// build the system command we want to run
		List<String> commands = new ArrayList<String>();
		commands.add("/bin/bash");
		commands.add("/opt/PalmSDK/Current/bin/palm-log"); // palm-worm
		commands.add("-f"); // follow until user quits it
		commands.add("--device=" + deviceID ); // specify device
		//commands.add("--system-log-level"); // log level
		commands.add( appID ); // <package id>
		
		// use separate thread to execute the command
		// and listen to its output
		// by using a separate thread this one can return and give back control to main Devtool
		logProcessor = new LogProcessor(this, commands);
		logProcessor.setDaemon(true);
		logProcessor.start();
	}
	
	private void stopLogStream() {
		// stop all, vars to null
		if (logProcessor != null) {
			logProcessor.stopAll();
			logProcessor = null;
		}
	}
	
	/**
	 * Method should be called to notify this class of terminated logging,
	 * so the GUI can be updated and the user informed.
	 */
	protected void notifyOfLogTermination() {
		
		// update the internal state correctly
		// but only if it assumed logging was still going on
		if (loggingEnabled) {
			// notify of no more activity
			addContent("--- LOGGER STOPPED -----(external reason)------------\n");
			
			// loggingEnabled: it is flipped by the next method call
			// so the desired value is false, it is set to true above
			toggleLogging();
		} else {
			// notify of no more activity
			addContent("--- LOGGER STOPPED ----------------------------------\n");
		}
	}
}