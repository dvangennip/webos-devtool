package webosdevtool;

// Import packages

import webosdevtool.help.DevtoolHelpWindow;
import webosdevtool.process.FileOperator;
import webosdevtool.process.Task;
import webosdevtool.process.TaskManager;
import webosdevtool.jslint.LintFrame;

import java.util.Vector;

/**
 * This webOS developer tool simplifies the use of HP's command line tools for packaging, installing and logging.
 * Essentially this software forms a GUI wrapper around those command line tools and offers similar functionality.
 * <br />
 * Devtool is the main class which coordinates the lists of devices and projects, and handles keyevents and
 * selected menu items. In turn it triggers the appropriate GUI updates via a <code>DevtoolFrame</code> instance.
 * Tasks are delegated to a <code>TaskManager</code> instance.
 * 
 * @author Doménique van Gennip, <a href="http://www.sinds1984.nl/">Sinds1984.nl</a>
 * @version 0.6
 */
public class Devtool {
	
	// Global variables
	
	/**
	 * Variable is true when instance desires to exit.
	 */
	public boolean exitStatus = false;

	/**
	 * Variable is true when log is redirected to the main window's log pane
	 */
	boolean loggingEnabled = false;
	
	/**
	 * Separate class to manage tasks
	 */
	public TaskManager taskManager;
	
	/**
	 * Reference to DevFileOperator helper class
	 */
	public FileOperator fileOperator;
	
	/**
	 * Reference to separate JSLint results window
	 */
	public LintFrame jslintWindow;
	
	/**
	 * Window frame used by this devtool instance
	 */
	public DevtoolFrame devwindow;
	
	/**
	 * SourceListModel used throughout program. Should be available everywhere.
	 */
	protected DevtoolSourceList devSourceList;
	
	/**
	 * Flexible size lists of devices and projects
	 */
	protected Vector devices = new Vector();
	protected Vector projects = new Vector();
	
	/**
	 * Variable that holds the currently selected device or project.
	 * Any action is done on this item.
	 */
	protected DevSourceItem currentItem;
	
	/**
	 * Variable that holds the currently selected device.
	 * Any action is done with this device as destination.
	 */
	protected Device currentDevice;
	
	/**
	 * Name of the default webkit browser to use for opening Enyo apps.
	 */
	private String defaultWebkitBrowser;
	
	// Constructor ------------------------------------------------------------
	
	/**
	 * Default constructor.
	 * It creates instances of a FileOperator, TaskManager, DevtoolSourceList, and DevtoolFrame.
	 * Also the device and project lists are initiated.
	 */
	public Devtool() {
		
		// init file operator helper
		fileOperator = new FileOperator();
		
		// init task manager
		taskManager = new TaskManager(this);
		
		// init sourceListModel
		devSourceList = new DevtoolSourceList(this);
		
		// init window frame
		devwindow = new DevtoolFrame(this);
		
		// add default devices
		this.deviceAdd("emulator", "0", "tcp"); // emulator
		
		// set default device and view
		try {
			Device defaultItem = (Device) devices.firstElement();
			
			setCurrentDevice( defaultItem );
			setCurrentItem( defaultItem );
		}
		catch (java.util.NoSuchElementException nsee) {}
		this.devwindow.menuBar.setCloseAllProjectsView( projects.size() );
		
		// set default browser
		this.devwindow.menuBar.setDefaultWebkitBrowser("chrome");
		
		// START GATHERING INFO ----------
		
		this.setLogState(true);
		this.deviceRefreshList();
	}
	
	// Methods ------------------------------------------------------------
	
	/**
	 * Update the current focus of program actions.
	 * Calls for GUI updates as well to reflect the change.
	 */
	protected void setCurrentItem(DevSourceItem selectedItem) {
		// check and set
		if (selectedItem != null) {
			currentItem = selectedItem;
		}
		
		// update GUI
		
		// explicitly set the item as selected
		// to reduce confusion when program automatically selects an item
		try {
			devSourceList.setSelectedItem( currentItem.getSourceListItem() );
		}
		catch (java.lang.IllegalArgumentException iae) {}
		
		// update action panel
		if ( currentItem.isDevice() ) {
			devwindow.setDeviceView(currentItem);			
		} else {
			devwindow.setProjectView(currentItem);
		}
	}
	
