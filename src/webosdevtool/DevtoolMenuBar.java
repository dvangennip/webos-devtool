/**
 * Adaptation of JMenuBar
 */
package webosdevtool;

import webosdevtool.process.FileOperator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu; //

/**
 * webOSdevtool GUI menu bar.
 */
public class DevtoolMenuBar extends JMenuBar {
    /**
     * Reference to main class
     */
    private Devtool devtool = null;
    
    /**
     * Menu's
     */
    private JMenu fileMenu = null;
	private JMenu recentProjectsMenu = null;
	private JMenu projectMenu = null;
	private JMenu deviceMenu = null;
	private JMenu browserMenu = null;
	private JMenu windowMenu = null;
	private JMenu helpMenu = null;
	
	/**
	 * Menu items
	 */
	private AMenuItem fileMenuNew = null;
	private AMenuItem fileMenuOpen = null;
	private AMenuItem fileMenuClose = null;
	private AMenuItem fileMenuCloseAll = null;
	private AMenuItem projectMenuRevealFinder = null;
	private AMenuItem projectMenuUpdateInfo = null;
	private AMenuItem projectMenuJSLint = null;
	private AMenuItem projectMenuNewScene = null;
	private AMenuItem projectMenuRun = null;
	private AMenuItem projectMenuRunInspectable = null;
	private AMenuItem projectMenuPackage = null;
	private AMenuItem projectMenuInstall = null;
	private AMenuItem projectMenuLaunch = null;
	private AMenuItem projectMenuLaunchInspectable = null;
	private AMenuItem projectMenuClose = null;
	private AMenuItem projectMenuUninstall = null;
	private AMenuItem projectMenuPalmLog = null;
	private AMenuItem projectMenuResourceMonitor = null;
	private AMenuItem projectMenuOpenInBrowser = null;
	private AMenuItem deviceMenuRefresh = null;
	private AMenuItem deviceMenuDeviceChoiceLabel = null;
	private AMenuItem deviceMenuSubReveal = null;
	private AMenuItem deviceMenuSubRLogger = null;
	private AMenuItem deviceMenuSubStart = null;
	private AMenuItem deviceMenuSubHostMode = null;
	private AMenuItem browserMenuChoiceLabel = null;
	private AMenuItem browserMenuOpen = null;
	private ARadioMenuItem browserMenuSafari = null;
	private ARadioMenuItem browserMenuChrome = null;
	private ButtonGroup deviceGroup = null;
	private ButtonGroup browserGroup = null;
	private AMenuItem windowMenuMinimize = null;
	private AMenuItem windowMenuZoom = null;
	private AMenuItem helpMenuManual = null;

    // Constructor
    
