package webosdevtool;

// Import packages

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;

import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.widgets.WindowUtils;

/**
 * JFrame that holds the main GUI and supports some smaller GUI dialog panels as well.
 */
public class DevtoolFrame extends JFrame
{
	// Global variables for this class
	
	private Devtool devtool;
	
	protected DevtoolMenuBar menuBar = null;
	protected DevtoolBottomBar bottomBar = null;
	protected DevtoolSourceList sourceList = null;
	protected DevtoolActionPanel actionPanel = null;

  /**
   * Creates a new DevtoolFrame service GUI.
   * @param myParent The <code>Devtool</code> service instance we manage a GUI for.
   */
  public DevtoolFrame (Devtool myParent) {

    // Set the title of the JFrame.
    super ("webOS developer tool");

    // Copy from method argument to instance field.
    devtool = myParent;
    
    // prevent the JFrame from disappearing on clicking the close button
	// rather keep it open and show what happens
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    // Create a menu bar to hold the menus.
    menuBar = new DevtoolMenuBar(devtool);
    // Install the menubar.
    setJMenuBar(menuBar);
	
    // Install code to execute for certain window events.
    // Adapter class WindowAdapter...
    addWindowListener (new WindowAdapter () {
		// If the windows is closed, shut dow.
		public void windowClosing (WindowEvent e) {
		  devtool.shutdown ();
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
	sourceList = devtool.devSourceList;
	sourceList.getComponent().setPreferredSize(new Dimension(170,220));
	sourceList.getComponent().setMinimumSize(new Dimension(150,150));
	
	// Actionable panel on right
	actionPanel = new DevtoolActionPanel(myParent);
	
	JSplitPane mainPane = MacWidgetFactory.createSplitPaneForSourceList(sourceList, actionPanel);
	
	// Bottom bar
	bottomBar = new DevtoolBottomBar(BottomBarSize.LARGE, devtool);
	bottomBar.installWindowDraggerOnWindow(this);
	
	JPanel windowPanel = new JPanel(new BorderLayout());
	windowPanel.add(mainPane, BorderLayout.CENTER);
	windowPanel.add(bottomBar.getComponent(), BorderLayout.SOUTH);
	setContentPane(windowPanel);

    // Do qualitative layout
    pack ();

    // Determine actual sizes.
    //Dimension d = Toolkit.getDefaultToolkit ().getScreenSize ();
    // The window is located 1/3th of the screen size from upper left corner.
    setLocation (50,70);
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
   * Minimises the window
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
   * method of a JFrame. It is used to specify anti-aliasing for the drawing.
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
  
  // Generic methods -----------------------------------------------------
  
  protected void setDeviceView(DevSourceItem myCurrentItem) {
  	// update actionpanel labels
	actionPanel.setDeviceName( myCurrentItem.getName() );
	actionPanel.setDeviceID( myCurrentItem.getID() );
	actionPanel.setDeviceVersion( myCurrentItem.getVersion() );
	actionPanel.setDeviceLocation( myCurrentItem.getLocation() );
	actionPanel.setDeviceIcon( myCurrentItem.getLargeIcon() );
	// true if ID equal to selected default device
	Device myDevice = (Device) myCurrentItem;
	actionPanel.setDeviceAsDefault( myDevice.getID() == devtool.currentDevice.getID() );
	actionPanel.setDeviceAsEmulator( myDevice.isEmulator() );
	// update actionpanel view
	actionPanel.switchToDeviceView();
	// update other GUI elements
	menuBar.setDeviceView();
	bottomBar.setDeviceView();
  }
  
  protected void setProjectView(DevSourceItem myCurrentItem) {
  	// update actionpanel labels
	actionPanel.setProjectName( myCurrentItem.getName() );
	actionPanel.setProjectVersion( myCurrentItem.getVersion() );
	actionPanel.setProjectAppID( myCurrentItem.getID() );
	actionPanel.setProjectIcon( myCurrentItem.getLargeIcon() );
	// update actionpanel view
	actionPanel.switchToProjectView();
	// update other GUI elements
	menuBar.setProjectView();
	bottomBar.setProjectView();
  }
  
  // Extra GUI methods -----------------------------------------------------
  
  /**
   * Folder chooser dialog.
   * NOTE: not run in separate thread, will thus block main UI.
   */
  public String showFolderPickerDialog (String windowText) {
  	// special OS X folder chooser property
  	System.setProperty("apple.awt.fileDialogForDirectories", "true");
  	
  	// get user input
  	FileDialog fd = new FileDialog(this, windowText, FileDialog.LOAD);
  	fd.setVisible(true); // blocking UI
  	String[] folderInfo = new String[2];
  	folderInfo[0] = fd.getDirectory();
  	folderInfo[1] = fd.getFile();
  	
  	// finally disable OS X folder property
  	System.setProperty("apple.awt.fileDialogForDirectories", "false");
  	
  	// prepare return value
  	String folder = null;
  	if (folderInfo[0] != null && folderInfo[1] != null) {
  		folder = folderInfo[0] + "" + folderInfo[1];
  	}
  	
  	return folder;
  }
  
  /**
   * New project name textfield dialog.
   * NOTE: not run in separate thread, will thus block main UI.
   */
  public String showInputDialog(String title, String instruction) {
  	
  	// use JOptionPane
  	String input = JOptionPane.showInputDialog(null, instruction, title, JOptionPane.QUESTION_MESSAGE);
  	
  	return input;
  }
  
  /**
   * Shows an informatin dialog with custom info
   */
  public void showInfoDialog(String title, String info) {
  	// use message dialog
	JOptionPane.showMessageDialog(null, info, title, JOptionPane.INFORMATION_MESSAGE);
  }
}
