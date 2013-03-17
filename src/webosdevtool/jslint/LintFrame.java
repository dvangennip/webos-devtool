package webosdevtool.jslint;

// Import packages

import webosdevtool.AMenuItem;
import webosdevtool.Devtool;
import webosdevtool.Project;
import webosdevtool.process.FileOperator;

import java.util.List;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.widgets.WindowUtils;

import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.Option;

/**
 * LintFrame creates a JFrame with a sourcelist on the left and a viewer (JEditorPane) on the right.
 * The viewer shows the feedback of a JSLint analysis for the selected source file. The sourcelist on the
 * left also indicates how many problems were encountered by the JSLint analyser.
 * The actual JSLint implementation is fully dependent on <a href="http://code.google.com/p/jslint4java/">jslint4java</a> by
 * <a href="http://happygiraffe.net/blog/tag/jslint4java/">Dominic Mitchell</a> and of course the original
 * JSLint by Douglas Crockford on <a href="http://jslint.com/">jslint.com</a>. Again, this LintFrame is best
 * seen as a GUI wrapper for the jslint4java package which simplifies the process for webOS developers.
 */
public class LintFrame extends JFrame {
	
	// Global variables for this class
	
	protected Devtool devtool;
	protected Project currentProject;
	
	private LintSourceListItem currentItem;
	
	private JMenuBar menuBar;
	private BottomBar bottomBar;
	private LintSourceList sourceList;
	private LintViewerPanel viewerPanel;
	
	private JButton closeButton;
	private JButton refreshButton;