    public DevtoolMenuBar (Devtool myParent) {
    	// assign reference to main class and call super constructor
    	super();
    	devtool = myParent;
    	
    	// setup the menubar
        
        // File menu
	    fileMenu = new JMenu("File");  
	    // File > New Project
	    fileMenuNew = new AMenuItem("New Project...", KeyEvent.VK_N);
	    fileMenuNew.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectNew();
			}
	    });
	    fileMenu.add(fileMenuNew);
	    // File > Open
	    fileMenuOpen = new AMenuItem("Open Project", KeyEvent.VK_O);
	    fileMenuOpen.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectOpen(null);
			}
	    });
	    fileMenu.add(fileMenuOpen);
	    // Add a submenu for recent projects
	    recentProjectsMenu = new JMenu("Open Recent Project");
	    AMenuItem recproj = new AMenuItem("No recent projects");
	    recproj.setEnabled(false);
	    recentProjectsMenu.add(recproj);
	    fileMenu.add(recentProjectsMenu);
	    // Separator
	    fileMenu.addSeparator();
	    // File > Close
	    fileMenuClose = new AMenuItem("Close Project", KeyEvent.VK_W);
	    fileMenuClose.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectClose();
			}
	    });
	    fileMenu.add(fileMenuClose);
	    // File > Close All
	    fileMenuCloseAll = new AMenuItem("Close All Projects", true, true, KeyEvent.VK_W);
	    fileMenuCloseAll.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectCloseAll();
			}
	    });
	    fileMenu.add(fileMenuCloseAll);
		
		// Project menu
		projectMenu = new JMenu("Project");
		// Project > Reveal In Finder
		projectMenuRevealFinder = new AMenuItem("Reveal In Finder", KeyEvent.VK_F);
		projectMenuRevealFinder.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectRevealInFinder();
			}
	    });
	    projectMenu.add(projectMenuRevealFinder);
	    // Project > Update info
	    projectMenuUpdateInfo = new AMenuItem("Update Project Data", KeyEvent.VK_U);
		projectMenuUpdateInfo.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectUpdateData();
			}
	    });
	    projectMenu.add(projectMenuUpdateInfo);
	    // Separator
	    projectMenu.addSeparator();
	    // Project > New Scene
		projectMenuNewScene = new AMenuItem("Add New Scene...", true, true, KeyEvent.VK_N);
		projectMenuNewScene.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectNewScene();
			}
	    });
	    projectMenu.add(projectMenuNewScene);
	    // Separator
	    projectMenu.addSeparator();
	    // Project > JSLint
		projectMenuJSLint = new AMenuItem("Scan with JSLint", KeyEvent.VK_J);
		projectMenuJSLint.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectJSLint();
			}
	    });
	    projectMenu.add(projectMenuJSLint);
		// Separator
	    projectMenu.addSeparator();
		// Project > Run
		projectMenuRun = new AMenuItem("Package, Install & Run", KeyEvent.VK_R);
		projectMenuRun.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectRun(false);
			}
	    });
	     projectMenu.add(projectMenuRun);
	    // Project > Run as Inspectable
		projectMenuRunInspectable = new AMenuItem("Package, Install & Run as Inspectable", true, true, KeyEvent.VK_R);
		projectMenuRunInspectable.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectRun(true);
			}
	    });
	    projectMenu.add(projectMenuRunInspectable);
	    // Separator
	    projectMenu.addSeparator();
	    // Project > Package
		projectMenuPackage = new AMenuItem("Package", KeyEvent.VK_1);
		projectMenuPackage.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectPackage();
			}
	    });
	    projectMenu.add(projectMenuPackage); 
	    // Project > Install
		projectMenuInstall = new AMenuItem("Install", KeyEvent.VK_2);
		projectMenuInstall.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectInstall();
			}
	    });
	    projectMenu.add(projectMenuInstall);	    
	    // Project > Launch
		projectMenuLaunch = new AMenuItem("Launch", KeyEvent.VK_3);
		projectMenuLaunch.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectLaunch(false);
			}
	    });
	    projectMenu.add(projectMenuLaunch); 
	    // Project > LaunchInspectable
		projectMenuLaunchInspectable = new AMenuItem("Launch as Inspectable", KeyEvent.VK_4);
		projectMenuLaunchInspectable.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectLaunch(true);
			}
	    });
	    projectMenu.add(projectMenuLaunchInspectable);
	    // Project > Close
		projectMenuClose = new AMenuItem("Close on Device", KeyEvent.VK_5);
		projectMenuClose.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectCloseOnDevice();
			}
	    });
	    projectMenu.add(projectMenuClose);
	    // Project > Uninstall
		projectMenuUninstall = new AMenuItem("Uninstall", KeyEvent.VK_6);
		projectMenuUninstall.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectUninstall();
			}
	    });
	    projectMenu.add(projectMenuUninstall);
	    // Separator
	    projectMenu.addSeparator();
	    // Project > Open in Browser (Enyo only)
	    projectMenuOpenInBrowser = new AMenuItem("Open In Browser", KeyEvent.VK_B);
		projectMenuOpenInBrowser.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectOpenInBrowser();
			}
	    });
	    projectMenu.add(projectMenuOpenInBrowser);
	    // Separator
	    projectMenu.addSeparator();
	    // Project > Palm Log
		projectMenuPalmLog = new AMenuItem("Open Palm-Log Window", KeyEvent.VK_L);
		projectMenuPalmLog.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.projectOpenPalmLog();
			}
	    });
	    projectMenu.add(projectMenuPalmLog);
	    // Project > Resource Monitor
		projectMenuResourceMonitor = new AMenuItem("Open Resource Monitor", KeyEvent.VK_K);
		projectMenuResourceMonitor.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.openResourceMonitor("currentitem");
			}
	    });
	    projectMenu.add(projectMenuResourceMonitor);
	    
	    // Device menu
	    deviceMenu = new JMenu("Devices");
	    // Device > Refresh device list
		deviceMenuRefresh = new AMenuItem("Refresh Device List", KeyEvent.VK_F5, true);
		deviceMenuRefresh.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.deviceRefreshList();
			}
	    });
	    deviceMenu.add(deviceMenuRefresh);
	    // Separator
	    deviceMenu.addSeparator();
	    // Clearifying label on top of device list
	    deviceMenuDeviceChoiceLabel = new AMenuItem("Set Default Device Below");
	    deviceMenuDeviceChoiceLabel.setEnabled(false);
	    deviceMenu.add(deviceMenuDeviceChoiceLabel);
	    // a group of radio button menu items (for individual devices)
		deviceGroup = new ButtonGroup();
		// Separator
	    deviceMenu.addSeparator();
		// ---Device menu items
		// Device > Reveal In Finder
		deviceMenuSubReveal = new AMenuItem("Reveal In Finder");
		deviceMenuSubReveal.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.deviceRevealInFinder("currentitem");
			}
	    });
	    deviceMenu.add(deviceMenuSubReveal);
	    // Device > Open Resource Logger
	    deviceMenuSubRLogger = new AMenuItem("Open Resource Monitor");
		deviceMenuSubRLogger.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.openResourceMonitor("currentitem");
			}
	    });
	    deviceMenu.add(deviceMenuSubRLogger);
	    // Emulator > Start
		deviceMenuSubStart = new AMenuItem("Start Device");
		deviceMenuSubStart.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.deviceStart("currentitem");
			}
	    });
	    deviceMenu.add(deviceMenuSubStart);		    
		// Emulator > Enable Host Mode
		deviceMenuSubHostMode = new AMenuItem("Enable Emulator Host Mode");
		deviceMenuSubHostMode.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.deviceEnableHostMode("currentitem");
			}
	    });
	    deviceMenu.add(deviceMenuSubHostMode);
	    
	    // Browsers as device menu
	    browserMenu = new JMenu("Browsers");
	    // Clearifying label on top of device list
	    browserMenuChoiceLabel = new AMenuItem("Set Default Browser For Enyo Below");
	    browserMenuChoiceLabel.setEnabled(false);
	    browserMenu.add(browserMenuChoiceLabel);
	    // a group of radio button menu items (for browser default choice)
		browserGroup = new ButtonGroup();
	    // Browsers > Open Safari
   	    //		only add when available
   	    if ( FileOperator.fileExists("/Applications/Safari.app") ) {
			browserMenuSafari = new ARadioMenuItem("Safari");
			browserMenuSafari.addActionListener (new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					setDefaultWebkitBrowser("safari");
				}
		    });
		    browserGroup.add( browserMenuSafari );
		    browserMenu.add( browserMenuSafari );
   	    }
	    // Browsers > Open Google Chrome
	    //		only add when available
	    if ( FileOperator.fileExists("/Applications/Google Chrome.app") ) {
			browserMenuChrome = new ARadioMenuItem("Google Chrome");
			browserMenuChrome.addActionListener (new ActionListener () {
				public void actionPerformed (ActionEvent e) {
					setDefaultWebkitBrowser("chrome");
				}
		    });
		    browserGroup.add( browserMenuChrome );
		    browserMenu.add( browserMenuChrome );
		}
   	    // Separator
	    browserMenu.addSeparator();
	    // Browsers > Open
	    browserMenuOpen = new AMenuItem("Open Default Browser");
	    browserMenuOpen.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.openWebkitBrowser();
			}
	    });
		browserMenu.add(browserMenuOpen);
				
		// Window menu
		windowMenu = new JMenu("Window");
		// Window > Minimize
		windowMenuMinimize = new AMenuItem("Minimize", KeyEvent.VK_M);
		windowMenuMinimize.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.windowMinimize();
			}
	    });
	    windowMenu.add(windowMenuMinimize);
		// Window > Zoom
		windowMenuZoom = new AMenuItem("Zoom");
		windowMenuZoom.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.windowZoom();
			}
	    });
	    windowMenu.add(windowMenuZoom);
		
		// Help menu
		helpMenu = new JMenu("Help");
		// Help > Manual
		helpMenuManual = new AMenuItem("Show Manual");
		helpMenuManual.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.windowShowHelp();
			}
	    });
	    helpMenu.add(helpMenuManual);
	    
	    this.add(fileMenu);
	    this.add(projectMenu);
	    this.add(deviceMenu);
	    this.add(browserMenu);
	    this.add(windowMenu);
	    this.add(helpMenu); 
    }
    
    // Methods ----------------------------------------------------------
    
    /**
     * Takes care of setting up the menu correctly for focus on project
     * while disabling non-sensible menu items.
     */
    protected void setDeviceView() {
    	// only close single (current) project makes no sense
    	fileMenuClose.setEnabled(false);
    	adjustDeviceMenu(true);
    	adjustProjectMenu(false);
    }
    
    /**
     * Takes care of setting up the menu correctly for focus on project
     */
    protected void setProjectView() {
    	// enable everything that might be disabled
    	fileMenuClose.setEnabled(true);
    	adjustDeviceMenu(false);
    	adjustProjectMenu(true);
    }
    
    /**
     * Sets up the Close All Projects menu item.
     * Should not be enabled when there are no projects available.
     */
    protected void setCloseAllProjectsView(int numberOfProjects) {
    	if (numberOfProjects > 0) {
    		fileMenuCloseAll.setEnabled(true);
    	} else {
    		fileMenuCloseAll.setEnabled(false);
    	}
    }
    
    /**
     * Enables or disables certain device menu items (e.g. those that are only relevant when
     * an appropriate device is selected).
     * @param enableStatus Boolean to indicate whether the menu will be enabled or disabled.
     */
    private void adjustDeviceMenu(boolean enableStatus) {
    	
	    // figure out if currentitem is a) a Device, and b) an emulator
	    // if so, relevant items can be enabled
	    boolean enableEmulatorItems = false;
	    if (devtool.currentItem.isDevice() ) {
	    	Device d = (Device) devtool.currentItem;
	    	enableEmulatorItems = d.isEmulator();
	    }
	    
	    // loop over all per-device items in deviceMenu
    	int numberOfItems = deviceMenu.getMenuComponentCount();
    	int startIndex = devtool.getNumberOfDevices() + 4; // number of generic previous menu items
    	int emulatorItemStartIndex = startIndex + 2; // number of generic device items
    	
    	for (int i = startIndex; i < numberOfItems; i++) {
    		// if item is a separator getItem() returns null
    		// which in turn throws an exception when invoking setter method
    		try {
    			// for normal items
    			if (i < emulatorItemStartIndex) {
    				deviceMenu.getItem(i).setEnabled( enableStatus );
    			}
    			// for emulator items
    			else {
    				deviceMenu.getItem(i).setEnabled( enableEmulatorItems );
    			}
    			
    		}
    		catch (java.lang.NullPointerException npe) {}
    	}
    }
    
    /**
     * Enables or disables the project menu
     * @param enableStatus Boolean to indicate whether the menu will be enabled or disabled.
     */
    private void adjustProjectMenu(boolean enableStatus) {
	    // loop over all items in projectMenu
    	int numberOfItems = projectMenu.getMenuComponentCount();
    	for (int i = 0; i < numberOfItems; i++) {
    		// if item is a separator getItem() returns null
    		// which in turn throws an exception when invoking setter method
    		try {
    			projectMenu.getItem(i).setEnabled( enableStatus );
    		}
    		catch (java.lang.NullPointerException npe) {}
    	}
    }
    
    /**
     * Method sets the input device as the selected one in the device menu.
     */
    protected void setCurrentDevice(Device d) {
    	d.getMenuItem().setSelected(true);
    }
    
    /**
     * Creates and adds a device menu item to the Device menu.
     * @param d Device instance to add the menu item for.
     * @return The newly created menu item instance is returned for future use.
     */
    protected ARadioMenuItem addDeviceToMenu(Device d) {
    	
    	final String myID = d.getID();
    	
    	// create this device's RadioButton menu item
    	ARadioMenuItem deviceSelectorItem = new ARadioMenuItem( d.getName() );
		deviceSelectorItem.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
			  devtool.setCurrentDevice( myID );
			}
	    });
	    
	    // add this device's menu item to radio button group
		deviceGroup.add( deviceSelectorItem );
		// add item to device menu after other devices
		int enterAtIndex = devtool.getNumberOfDevices() + 3;
		deviceMenu.add( deviceSelectorItem, enterAtIndex );
	    
	    // return the menu item so a Device instance can save it for future reference
		return deviceSelectorItem;
    }
    
    /**
     * Removes a device menu item from the Device menu.
     * @param menuitem The menu item reference that was passed by calling the <code>addDeviceToMenu</code> method.
     */
    protected void removeDeviceFromMenu(ARadioMenuItem menuitem) {
    	
    	if (menuitem != null) {
    		deviceGroup.remove( menuitem );
    		deviceMenu.remove( menuitem );
    	}
    }
    
    /**
     * Sets a clicked menu item as selected and calls <code>Devtool</code> instance to set respective browser as default.
     *
	 * @param browser The browser to open as a <code>String</code> value (e.g. <code>chrome</code> or <code>safari</code>).
	 */
    protected void setDefaultWebkitBrowser(String browser) {
    	    	
    	// set menu item as selected
    	if (browser != null) {
			if ( browser.equals("safari") ) {
				if (browserMenuSafari != null) {
					browserMenuSafari.setSelected(true);
				}
			} else if ( browser.equals("chrome") ) {
				if (browserMenuChrome != null) {
					browserMenuChrome.setSelected(true);
				}
			}
	    	
	    	// call main class to set browser as default
	    	devtool.setDefaultWebkitBrowser(browser);
    	} else {
    		// nothing is selected
    		// NOTE: should not occur
    		browserGroup.clearSelection();
    	}
    }
}