	/**
	 * Updates the current selection of a device as a destination for any program actions.
	 * For example packages will be installed on this device when a user selects that option.
	 * NOTE: alternative versions of <code>setCurrentDevice</code> all use this core method.
	 * @param selectedDevice Specifies a device to set as default.
	 */
	protected void setCurrentDevice(Device selectedDevice) {
		// check and set
		if (selectedDevice != null) {
			// set
			currentDevice = selectedDevice;
			
			// update menu item via device instance
			currentDevice.setSelected(true);
			
			// update action panel
			if (currentItem != null) { // on init this may cause a NullPointerException if not checked
				if ( currentItem.equals( currentDevice ) ) {
					devwindow.actionPanel.setDeviceAsDefault(true);
				} else {
					devwindow.actionPanel.setDeviceAsDefault(false);
				}
			}
		}
	}
	
	/**
	 * Updates the current selection of a device as a destination for any program actions.
	 * For example packages will be installed on this device when a user selects that option.
	 * Use a String of value '0' to set the emulator in a.o. actionlisteners that require a final ID
	 * (as an alternative to a real, possibly fluctuating emulator nduid which works just as well).
	 * The emulator ID may fluctuate due to it closing and relaunching and 0 provides a stable reference.
	 *
	 * @param identifier Specifies a device by its unique ID (nduid) as String.
	 */
	protected void setCurrentDevice(String identifier) {
		// use a workaround for situations where emulator related events
		if ( identifier.equals("0") ) {
			identifier = "emulator";
		}
		// feed similar method with a device instance
		setCurrentDevice( getDevice(identifier) );
	}
	
	/**
	 * Updates the current selection of a device as a destination for any program actions.
	 * For example packages will be installed on this device when a user selects that option.
	 * Takes no parameters, therefore this method takes the currently selected item (checks if it is a device).
	 */
	protected void setCurrentDevice() {
		if ( currentItem.isDevice() ) {
			Device d = (Device) currentItem;
			setCurrentDevice( d );
		}
	}
	
	/**
	 * Updates the current selection of a device as a destination for any program actions.
	 * This particular method allows an index integer to be passed, thus an index of 0 means
	 * the first device in the devices Vector list will be selected. A value of 1 means the
	 * second, and so on.
	 * This method is essentially a wrapper for the method above, having a different input mechanism.
	 * @param deviceIndex Index of the device to select.
	 */
	protected void setCurrentDevice(int deviceIndex) {
		
		// try to get a valid device
		// if found call above method to set it
		try {
			Device indexItem = (Device) devices.elementAt( deviceIndex );
			setCurrentDevice( indexItem );
		}
		catch (java.util.NoSuchElementException nsee) {
			System.out.println("Error: device index does not exist.");
		}
	}
	
	/**
	 * Update DevSourceItem view
	 */
	public void updateCurrentItem() {
		// update GUI / action panel
		if ( currentItem.isDevice() ) {
			devwindow.setDeviceView(currentItem);			
		} else {
			devwindow.setProjectView(currentItem);
		}
	}
	
	/**
	 * If the specified item is in view its info and representation gets
	 * updated to reflect any changes. If it is indeed in view this method calls <code>updateCurrentItem</code>,
	 * else it does not do anything.
	 * It is advised changing methods call this method at the end to make sure the UI
	 * reflects the actual underlying information and state of the item.
	 *
	 * @param item A DevSourceItem instance (e.g. a project or device)
	 */
	public void updateItem (DevSourceItem item) {
		// any of the two object may be zero, exceptions can be ignored here (equals false result)
		try {
			if ( item.equals(currentItem) ) {
				updateCurrentItem();
			}
		}
		catch (java.lang.NullPointerException npe) {}
	}
	
	/**
	 * Method returns number of devices currently available in the list.
	 * @return Number of devices as int value.
	 */
	public int getNumberOfDevices() {
		synchronized(devices) {
			return devices.size();
		}
	}
	