  /**
   * Creates a new JSLint analysis GUI.
   * @param myParent Reference to Devtool main class.
   */
  public LintFrame (Devtool myParent) {

    // Set the title of the JFrame.
    super ("JSLint analysis results");

    // Copy from method argument to instance field.
    devtool = myParent;
    
    // prevent the JFrame from disappearing on clicking the close button
	  // rather keep it open and show what happens
	  setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    // Create a menu bar to hold the menus.
    menuBar = new JMenuBar();
    
    // JSLint menu
    JMenu jslintMenu = new JMenu("JSLint");
    // Window > Minimize
    AMenuItem jslintMenuRefresh = new AMenuItem("Refresh", KeyEvent.VK_J);
    jslintMenuRefresh.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
			refresh();
		}
    });
    jslintMenu.add(jslintMenuRefresh);
    
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
			disposeMe();
		}
    });
    windowMenu.add(windowMenuClose);
    
    menuBar.add(jslintMenu);
    menuBar.add(windowMenu);
    
    // Install the menubar.
    setJMenuBar(menuBar);
	
    // Install code to execute for certain window events.
    // Adapter class WindowAdapter...
    addWindowListener (new WindowAdapter () {
		// If the windows is closed, shut dow.
		public void windowClosing (WindowEvent e) {
			disposeMe();
		}
		// If we are minimized or maximized, keep working.
		public void windowDeiconified (WindowEvent e) {}
		public void windowIconified (WindowEvent e) {}
    });
    
    // Window GUI is laid out here
    
    // For some versions of Mac OS X, Java will handle painting the Unified Tool Bar.
	// Calling this method ensures that this painting is turned on if necessary.
	//MacUtils.makeWindowLeopardStyle(this.getRootPane());
	WindowUtils.createAndInstallRepaintWindowFocusListener(this);
	
	// SourceList on left
	sourceList = new LintSourceList(this);
	sourceList.getComponent().setPreferredSize(new Dimension(300,350));
	sourceList.getComponent().setMinimumSize(new Dimension(200,200));
	
	// Actionable panel on right
	viewerPanel = new LintViewerPanel();
	
	// Create splitpane
	JSplitPane mainPane = MacWidgetFactory.createSplitPaneForSourceList(sourceList, viewerPanel);
		
	// Bottom bar
	bottomBar = new BottomBar(BottomBarSize.LARGE);
	bottomBar.installWindowDraggerOnWindow(this);
	
	// Bottom bar - close button
	closeButton = new JButton("Close");
    closeButton.putClientProperty("JButton.buttonType", "textured");
    closeButton.setToolTipText("Close this window (\u2318W)");
    closeButton.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
			disposeMe();
		}
    });
    bottomBar.addComponentToCenter(closeButton);
    // Bottom bar - refresh button
	refreshButton = new JButton("Refresh (\u2318J)");
    refreshButton.putClientProperty("JButton.buttonType", "textured");
    refreshButton.setToolTipText("Redo the JSLint analysis (\u2318J)");
    refreshButton.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent e) {
			refresh();
		}
    });
    bottomBar.addComponentToCenter(refreshButton);
	
	JPanel windowPanel = new JPanel(new BorderLayout());
	windowPanel.add(mainPane, BorderLayout.CENTER);
	windowPanel.add(bottomBar.getComponent(), BorderLayout.SOUTH);
	setContentPane(windowPanel);

    // Do qualitative layout
    pack ();

    // The window location.
    setLocation (20, 40);
    // Show it.
    setVisible (true);
  }
  
  // Window methods -----------------------------------------------------
  
  /**
   * Minimises the window
   */
  public void windowMinimize() {
  	// check state and act accordingly
  	if (getExtendedState() == Frame.ICONIFIED) {
  		setExtendedState(Frame.NORMAL);
  	} else {
  		setExtendedState(Frame.ICONIFIED);
  	}
  }
  
  /**
   * Zoomes the window
   */
  public void windowZoom() {
  	// check state and act accordingly
  	if (getExtendedState() == Frame.MAXIMIZED_BOTH) {
  		setExtendedState(Frame.NORMAL);
  	} else {
  		setExtendedState(Frame.MAXIMIZED_BOTH);
  	}
  }
  
  /**
   * Method is automatically called and essentially overrides the built-in paint()
   * method of a JFrame.
   * @param gl The default Graphics element, provided by super class.
   */
  public void paint(Graphics gl) {
  	// this explicit casting to G2D has to be done
  	// to be able to use anti-aliasing methods
  	Graphics2D g = (Graphics2D) gl;
  	
  	// enable anti-aliasing for smoother visuals
  	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  	
  	// obligatory call to super class
	super.paint(g);
  }
  
  // Methods -----------------------------------------------------
  
  /**
   * Clean up and exit
   */
  private void disposeMe() {
  	dispose();
  	devtool.disposeJSLintWindow();
  }
  
  /**
   * Update the current focus of viewer panel.
   * Calls for GUI updates as well to reflect the change.
   *
   * @param selectedItem The item to show the JSLint analysis results for.
   */
  protected void setCurrentItem(LintSourceListItem selectedItem) {
	// check and set
	if (selectedItem != null) {
		currentItem = selectedItem;
	}
	
	// explicitly set the item as selected
	// to reduce confusion when program automatically selects an item
	try {
		sourceList.setSelectedItem( currentItem );
	}
	catch (java.lang.IllegalArgumentException iae) {}
		
	// update GUI
	viewerPanel.setContent( currentItem.getReport() );
  }
  
  /**
   * Sets a project and calls <code>refresh()</code> to get the analysis results to display.
   * @param p Reference to a project that will be the focus of the LintFrame.
   */
  public void setProject(Project p) {
  	if (p != null) {
  		currentProject = p;
  		refresh();
  	}
  }
  
  /**
   * Refreshes the sourcelist items to reflect the current project,
   * based on a <code>Project</code> instance's sources.json collection,
   * and consequently analyses each source file with the <code>lint</code> method of jslint4java.
   */
  public void refresh() {
 	
 	// if a project is available refresh
 	if (currentProject != null) {
 		
	  	// update category name
	  	sourceList.setProjectName( currentProject.getName() );
	  	viewerPanel.setDefaultContent();
	  	
	  	// crude way to refresh
	  	// remove it all before adding it again
	  	sourceList.removeAllItems();
	  	
	  	// get source data
	  	String[] sources = currentProject.getSources();
	  	String projectFolder = currentProject.getLocation();
	  	
		// using jslint4java @ http://code.google.com/p/jslint4java/
		JSLint lint = new JSLintBuilder().fromDefault();
		// set options if necessary
		lint.addOption(Option.SLOPPY); // no need to include 'use strict;'
		//lint.addOption(Option.CONFUSION); // type confusion allowed (e.g. var = "string" + number;)
		lint.addOption(Option.CONTINUE); // continue; statement allowed
		lint.addOption(Option.WHITE); // sloppy white space allowed
		lint.addOption(Option.PLUSPLUS); // ++ and -- allowed
		lint.addOption(Option.REGEXP); // . and [^ ..] allowed in regex
		lint.addOption(Option.PREDEF, "$L,$,Ajax,Mojo,enyo,window,StageAssistant,AppAssistant,Element"); // predefined global variables
    lint.addOption(Option.TODO); // todo comments allowed
		
    if (sources == null) {
      viewerPanel.setPlainTextContent("<h3>This project has no identifiable Javascript sources.</h3>");
      return;
    }

		for (int i = 0; i < sources.length; i++) {
			
			LintSourceListItem li = new LintSourceListItem(sources[i], projectFolder+"/app_src/"+sources[i]);
			
			// syntax: lint(String filename, String javascript)
			// fileOperator method returns BufferedReader which may throw IO exceptions
			try {
				JSLintResult result = lint.lint(sources[i], FileOperator.getFileReader(projectFolder+"/app_src/"+sources[i]) );
				List<Issue> issues = result.getIssues();
				li.setCounterValue( issues.size() );
				// get report, get it styled and store it
				li.setReport( LintOutputStyler.styleReport( result.getReport() ) );
				
				sourceList.addToSourceList(li);
				
				// select item
				setCurrentItem(li);
			}
			catch (java.io.IOException ioe) {}
		} // end of loop
 	}
 	// if no project available inform user
 	else {
 		viewerPanel.setPlainTextContent("<h2>No project has been assigned</h2><p>Please try the JSLint function again from the main window's project info pane.</p>");
 	}
	
  }
}