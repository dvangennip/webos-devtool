package webosdevtool.process;

import webosdevtool.DevSourceItem;
import webosdevtool.Device;

/**
 * Task object can be regarded as an order sheet for the TaskHandler.
 * It signals which process should be performed on which project and device.
 */
public class Task {
	
	// Static variables
	
	public static final int PROJECT_REVEAL = 1;
	public static final int PROJECT_JSLINT = 2;
	public static final int PROJECT_PACKAGE = 3;
	public static final int PROJECT_INSTALL = 4;
	public static final int PROJECT_LAUNCH = 5;
	public static final int PROJECT_CLOSE = 7;
	public static final int PROJECT_UNINSTALL = 8;
	public static final int PROJECT_RUN = 9;
	public static final int PROJECT_NEW = 11;
	public static final int PROJECT_NEW_SCENE = 12;
	public static final int PROJECT_OPEN_LOGGER = 13;
	public static final int PROJECT_DEPLOY = 14;
	
	public static final int RESOURCE_MONITOR = 20;
	public static final int OPEN_WEBBROWSER = 21;
	public static final int OPEN_PROJECT_IN_BROWSER = 22;
	
	public static final int DEVICE_START = 31;
	public static final int DEVICE_REVEAL = 32;
	public static final int DEVICE_DISCONNECT = 33;
	public static final int DEVICE_ENABLE_HOST_MODE = 34;
	public static final int DEVICE_SCAN = 35;
	public static final int DEVICE_LIST_APPS = 36;
	public static final int DEVICE_LOG_LEVEL = 37;
	
	// Instance variables
	
	private boolean active;
	private boolean completed;
	private boolean successful;
	private String report;
	private int taskType;
	private DevSourceItem devSourceItem;
	private Device destination;
	private String[] arguments;
	
	// Constructors
	
	/**
	 * Overloaded constructor, has the ability to hold arguments in a String array.
	 * @param myTaskType Indicates which kind of task should be performed. Use one of the <code>static final int</code> variables.
	 * @param itemReference Pass a reference to a project or <code>null</code> if no project is involved.
 	 * @param destinationReference Pass a reference to a device or <code>null</code> if no device is involved.
 	 * @param args String array of additional arguments which some tasks may require (refer to <code>TaskHandler</code> API).
	 */
	public Task (int myTaskType, DevSourceItem itemReference, Device destinationReference, String[] args) {
		
		// init variables
		active = false;
		completed = false;
		successful = false;
		report = null;
		taskType = myTaskType;
		devSourceItem = itemReference;
		destination = destinationReference;
		arguments = args;
	}
	
	/**
	 * Default constuctor, misses the optional arguments parameter.
	 */
	public Task (int myTaskType, DevSourceItem itemReference, Device destinationReference) {
		this(myTaskType, itemReference, destinationReference, null);
	}
	
	// Methods
	
	/**
	 * @return A human readable name for the task
	 */
	public String getName() {
		
		String name = null;
		
		switch( taskType ) {
	   		case PROJECT_REVEAL: 			name = "Reveal Project in Finder"; break;
			case PROJECT_JSLINT: 			name = "Analyse Project with JSLint"; break;
			case PROJECT_NEW:	 			name = "Create New Project"; break;
			case PROJECT_NEW_SCENE:	 		name = "Add New Scene"; break;
			case PROJECT_DEPLOY: 			name = "Deploy Project"; break;
			case PROJECT_PACKAGE: 			name = "Package Project"; break;
			case PROJECT_INSTALL: 			name = "Install Project"; break;
			case PROJECT_LAUNCH: 			name = "Launch Project"; break;
			case PROJECT_CLOSE: 			name = "Close Project"; break;
			case PROJECT_UNINSTALL: 		name = "Uninstall Project"; break;
			case PROJECT_RUN: 				name = "Run Project"; break;
			case PROJECT_OPEN_LOGGER: 		name = "Open Palm-Log"; break;
			case RESOURCE_MONITOR: 			name = "Open Resource Monitor"; break;
			case DEVICE_SCAN:				name = "Scan For Devices"; break;
			case DEVICE_REVEAL:				name = "Reveal Device in Finder"; break;
			case DEVICE_LIST_APPS:			name = "List Apps for Device"; break;
			case DEVICE_START:			 	name = "Start Device"; break;
			case DEVICE_ENABLE_HOST_MODE:	name = "Enable Host Mode"; break;
			case DEVICE_LOG_LEVEL:			name = "Set Device Log Level"; break;
	   		default: 						name = "Unknown Task"; break;
	   	}
		
		return name;
	}
	
	/**
	 * @return True if active
	 */
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean state) {
		active = state;
	}
	
	/**
	 * @return False if not completed
	 */
	public boolean isCompleted() {
		return completed;
	}
	
	public void setCompleted(boolean state) {
		completed = state;
	}
	
	/**
	 * @return False if not completed
	 */
	public boolean isSuccessful() {
		return successful;
	}
	
	public void setSuccessful(boolean state) {
		successful = state;
	}
	
	/**
	 * @return True if a report is available, false if otherwise.
	 */
	public boolean hasReport() {
		if (report != null) {
			return true;
		}
		// else
		return false;
	}
	
	/**
	 * @return A process report in String form. Only added if not completed successfully.
	 */
	public String getReport() {
		return report;
	}
	
	/**
	 * @param report A process report with useful information on why a task did not complete successfully.
	 */
	public void setReport(String report) {
		this.report = report;
	}
	
	/**
	 * @return Returns the task type (one of the listed <code>static final</code> types)
	 */
	public int getTaskType() {
		return taskType;
	}
	
	/**
	 * @return Returns the destination device this task aims at
	 */
	public Device getDestinationDevice() {
		return destination;
	}
	
	/**
	 * @return Reference to source data
	 */
	public DevSourceItem getDevSourceItem() {
		return devSourceItem;
	}
	
	/**
	 * @return Number of arguments available, 0 if none are available.
	 */
	public int hasArguments() {
		if (arguments != null) {
			return arguments.length;
		} else {
			return 0;
		}
	}
	
	/**
	 * Getter method for Task arguments.
	 * @return String array of optional arguments.
	 */
	public String[] getArguments() {
		return arguments;
	}
}