	/**
	 * Method returns number of projects currently available in the list.
	 * @return Number of projects as int value.
	 */
	public int getNumberOfProjects() {
		synchronized(projects) {
			return projects.size();
		}
	}
	
	// Project methods --------------------------------------------
	
	/**
	 * Updates the data of a project based on its files, etc.
	 * @see webosdevtool.Project
	 */
	public void projectUpdateData() {
		// update item
		currentItem.update(); // asks for view update there
	}
	
	/**
	 * Opens a project. If successful it is added to the list.
	 */
	public void projectOpen(String pFolder) {
		System.out.println("\nOpening a project");
		
		String projectFolder = pFolder;
		// if folder was not defined open a dialog
		if (projectFolder == null) {
			projectFolder = devwindow.showFolderPickerDialog("Select Project Folder");
		}
		
		// check whether project folder is defined by now
		// if so, check validity and open
		if (projectFolder != null) {

			synchronized(projects) {
				// check if no project is open with such folder
				for (int i = 0; i < projects.size(); i++) {
					Project p = (Project) projects.elementAt(i);
					if (p.getLocation().equals(projectFolder)) {
						System.out.println("Project has already been opened: see " + p.getName());
						return;
					}
				}
				
				// when project is OK
				if ( fileOperator.checkProjectFolderValidity(projectFolder) ) {
					
					System.out.println("Folder valid: "+projectFolder);
						
					// add project to list
					projects.add(new Project(this, null, projectFolder));
					this.devwindow.menuBar.setCloseAllProjectsView( projects.size() );
					
					// view the new project
					try {
						setCurrentItem( ((DevSourceItem) projects.lastElement() ) );
					}
					catch (java.util.NoSuchElementException nsee) {}
				} else {
					// TODO add info dialog
					System.out.println("Folder NOT valid: "+projectFolder);
				}
			}
		}
	}
	
	/**
	 * Close a project. It will close the currently selected project.
	 */
	public void projectClose() {
		System.out.println("\nClosing a project");
		
		boolean success = false;
		
		// check if it is safe to remove
		// that is, no tasks scheduled for this project
		if ( taskManager.getNumberOfTasksForItem( currentItem ) == 0) {
			
			synchronized(projects) {
				// get index for later use (switch view to another, nearby item)
				int currentIndex = projects.indexOf(currentItem);
				// call dispose methods if available
				currentItem.dispose();
				// remove project from Vector list
				projects.remove(currentItem);
				
				// switch view
				DevSourceItem refocusItem = null;
				// define whereto
				if ( projects.size() == 0) {
					// no projects, select first device instead (Emulator is always there)
					refocusItem = (DevSourceItem) devices.firstElement();
				} else {
					// switch to project above currently removed one
					if (currentIndex > 0) {
						currentIndex--;
					}
					refocusItem = (DevSourceItem) projects.elementAt( currentIndex );
				}
				// set new view
				setCurrentItem( refocusItem );
			}
			
			// TODO - catch non-success cases
			success = true;
		}
		
		// if unsuccessful show it to the user
		if (!success) {
			// TODO - show error dialog
		}
		
		// update GUI
		this.devwindow.menuBar.setCloseAllProjectsView( projects.size() );
	}
	
	/**
	 * Close all projects. It will close the currently selected project.
	 */
	public void projectCloseAll() {
		System.out.println("\nClosing all projects");
		
		// for length of projects vector
		// count down as size diminishes when closing
		for (int i = projects.size(); i >= 0; i--) {
			try {
				currentItem = ((DevSourceItem) projects.elementAt(i) );
				projectClose();
			}
			catch (java.lang.Exception e) {
				// element may not exist, causing an error
				// no need to act, project cannot be closed anymore
			}
		}
	}
	
	/**
	 * Generate a project.
	 */
	public void projectNew() {
		System.out.println("\nNew project...");
		// get a destination folder
		String destinationFolder = devwindow.showFolderPickerDialog("Select Destination Folder");
		
		// if a folder is found
		if (destinationFolder != null) {
			
			// ask for project name
			String projectName = devwindow.showInputDialog("New project name", "Enter project name");
			
			// if name is given
			if (projectName != null) {
				
				// create arguments
				String[] args = new String[2];
				args[0] = destinationFolder;
				args[1] = projectName;
				
				// create task with arguments
				taskManager.addTask( new Task(Task.PROJECT_NEW, null, null, args) );
			}
		}
	}
	
