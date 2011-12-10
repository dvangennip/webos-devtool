package webosdevtool.help;

import webosdevtool.AMenuItem;
import webosdevtool.DevtoolHTMLHelper;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.widgets.WindowUtils;

/**
 * Help screen with info on how this developer for webOS works.
 *
 * Essentially this is a simple HTML viewer for the manual.
 * @see webosdevtool.DevtoolHTMLHelper
 */
public class DevtoolHelpWindow extends JFrame {
	
	// variables
	JPanel helpPanel = null;
	JMenuBar helpMenuBar = null;
	BottomBar helpBottomBar = null;
	JButton closeButton = null;
	
	// constructor
	public DevtoolHelpWindow () {
		
		super("webOS developer tool - Manual");
		
		// May be required for macwidgets
		//MacUtils.makeWindowLeopardStyle(this.getRootPane());
		WindowUtils.createAndInstallRepaintWindowFocusListener(this);
		
		// Menu bar
		helpMenuBar = new JMenuBar();
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
				dispose();
			}
	    });
	    windowMenu.add(windowMenuClose);
	    helpMenuBar.add(windowMenu);
	    // Set menubar
	    setJMenuBar(helpMenuBar);
		
		// Help text pane
		JEditorPane helpTextPane = new JEditorPane();
		helpTextPane.setEditable(false);
		helpTextPane.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		helpTextPane.setContentType("text/html; charset=UTF-8");
		
		// Get the text pane styled
		DevtoolHTMLHelper.applyHTMLStyle(helpTextPane, "/webosdevtool/images/help.css");
		
		//Put the editor pane in a scroll pane.
		JScrollPane helpScrollPane = new JScrollPane(helpTextPane);
		IAppWidgetFactory.makeIAppScrollPane(helpScrollPane);
		helpScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		helpScrollPane.setPreferredSize(new Dimension(700, 400));
		helpScrollPane.setMinimumSize(new Dimension(300, 100));
		
		// Get and set the HTML file
		URL helpURL = DevtoolHelpWindow.class.getResource("DevtoolManual.html");
		if (helpURL != null) {
		    try {
		        helpTextPane.setPage(helpURL);
		    } catch (java.io.IOException e) {
		        helpTextPane.setText("Error: Attempted to read a bad URL: " + helpURL.toString() );
		    }
		} else {
		    helpTextPane.setText("Error: Couldn't find HTML manual file.");
		}
		
		// Add a hyperlink listener
		DevtoolHTMLHelper.addLinkListener(helpTextPane);
		
		// Bottom bar
		helpBottomBar = new BottomBar(BottomBarSize.LARGE);
		helpBottomBar.installWindowDraggerOnWindow(this);
		
		// close button
		closeButton = new JButton("Close");
        closeButton.putClientProperty("JButton.buttonType", "textured");
        closeButton.setToolTipText("Close this window (\u2318W)");
        closeButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
	    });
	    helpBottomBar.addComponentToCenter(closeButton);
		
		helpPanel = new JPanel(new BorderLayout());
		helpPanel.add(helpScrollPane, BorderLayout.CENTER);
		helpPanel.add(helpBottomBar.getComponent(), BorderLayout.SOUTH);
		setContentPane(helpPanel);
		
		// Do qualitative layout
	    pack ();
	
	    // Determine actual sizes.
	    Dimension d = Toolkit.getDefaultToolkit ().getScreenSize ();
	    // The window is 20 pixels right of devtool default and aligned with top.
	    setLocation (d.width/5+20, 0);
	    //setSize (new Dimension (600, 300));
	    // Show it.
	    setVisible (true);
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
}