	/**
	 * Generate a new scene for an existing project.
	 */
	public void projectNewScene() {
		System.out.println("\nNew scene...");

		if (currentItem.isEnyoEnabled()) {
			System.out.println("Project is based on Enyo, only Mojo projects can have scenes added.");
			return;
		}
		
		// ask for project name
		String sceneName = devwindow.showInputDialog("New scene name", "Enter scene name");
			
		// if name is given
		if (sceneName != null) {
			
			// create arguments
			String[] args = new String[1];
			args[0] = sceneName;
			
			// create task with arguments
			taskManager.addTask( new Task(Task.PROJECT_NEW_SCENE, currentItem, null, args) );
		}
	}

	/**
	 *
	 */
	public void projectDeploy() {
		System.out.println("\nDeploying a project");
		
		taskManager.addTask( new Task(Task.PROJECT_DEPLOY, currentItem, null) );
	}
	
	/**
	 *
	 */
	public void projectPackage() {
		System.out.println("\nPackaging a project");
		
		taskManager.addTask( new Task(Task.PROJECT_PACKAGE, currentItem, null) );
	}
	
	/**
	 *
	 */
	public void projectInstall() {
		System.out.println("\nInstalling a project");
		
		taskManager.addTask( new Task(Task.PROJECT_INSTALL, currentItem, currentDevice) );
	}
	
	/**
	 * Launches a project's application on a device. The inspectable status is deprecated in webOS 2+.
	 *
	 * @param inspectable True if the project should be launched as inspectable, that is to be inspected with the Palm Inspector (deprecated for SDK versions 2+)
	 */
	public void projectLaunch() {
		System.out.println("\nRunning a project");
		
		taskManager.addTask( new Task(Task.PROJECT_LAUNCH, currentItem, currentDevice) );
	}
	
	/**
	 *
	 */
	public void projectCloseOnDevice() {
		System.out.println("\nClosing a project on device");
		
		taskManager.addTask( new Task(Task.PROJECT_CLOSE, currentItem, currentDevice) );
	}
	
	/**
	 *
	 */
	public void projectUninstall() {
		System.out.println("\nUninstalling a project");
		
		taskManager.addTask( new Task(Task.PROJECT_UNINSTALL, currentItem, currentDevice) );
	}
	
	/**
	 * 
	 */
	public void projectRun() {
		System.out.println("\nRunning a project");
		
		taskManager.addTask( new Task(Task.PROJECT_RUN, currentItem, currentDevice) );
	}
	
	/**
	 *
	 */
	public void projectOpenPalmLog() {
		System.out.println("\nOpening palm-log window");
		
		taskManager.addTask( new Task(Task.PROJECT_OPEN_LOGGER, currentItem, currentDevice) );
	}
	
	/**
	 * Method opens a new Finder window focused on the project folder.
	 */
	public void projectRevealInFinder() {
		System.out.println("\nRevealing project in Finder");
		
		taskManager.addTask( new Task(Task.PROJECT_REVEAL, currentItem, null) );
	}
	
	/**
	 *
	 */
	public void projectJSLint() {
		System.out.println("\nScanning a project with JSLint");
		
		taskManager.addTask( new Task(Task.PROJECT_JSLINT, currentItem, null) );
	}
	
	/**
	 * A Enyo app only method which tries to open the index.html file in /app_src/ root folder
	 * in a webkit browser.
	 */
	public void projectOpenInBrowser() {
		System.out.println("\nOpening an Enyo project in browser");
		
		taskManager.addTask( new Task(Task.OPEN_PROJECT_IN_BROWSER, currentItem, null) );
	}
	
	// Enyo in browser methods ---------------------------------------------------
	
	/**
	 * Sets the default webkit browser to use for opening Enyo apps in.
	 * Currently only Google Chrome and Safari are supported.
	 *
	 * @param browser The browser to open as a <code>String</code> value (e.g. <code>chrome</code> or <code>safari</code>).
	 */
	public void setDefaultWebkitBrowser(String browser) {
		// check input
		if (browser != null) {
			// set variable
			this.defaultWebkitBrowser = browser;
		}
	}
	
	/**
	 * @return <code>String</code> value of default webkit browser name to use for opening Enyo apps in.
	 */
	public String getDefaultWebkitBrowser() {
		return defaultWebkitBrowser;
	}
	
	/**
	 * Opens webkit browser of choice with the correct flags for Enyo simulation to work.
	 * Or rather this method assigns this task to the TaskManager.
	 */
	public void openWebkitBrowser() {
		System.out.println("\nOpening default webkit browser");
		
		// create arguments
		String[] args = new String[1];
		args[0] = this.defaultWebkitBrowser;
		
		// create task with arguments
		taskManager.addTask( new Task(Task.OPEN_WEBBROWSER, null, null, args) );
	}
	
	// Device methods --------------------------------------------
	
	/**
	 * Method returns a device based on its identifier (nduid) as String.
	 * Taking the current item in view can be done by specifying the identifier as '<code>currentitem</code>'.
	 * Specifying the emulator can be done by specifying the identifier as '<code>emulator</code>'.
	 * Because it returns <code>null</code> if no device is found, this method can be used to check if a device
	 * is in the device list.
	 * @param identifier Device unique ID (nduid), <code>null</code> will return default device, '<code>currentitem</code>' will return the device currently in view, and '<code>emulator</code>'.
	 * @return Device instance or <code>null</code> if no device could be found with the specified ID.
	 */
	protected Device getDevice(String identifier) {
		
		if (identifier == null) {
			return currentDevice; // default
		}
		else if ( identifier.equals("currentitem") && currentItem.isDevice() ) {
			// use current item as device
			Device d = (Device) currentItem;
			return d;
		}
		else if ( identifier.equals("emulator") ) {
			// use emulator as device
			
			// search for emulator
			synchronized(devices) {
				for (int i = 0; i < devices.size(); i++) {
					Device d = (Device) devices.elementAt(i);
					if ( d.isEmulator() ) {
						return d; // return this emulator device
					}
				}
			}
			
			return null; // if no emulator found
		}
		else {
		
			// make sure devices vector is not manipulated concurrently
			synchronized(devices) {
				
				// check whether it already exists in the list			
				Device listedDevice = null;
				
				for (int i = 0; i < devices.size(); i++) {
					// cast device
					listedDevice = (Device) devices.elementAt(i);
					// use unique ID String to check similarity
					if ( listedDevice.getID().equals( identifier ) ) {
						// if found we can return and exit immediately
						return listedDevice;
					}
				}
			}
		}
		
		// return null if an instance hasn't been returned above
		// essentially it means no such Device is available for this identifier
		return null;
	}
	
	/**
	 * Method handles the adding of a device and related matters, such as updating
	 * the menubar and other GUI elements. If the specified device is already found in the current list
	 * it will not be added to avoid doubles. This checking is based on the <code>identifier</code>.
	 * However, the existing item will be updated with the fresh data to reflect potential changes.
	 *
	 * @param name Device name as String
	 * @param identifier HP webOS device unique ID (nduid) as String
	 * @param location Physical address of the device (e.g., 'tcp 55234') as String
	 */
	public void deviceAdd(String name, String identifier, String location) {
		
		// make sure devices vector is not manipulated concurrently
		synchronized(devices) {
			
			// get the device
			Device d;
			if ( name.equals("emulator") ) {
				d = getDevice("emulator"); // returns null if not found
			} else {
				d = getDevice(identifier); // returns null if not found
			}
			
			// add device if not yet available
			if ( d == null ) {
				// add it to vector
				devices.add( new Device(this, name, identifier, location) );
				// get version and installed apps
				this.deviceListApplications(identifier);
			} else {
				// if available just update data
				d.setID(identifier); // useful for emulators, no change for other devices
				d.setLocation(location);
				// get version and installed apps
				this.deviceGetLogLevel(identifier);
				this.deviceListApplications(identifier);
			}
			
			// if device was in view update to reflect any changes
			this.updateItem(d);
		}
	}
	
	/**
	 * Method handles the removal of a device and related matters, such as removing
	 * the menubar choices and other GUI elements. Note that emulator devices cannot be removed.
	 * It first checks that a device is not selected as the default device, if so the emulator is given
	 * this role so the device can be removed. Also a device that has tasks related to it cannot be removed.
	 *
	 * @param identifier Unique ID of the device (nduid) which is used to determine which device to remove.
	 */
	public void deviceRemove(String identifier) {
		
		// make sure devices vector is not manipulated concurrently
		synchronized(devices) {
			
			// check whether it already exists in the list
			boolean foundHere = false;
			
			Device listedDevice = getDevice(identifier);
			
			if (listedDevice != null) {
				foundHere = true;
			}
			
			// remove device if found
			if (foundHere) {
				
				// firstly, make sure it is not used for upcoming tasks
				// that is, no tasks scheduled for this project
				if ( taskManager.getNumberOfTasksForItem( listedDevice ) == 0) {
					
					// secondly, make sure if it is in view
					
					boolean inView = false;
					// get index for later use (switch view to another, nearby item)
					int currentIndex = devices.indexOf( listedDevice );
					// check if in view
					if ( listedDevice.getID().equals( currentItem.getID() ) ) {
						inView = true;
						
						// switch to device above currently removed one
						if (currentIndex > 0) {
							currentIndex--;
						}
					}
					
					// if it is a default device this position is transferred
					
					// if default device, return this role to emulator
					if ( listedDevice.getID().equals( currentDevice.getID() ) ) {
						setCurrentDevice(0);
					}
					
					// ACTUAL REMOVAL
					// emulator does not allow removal
					if ( listedDevice.isEmulator() ) {
						// reset emulator
						listedDevice.setID("0");
						listedDevice.setLocation(null);
						listedDevice.setVersion(null);
						listedDevice.setInstalledApps(null);
						// update if in view
						this.updateItem( listedDevice );
					} else {
						// REMOVE
						// call dispose method
						listedDevice.dispose();
						// finally remove it from vector
						devices.remove( listedDevice );
						
						// switch view if necessary
						if (inView) {
							// switch view
							DevSourceItem refocusItem = (DevSourceItem) devices.elementAt( currentIndex );
							// set new view
							setCurrentItem( refocusItem );
						}
					} // end removal
				} // end if no tasks for device
			} // end if found
			
		} // end synchronized block
	}
	
	/**
	 * @return A String array of device identifiers (nduid) currently in the list.
	 */
	public String[] getAvailableDeviceIDs() {
		
		String[] idList = new String[devices.size()];
		
		synchronized(devices) {
			for (int i = 0; i < devices.size(); i++) {
				// cast device
				Device d = (Device) devices.elementAt(i);
				// store ID
				idList[i] = d.getID();
			}
		}
		
		return idList;
	}
	
	/**
	 * Method triggers a refresh / new scan of the currently available devices
	 * for use in the software.
	 */
	public void deviceRefreshList() {
		System.out.println("\nRefreshing device list...");
		
		taskManager.addTask( new Task(Task.DEVICE_SCAN, null, null) );
	}
	
	/**
	 * Method starts the device if it is a software device, such as the emulator.
	 * @param identifier String value, if <code>null</code> it will use the default device,
	 *			   if the String equals 'currentitem' it will use the currently selected item as its device.
	 */
	public void deviceStart(String identifier) {
		System.out.println("\nStarting a device");
		
		// getDevice() will decide on which device to focus on by identifier
		Device deviceToUse = getDevice(identifier);
		
		if (deviceToUse == null) {
			// TODO no device found warning message
		} else {
			// add task
			taskManager.addTask( new Task(Task.DEVICE_START, null, deviceToUse) );
		}
	}
	
	/**
	 * @param identifier String value, if <code>null</code> it will use the default device,
	 *			   if the String equals 'currentitem' it will use the currently selected item as its device.
	 *			   An identifier equal to '0' implies a disabled emulator and will thus reset any data.
	 */
	public void deviceListApplications(String identifier) {
		System.out.println("\nListing applications on a device");
		
		// getDevice() will decide on which device to focus on by identifier
		Device deviceToUse = getDevice(identifier);
		
		if (deviceToUse == null) {
			// TODO no device found warning message
		} else if ( !identifier.equals("0") ) {
			// disabled emulators do get nothing, preventing possible errors during task handling
			// add task
			taskManager.addTask( new Task(Task.DEVICE_LIST_APPS, null, deviceToUse) );
		}
	}
	
	/**
	 * Method opens a new Finder window focused on the project folder.
	 * @param identifier String value, if <code>null</code> it will use the default device,
	 *			   if the String equals 'currentitem' it will use the currently selected item as its device.
	 */
	public void deviceRevealInFinder(String identifier) {
		System.out.println("\nRevealing device in Finder");
		
		// getDevice() will decide on which device to focus on by identifier
		Device deviceToUse = getDevice(identifier);
		
		if (deviceToUse == null) {
			// TODO no device found warning message
		} else {
			// add task
			taskManager.addTask( new Task(Task.DEVICE_REVEAL, null, deviceToUse) );
		}
	}
	
	/**
	 * @param identifier String value, if <code>null</code> it will use the default device,
	 *			if the String equals 'currentitem' it will use the currently selected item as its device.
	 */
	public void deviceEnableHostMode(String identifier) {
		System.out.println("\nEnabling Emulator Host Mode");
		
		// getDevice() will decide on which device to focus on by identifier
		Device deviceToUse = getDevice(identifier);
		
		if (deviceToUse == null) {
			// TODO no device found warning message
		} else {
			taskManager.addTask( new Task(Task.DEVICE_ENABLE_HOST_MODE, null, deviceToUse) );
		}
	}

	/**
	 * Because of the asynchronous nature of the task, this method cannot immediately return any value.
	 * TODO: this does not work at the moment, as there is no console command to get info.
	 *
	 * @param identifier String value, if <code>null</code> it will use the default device,
	 *			if the String equals 'currentitem' it will use the currently selected item as its device.
	 */
	public void deviceGetLogLevel(String deviceID) {
		System.out.println("\nGetting device log level is not yet implemented.");
		// // getDevice() will decide on which device to focus on by identifier
		// Device deviceToUse = getDevice(deviceID);

		// if (deviceToUse == null) {
		// 	System.out.println("\nNo device selected or available to get log level.");
		// } else {
		// 	taskManager.addTask( new Task(Task.DEVICE_LOG_LEVEL, null, deviceToUse, null) );
		// }
	}

	/**
	 * Sets log level for a device
	 *
	 * @param identifier String value, if <code>null</code> it will use the default device,
	 *			if the String equals 'currentitem' it will use the currently selected item as its device.
	 * @param level The log level to be set as <code>String</code>.
	 */
	public void deviceSetLogLevel(String deviceID, String level) {
		if (level == null) {
			System.out.println("\nNo log level was given, thus no device log level set.");
		} else {
			System.out.println("\nSetting device log level to: " + level);
			
			// getDevice() will decide on which device to focus on by identifier
			Device deviceToUse = getDevice(deviceID);

			if (deviceToUse == null) {
				System.out.println("\nNo device selected or available to set log level.");
			} else {
				deviceToUse.setLogLevel(level, false);
				String[] args = { level };
				taskManager.addTask( new Task(Task.DEVICE_LOG_LEVEL, null, deviceToUse, args) );
			}
		}
	}
	
	// Resource Monitor ---------------------------------------------------
	
	/**
	 * Opens the Resource Monitor for a specified device, app may be passed on if called from project item
	 */
	public void openResourceMonitor(String identifier) {
		System.out.println("\nOpening HP resource monitor");
		
		if ( currentItem.isDevice() ) {
			// getDevice() will decide on which device to focus on by identifier
			Device deviceToUse = getDevice(identifier);
			
			if (deviceToUse == null) {
				// TODO no device found warning message
			} else {
				// add task
				taskManager.addTask( new Task(Task.RESOURCE_MONITOR, null, deviceToUse) );
			}
		} else {
			// for a project focus: also pass on the project, along with default device
			taskManager.addTask( new Task(Task.RESOURCE_MONITOR, currentItem, currentDevice) );
		}
	}
	
	// JSLint window methods --------------------------------------------
	
	/**
	 * Method always returns a functional, non-null reference.
	 * In case no functional instance is available it will initiate one and pass it back.
	 */
	public LintFrame getJSLintWindow() {
		
		if (jslintWindow != null) {
			return jslintWindow;
		} else {
			// init
			jslintWindow = new LintFrame(this);
			// return a working instance
			return jslintWindow;
		}
	}
	
	public void disposeJSLintWindow() {
		jslintWindow = null;
	}
	
	// Other methods --------------------------------------------
	
	/**
	 * Providing access to classes from other packages (as a proxy)
	 * to protected BottomBar in protected DevtoolFrame.
	 */
	public void setActivityIndicator(int numberOfTasks) {
		devwindow.bottomBar.setActivityIndicator( numberOfTasks );
	}
	
	/**
	 * @return String with information about this program.
	 */
	public String toString() {
		return "This program assists a webOS developer by giving one-click support for common operations.";
	}
	
	/**
	 *
	 */
	public void windowMinimize() {
		//System.out.println("Minimizing window");
		devwindow.windowMinimize();
	}
	
	/**
	 *
	 */
	public void windowZoom() {
		//System.out.println("Zooming window");
		devwindow.windowZoom();
	}
	
	/**
	 * Show a manual
	 */
	public void windowShowHelp() {
		//System.out.println("Showing manual");
		
		// using a new Thread
		new Thread (new Runnable () {
			public void run () {
			  new DevtoolHelpWindow();
			}
	    }).start ();
	}
	
	/**
	 * The 'about' dialog.
	 */ 
	public void windowShowAbout() {
	    // Note that a new thread is created here to run the dialogue.
	    // That way control returns at once to the caller, while the user
	    // interacts with the dialogue. This ok since its just a read-only
	    // information box.
	    /*
	    new Thread (new Runnable () {
			public void run () {
			  JOptionPane.showMessageDialog (null,
							 toString (),
							 "Devtool information",
							 JOptionPane.INFORMATION_MESSAGE);
			}
	    }).start ();
	    */
	}

	boolean toggleLogging() {
		loggingEnabled = !loggingEnabled;
		this.setLogState(loggingEnabled);
		return loggingEnabled;
	}

	/*
	 * @param inState Enables or disables the use of the log pane in the main window.
	 * If enabled, the System.out (STDOUT) will be redirected to the pane.
	 * Disable to get STDOUT to the terminal
	 */
	void setLogState(boolean inState) {
		loggingEnabled = inState;
		this.devwindow.setLogState(inState);
	}

	void clearLogContent() {
		this.devwindow.clearLogContent();
	}

	/**
	 * Refreshes both device list, as well as each project
	 */
	void refreshAll() {
		this.deviceRefreshList();

		for (int i = 0; i < projects.size(); i++) {
			((Project) projects.elementAt(i)).update();
		}
	}
	
	/**
	 * Shutdown and exit JVM
	 */
	protected void shutdown() {
		
		// set flag to true for any agents to check
	  	exitStatus = true;
  	
	  	// run it a separate thread to allow the program and GUI
	  	// to remain functionable
	    new Thread (new Runnable () {
			public void run () {
				// while there are still agents left do not quit
				// this allows agents to migrate
				int naps = 50; // unless a limit of short naps has been reached
				  	
				do {
					// sleep for some time
					try {
					    Thread.currentThread().sleep(200); // 0,2 second
					}
					catch (java.lang.InterruptedException ie) {}
					
					// decrement counter
					naps--;
					
				} while ( taskManager.hasTasks() && naps > 0);
				
				// End it all
				setLogState(false);
				System.exit (0);	// Exit the JVM 
			}
	    }).start ();
	}
	
	/**
	 * Init main method
	 */
	public static void main(String[] args) {
		
		// set system properties
		String lcOSName = System.getProperty("os.name").toLowerCase();
		boolean IS_MAC = lcOSName.startsWith("mac os x");
		if (IS_MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "webOS Devtool");
		}
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// init
		Devtool webosdevtool = new Devtool();
	}
}