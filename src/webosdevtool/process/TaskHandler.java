package webosdevtool.process;

import webosdevtool.Device;
import webosdevtool.InstalledApp;
import webosdevtool.Project;
import webosdevtool.logger.LogFrame;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import devdaily.SystemCommandExecutor;

/**
 * TaskHandler.class<br />
 * Processes Tasks that were added via its TaskManager. When done this handler,
 * which runs in a separate thread, goes to sleep. It is interrupted by its manager when a
 * new Task is added.
 */
public class TaskHandler extends Thread {
	
	// Variables
	
	private Task currentTask;
	private TaskManager taskManager;
	private FileOperator fileOperator;
	
	// Constructor
	
	/**
	 * Default constructor
	 *
	 * @param myTaskManager Reference to its TaskManager
	 * @param myFileOperator Reference to a FileOperator
	 */
	public TaskHandler (TaskManager myTaskManager, FileOperator myFileOperator) {
		
		// assign references
		this.taskManager = myTaskManager;
		this.fileOperator = myFileOperator;
	}
	
	// Run method
	
	/**
	 * Run method tries to sleep. When interrupted by its TaskManager it
	 * will check whether any tasks are available for processing.
	 * If so, it will process the task.
	 */
	public void run() {
		// forever
		while (true) {
			
			// try to sleep
			synchronized (this) {
				try {
				    this.wait();
			    }
			    catch (java.lang.InterruptedException iex) {
			    	System.out.println("Handler: Interrupted while trying to wait");
			    }
			    catch (java.lang.IllegalMonitorStateException ims) {
			    	System.out.println("Handler: Illegal Monitor State Exception while trying to wait");
			    }
			}
		    
		    // when interrupted check whether tasks are available
		    while ( taskManager.hasTasks() ) {
				
				// get a task
				currentTask = taskManager.getNextTask();
				
				// This block handles the setting of active and completed status of a task
				// based on the results from the particular process() method.
				
				// check if not null
				// null may be returned if no suitable tasks where available
				if ( currentTask != null ) {
					
					// begin
					currentTask.setActive(true);
					
					// use return value to determine succes
			    	boolean success = false; // assume it goes wrong
			    	
			    	try {
				    	// call actual processing and fetch success
				    	switch( currentTask.getTaskType() ) {
				    		case Task.PROJECT_REVEAL: 			success = processProjectReveal(); break;
							case Task.PROJECT_JSLINT: 			success = processProjectJSLint(); break;
							case Task.PROJECT_NEW:	 			success = processProjectNew(); break;
							case Task.PROJECT_NEW_SCENE:	 	success = processProjectNewScene(); break;
							case Task.PROJECT_DEPLOY: 			success = processProjectDeploy(); break;
							case Task.PROJECT_PACKAGE: 			success = processProjectPackage(); break;
							case Task.PROJECT_INSTALL: 			success = processProjectInstall(); break;
							case Task.PROJECT_LAUNCH: 			success = processProjectLaunch(false); break;
							case Task.PROJECT_CLOSE: 			success = processProjectLaunch(true); break;
							case Task.PROJECT_UNINSTALL: 		success = processProjectUninstall(); break;
							case Task.PROJECT_RUN: 				success = processProjectRun(); break;
							case Task.PROJECT_OPEN_LOGGER: 		success = processProjectOpenLogger(); break;
							case Task.OPEN_PROJECT_IN_BROWSER:	success = processProjectOpenInBrowser(); break;
							case Task.RESOURCE_MONITOR: 		success = processOpenResourceMonitor(); break;
							case Task.OPEN_WEBBROWSER: 			success = processOpenWebkitBrowser(); break;
							case Task.DEVICE_SCAN:				success = processDeviceRefreshList(); break;
							case Task.DEVICE_REVEAL:			success = processDummy(); break;
							case Task.DEVICE_LIST_APPS:			success = processDeviceGetApplications(); break;
							case Task.DEVICE_START:			 	success = processDeviceStart(); break;
							case Task.DEVICE_ENABLE_HOST_MODE:	success = processEmulatorEnableHostMode(); break;
				    		default: 							success = true; break;
				    	}
			    	} catch (Exception e) {
			    		System.out.println("FAILED " + currentTask.getName() + ": " + e.toString() );
			    		e.printStackTrace();
			    	}
			    	
			    	// mark if successful
					// is false by default
					if (success) {
						currentTask.setSuccessful(true);
					}
					
					// check if a report is available, if so handle it
					if ( currentTask.hasReport() ) {
						// generate feedback to user
						TaskReportDialog.show( currentTask );
					}
					
					// finish
					currentTask.setActive(false);
					currentTask.setCompleted(true);
			    	
			    	// when processing is done ask if task can be removed
			    	taskManager.removeTask(currentTask);
			    	
				} // end of processing task
				
				// reset variable
				currentTask = null;
				
		    } // end of tasks loop
		} // end of forever loop
	} // end of run()
	
	// Helper methods --------------------------------------------------------------------------
	
	/**
	 * Method executes a List of commands and returns the results after processing finishes. This is a blocking method.
	 * @param commands List of <code>String</code> elements that together form one command-line instruction.
	 * @return Object array with three items: 1. process exitValue (Integer value: 0 means succesful completion), 2. Process output (String value), 3. Process error output (String value).
	 */
	private Object[] execute(List<String> commands) {
	
		// init return Object[]
		Object[] results = new Object[3];
		results[0] = new Integer(2); // 0 means no error, 1 palm default error
		
		// execute the command
	    SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
	    try {
	    	// actual processing, exitvalue is saved
	    	results[0] = new Integer( commandExecutor.executeCommand() );
	    	
	    	// get the stdout and stderr from the command that was run
		    StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
		    StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
		    
		    // save into Object
		    results[1] = new String(stdout);
			results[2] = new String(stderr);
		    
		    // print the stdout and stderr
		    System.out.println("The numeric result of the command was: " + results[0]);
		    System.out.println("STDOUT:");
		    System.out.println(stdout);
		    System.out.println("STDERR:");
		    System.out.println(stderr);
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
		
		return results;
	}
	
	/**
	 * Method converts a version number <code>String</code> (e.g. 2.1.0) to an <code>int</code> value.
	 * Dots are removed and the resulting number treats this removal intelligently. However,
	 * digits behind the second dot are truncated. Some examples:<br />
	 * 0.5.6 -> 56<br />
	 * 1.4.5 -> 145<br />
	 * 2.1.0.519 -> 210<br />
	 * 10.3.435 -> 1034<br />
	 * An integer version number is easier for comparison or checking whether a version can accept certain actions.
	 *
	 * @param versionstring Version number as <code>String</code>, including dots.
	 * @return Version as <code>int</code> value, thus without dots, et cetera.
	 */
	public static int convertVersionToInt(String versionstring) {
		int version = Integer.parseInt( versionstring.replaceAll("(\\d+)\\.{1}(\\d{1})\\.{1}(\\d{1})\\d*", "$1$2$3") );
		
		return version;
	}
	
	/**
	 * Method uses list of currently available devices and checks
	 * whether indicated Device is available.
	 * Its result can be useful in checking assumptions for a task involving a device.
	 * @param d Device instance.
	 * @return Boolean value which returns true if device is available.
	 */
	private boolean isDeviceAvailable(Device d) {
		
		// emulator is offline, counts as not available
		if ( d.getID().equals("0") ) {
			return false;
		}
		
		// continue with available list
		String[] ids = taskManager.devtool.getAvailableDeviceIDs();
		// loop over devices to find id
		for (int i = 0; i < ids.length; i++) {
			
			if ( ids[i].equals( d.getID() ) ) {
				return true;
			}
		}
		
		// else
		return false;	
	}

	// Project methods -----------------------------------------------------
	
	/**
	 * Actual processing of a task.
	 * This is a dummy process which serves as an example or placeholder.
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processDummy() {
		
		// do processing
		System.out.println("--- Processing Dummy task");
		try {
		    Thread.currentThread().sleep(3000); // fake activity
	    }
	    catch (java.lang.InterruptedException iex) {
	    	System.out.println("Process Dummy interrupted");
	    }
	    // generate information output
	    System.out.println("--- Type: " + currentTask.getTaskType() );
	    try{
		    if (currentTask.getDevSourceItem() != null) {
		    	System.out.println("--- Source name: " + currentTask.getDevSourceItem().getName() );
		    }
		    if (currentTask.getDestinationDevice() != null) {
		    	System.out.println("--- Device: " + currentTask.getDestinationDevice().getName() + " @ " + currentTask.getDestinationDevice().getLocation() );
		    }
	    }
	    catch (java.lang.NullPointerException npe) {
	    	System.out.println("--- NOTE: some data may be missing here due to NullPointerException(s).");
	    }
	    
	    // add a dummy report
	    //currentTask.setReport( new String("This is a generic message about the\npossible issues with a task.") );
		
		// return successfully
		return true;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectReveal() {
		
		// returns true if opening a file or directory was successful
		return fileOperator.openFile( currentTask.getDevSourceItem().getLocation() );
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectJSLint() {
				
		// cast the project item
		Project p = (Project) currentTask.getDevSourceItem();
		// update item
		p.update();
		// open jslint window and handle it there
		taskManager.devtool.getJSLintWindow().setProject(p);
		
		return true;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectNew() {
		
		// init project folder
		String projectFolder = currentTask.getArguments()[0] + "/" + currentTask.getArguments()[1];
		// remove any spaces
		projectFolder = projectFolder.replaceAll("\\s+","");
		System.out.println("Creating new project in: "+projectFolder);
		
		// make directories, only continue if successful
		boolean dirCreationSuccess = false;
		// <projectname without spaces>
		if ( fileOperator.createDirectory(projectFolder) ) {
			//		\-- bin
			if ( fileOperator.createDirectory(projectFolder + "/bin") ) {
				//	\-- app_src
				if ( fileOperator.createDirectory(projectFolder + "/app_src") ) {
					//
					dirCreationSuccess = true;
				}
			}
		}
		
		// if someting went wrong exit with error message
		if (!dirCreationSuccess) {
			currentTask.setReport("Creating a new project failed. The necessary directories could not be created, perhaps because of access or privilege issues?");
			return false;
		}
		// else all still going smooth
		else {
			// palm-generate [options] <destination path>
			// New project with name Stock Ticker: palm-generate -p "title=Stock Ticker" ~/projects/Ticker 
			
			// build the system command we want to run
		    List<String> commands = new ArrayList<String>();
		    commands.add("/bin/bash");
		    commands.add("/opt/PalmSDK/Current/bin/palm-generate"); // palm-generate
		    commands.add("-p"); // parameter flag
		    commands.add("\"{title:\'"+currentTask.getArguments()[1]+"\'}\""); // title as JSON
		    commands.add(projectFolder + "/app_src"); // <destination path>
		
		    // execute the command
		    Object[] obj = execute(commands);
			int result = ((Integer) obj[0]); // cast as Integer
					
			// return false if result code is not 0
			if (result >= 1) {
				// generate feedback
				String errorMessage = ((String) obj[2]);
				currentTask.setReport(errorMessage);
				
				return false;
			}
			
			// when successfull continue
			
			// callback to open project
			taskManager.devtool.projectOpen(projectFolder);
			// finally we are done
			return true;
		}
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectNewScene() {
		
		// Generate new scene: palm-generate -t new_scene -p "name=First" ~/projects/Ticker
		
		// build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add("/opt/PalmSDK/Current/bin/palm-generate"); // palm-generate
	    commands.add("-t"); // template flag
   	    commands.add("new_scene"); // <template new scene>
	    commands.add("-p"); // parameter flag
	    commands.add("\"{name:\'"+currentTask.getArguments()[0]+"\'}\""); // scene name as JSON
	    commands.add( currentTask.getDevSourceItem().getLocation() + "/app_src"); // <destination path>
	
	    // execute the command
	    Object[] obj = execute(commands);
		int result = ((Integer) obj[0]); // cast as Integer
		
		// return false if result code is not 0
		if (result >= 1) {
			// generate feedback
			String errorMessage = ((String) obj[2]);
			currentTask.setReport(errorMessage);
			
			return false;
		}
		
		// update item - sources.json should be changed by now
		currentTask.getDevSourceItem().update();
		// finally return
		return true;
	}

	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectDeploy() {
		
		// update item - make sure the info is in sync with real source
		currentTask.getDevSourceItem().update();

		// non-enyo2 does not need deployment
		// TODO include a proper Enyo version check instead of this file-based assumption
		if ( !FileOperator.checkFileValidity(currentTask.getDevSourceItem().getLocation() + "/app_src/tools/deploy.sh") ) {
			System.out.println("No tools/deploy.sh found (not required for non-enyo2 projects).");
			return true;
		}
		
		// <source path> ./tools/deploy.sh
		
		// build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add( currentTask.getDevSourceItem().getLocation() + "/app_src/tools/deploy.sh"); // <source path>
	    
	    // execute the command
	    Object[] obj = execute(commands);
		int result = ((Integer) obj[0]); // cast as Integer
		System.out.println(result);

		// return false if result code is not 0
		if (result >= 1) {
			// generate feedback only for non-1 errors
			// because usually it means those are regular compiling troubles
			// which ought to go to a proper (scrollable) log window, not a small dialog
			if (result >= 2) {
				String errorMessage = ((String) obj[2]);
				currentTask.setReport(errorMessage);
			}
			
			return false;
		}
		
		// nothing went wrong if we got this far
		return true;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectPackage() {
		
		// update item - make sure the info is in sync with real source
		currentTask.getDevSourceItem().update();

		// palm-package -o <destination path> <source path>
		
		// build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add("/opt/PalmSDK/Current/bin/palm-package"); // palm-package
	    commands.add("--outdir=" + currentTask.getDevSourceItem().getLocation() + "/bin"); // -o <output location>
	    commands.add( currentTask.getDevSourceItem().getLocation() + "/app_src"); // <source path>
	    // include extra stuff if required
	    if ( currentTask.getDevSourceItem().hasService() ) {
	    	    commands.add( currentTask.getDevSourceItem().getLocation() + "/app_service"); // <service path>
	    }
	    if ( currentTask.getDevSourceItem().hasPackage() ) {
	    	    commands.add( currentTask.getDevSourceItem().getLocation() + "/app_package"); // <package path>
	    }
	
	    // execute the command
	    Object[] obj = execute(commands);
		int result = ((Integer) obj[0]); // cast as Integer
		
		// return false if result code is not 0
		if (result >= 1) {
			// generate feedback
			String errorMessage = ((String) obj[2]);
			currentTask.setReport(errorMessage);
			
			return false;
		}
		
		// nothing went wrong if we got this far
		return true;
	}
	
	/**
	 * Method will install a project package file onto the Task's destination device.
	 * If the package is not available based on current project data (version, etc.)
	 * it will ask to package and try again once more.
	 *
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectInstall() {
		
		// update item - make sure the info is in sync with real source
		currentTask.getDevSourceItem().update();
		
		// package location
		String packagePath = currentTask.getDevSourceItem().getLocation() + "/bin/" + currentTask.getDevSourceItem().getID() + "_" + currentTask.getDevSourceItem().getVersion() + "_all.ipk";
		
		// CHECK ASSUMPTIONS
		
		// package is available
		if ( !FileOperator.fileExists( packagePath ) ) {
			// not available, so get it packaged first
			processProjectPackage();
			
			// check again
			if ( !FileOperator.fileExists( packagePath ) ) {
				// this time exit
				currentTask.setReport("Installing failed, getting the project package failed.");
				return false;
			}
		}
		// device is available
		if ( !this.isDeviceAvailable( currentTask.getDestinationDevice() ) ) {
			currentTask.setReport("Installing "+currentTask.getDevSourceItem().getName()+" failed, because the destination device is not available.");
			return false;
		}
		// device supports the application (e.g. Enyo is webOS v3.0+)
		if ( currentTask.getDevSourceItem().isEnyoEnabled() ) {
			// app signals it requires Enyo
			// if the device does not support it, we're in trouble, so abort
			if ( !currentTask.getDestinationDevice().isEnyoEnabled() ) {
				currentTask.setReport("Installing "+currentTask.getDevSourceItem().getName()+" failed, because the destination device does not support the required Enyo framework.");
				return false;
			}
		}
		
		// ACTUAL PROCESS
		
		// palm-install -d (tcp | usb) <package path>
		
		// build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add("/opt/PalmSDK/Current/bin/palm-install"); // palm-install
	    commands.add("--device=" + currentTask.getDestinationDevice().getID() ); // specify device
	    commands.add( packagePath ); // <package path>
	
	    // execute the command
	    Object[] obj = execute(commands);
		int result = ((Integer) obj[0]); // cast as Integer
		
		// return false if result code is not 0
		if (result >= 1) {
			// generate feedback
			String errorMessage = ((String) obj[2]);
			currentTask.setReport(errorMessage);
			
			return false;
		}
		
		// finally
		return true;
	}
	
	/**
	 * @param close True if the project application should be closed on the device instead of opened.
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectLaunch(boolean close) {
		
		// CHECK ASSUMPTIONS
		
		// device is available
		if ( !this.isDeviceAvailable( currentTask.getDestinationDevice() ) ) {
			currentTask.setReport("Launching "+currentTask.getDevSourceItem().getName()+" failed, because the destination device is not available.");
			return false;
		}
		// app is available on device? no, just get the process error message and show that
		
		// ACTUAL PROCESS
		
		// palm-launch [options] <appid>
		// -i : inspectable
		// -c : close app
		// -f : relaunch, close and launch again
		// -p : include parameters. Example: -p "{mojoConfig: {debuggingEnabled:true}}" com.example.app
		// -d : device (tcp | usb)
		// -l : list installed applications on device
		
		// build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add("/opt/PalmSDK/Current/bin/palm-launch"); // palm-launch
	    commands.add("--device=" + currentTask.getDestinationDevice().getID() ); // specify device
	    if (close) {
	    	commands.add("-c"); // close app
	    }
	    commands.add( currentTask.getDevSourceItem().getID() ); // <app id>
	
	    // execute the command
	    Object[] obj = execute(commands);
		int result = ((Integer) obj[0]); // cast as Integer
		
		// return false if result code is not 0
		if (result >= 1) {
			// generate feedback
			String errorMessage = ((String) obj[2]);
			// if error is related to project not being installed, show a more readable version
			if ( errorMessage.contains("no matches for") ) {
				currentTask.setReport("The application "+currentTask.getDevSourceItem().getName()+" could not be launched as it is not yet installed.");
			} else {
				currentTask.setReport(errorMessage);
			}
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectUninstall() {
		
		// CHECK ASSUMPTIONS
		
		// device is available
		if ( !this.isDeviceAvailable( currentTask.getDestinationDevice() ) ) {
			currentTask.setReport("Uninstalling "+currentTask.getDevSourceItem().getName()+" failed, because the destination device is not available.");
			return false;
		}
		// app available on device: not relevant, if not available it is good :)
		
		// ACTUAL PROCESS
		
		// palm-install -d <device> -r <package id>
		
		//Device d = getDestinationDevice();
				
		// build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add("/opt/PalmSDK/Current/bin/palm-install"); // palm-install
	    commands.add("--device=" + currentTask.getDestinationDevice().getID() ); // specify device
	    commands.add("-r"); // remove
	    commands.add( currentTask.getDevSourceItem().getID() ); // <package id>
	
	    // execute the command
	    Object[] obj = execute(commands);
		int result = ((Integer) obj[0]); // cast as Integer
		
		// return false if result code is not 0
		if (result >= 1) {
			// generate feedback
			String errorMessage = ((String) obj[2]);
			// if the error message just tells that the app was not available anyway ignore it
			// that is not an error because the goal is reached
			if ( !errorMessage.contains("command failed with returnValue: false") ) {
				currentTask.setReport(errorMessage);
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectRun() {
		
		// deploy
		if ( processProjectDeploy() ) {
			// package
			if ( processProjectPackage() ) {
				// install
				if ( processProjectInstall() ) {
					// launch
					return processProjectLaunch(false);
				}
			}
		}
		
		// return without success if we end up here
		return false;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectOpenInBrowser() {
		
		// create URL to open
		Project prj = (Project) currentTask.getDevSourceItem();
		String projectURL = "file://" + prj.getLocation() + "/app_src/" + prj.getAppInfo().main;
		
		// try to open it
		boolean success = FileOperator.openWebpageInBrowser( projectURL );
		
		// when successful exit immediately
		if (success) {
			return true;
		}
		// else generate error message
		currentTask.setReport("Opening "+prj.getName()+" in default browser failed, because something went wrong.");
		return false;
	}
	
	// Logger methods -----------------------------------------------------
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processProjectOpenLogger() {
		// let everything be handled by a LogFrame instance
		new LogFrame( currentTask.getDevSourceItem().getID(), currentTask.getDestinationDevice().getID() );
		return true;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processOpenResourceMonitor() {
		
		// CHECK ASSUMPTIONS
		
		// device is available
		// (app is available on device)
		
		// ACTUAL PROCESS
		
		// palm-worm -d <device> <appid>
						
		// build the system command we want to run
	    final List<String> commands = new ArrayList<String>();
	    
	    boolean monitorAppExists = FileOperator.fileExists("/Applications/Palm Monitor.app");
	    
	    if (monitorAppExists) {
	    	// use Resource Monitor app
	    	commands.add("open"); // mac open command
	    	commands.add("/Applications/Palm Monitor.app"); // the monitor app
	    	commands.add("--args"); // indicates arguments will follow
	    	//commands.add("-gui"); // indicates to return to this apps's gui upon closing
	    } else {
	    	// use regular palm-worm command
		    commands.add("/bin/bash");
		    commands.add("/opt/PalmSDK/Current/bin/palm-worm"); // palm-worm
	    }
	    // rest is not specific
	    commands.add("-d"); // device
	    commands.add( currentTask.getDestinationDevice().getID() ); // specify device
	    // add only appID if available
	    if (currentTask.getDevSourceItem() != null) {
	    	commands.add( currentTask.getDevSourceItem().getID() ); // <package id>
	    }
	    
	    // execute the command
	    // done in separate thread to avoid execute not returning...
	    new Thread (new Runnable () {
			public void run () {
				execute(commands);
			}
	    }).start();
		
		// return positively
		// NOTE: result cannot be checked, as it will only return upon closing the Resource Logger
		return true;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processOpenWebkitBrowser() {
	    
		// figure out which browser to start
		String browserChoice = currentTask.getArguments()[0];
		// make sure it is defined
		if (browserChoice == null) {
			browserChoice = "safari";
		}
	    
	    // build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("open"); // mac open command
	    // pick right browser
	    if ( browserChoice.equals("chrome") ) {
		    commands.add("/Applications/Google Chrome.app"); // Chrome
	    } else {
	    	commands.add("/Applications/Safari.app"); // Safari - default
	    }
	    commands.add("--args"); // indicates arguments will follow
	    commands.add("--allow-file-access-from-files"); // arg (required for Enyo to work in browser)
	    commands.add("--enable-file-cookies"); // arg (required for Enyo to work in browser)
	    commands.add("--disable-web-security"); // arg (required for Enyo to work in browser)
	    
	    // execute the command
	    Object[] result = execute(commands);
		
		// return false if result code is not 0
		if ( ((Integer) result[0]) > 0) {
			// generate feedback
			String errorMessage = ((String) result[2]);
			currentTask.setReport(errorMessage);
			return false;
		}
		// else return positively
		return true;
	}
	
	// Device methods -----------------------------------------------------
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	protected boolean processDeviceRefreshList() {
		
		// palm-launch --device-list
		// TYPICAL RESPONSE:
		//"emulator" {c69ddacef0160fada37edd4c89a26412420db120} tcp 52199
		//"<device name>" {<nduid>} <connection type> <port>

		// build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add("/opt/PalmSDK/Current/bin/palm-launch"); // palm-launch
	    commands.add("--device-list"); // generate list
		
		// get result
		Object[] result = execute(commands);
		
		// only parse result if successful
		if ( ((Integer) result[0]) > 0) {
			return false;
		} else {
			// parse command output
			String deviceString = ((String) result[1]);
			List<String> foundIDs = new ArrayList<String>();
			
			// per item get info				
			// create regex pattern and matcher
			// "$1" {$2} $3
			Pattern scriptRegex = Pattern.compile("\"{1}(.+)\"{1}\\s{1}\\{{1}(\\w+)\\}{1}\\s{1}(.+)\\n");
			Matcher myMatcher = scriptRegex.matcher( deviceString ); // input data
			
			// find matches (true if so, false if no match found anymore)
			while ( myMatcher.find() ) {
				
				// get result
				String name = myMatcher.group(1);
				String identifier = myMatcher.group(2);
				String location = myMatcher.group(3);
				
				// save into device list
				// add method already checks for duplicates
				taskManager.devtool.deviceAdd(name, identifier, location);
				
				// add ID to foundID list for later matching (see below)
				foundIDs.add( identifier );
			}
			
			// ---- END OF FINDING / ADDING NEW DEVICES
			
			// check for devices that are available in the devices Vector of devtool
			// but which are no longer found by the parser
			// done by looping over available device ID's (devices Vector) and matching with found ID's
			String[] deviceIDs = taskManager.devtool.getAvailableDeviceIDs();
			for (int k = 0; k < deviceIDs.length; k++) {
				
				// assume it will not be found
				boolean foundInList = false;
				
				// go over found list, compare ID's
				for (int j = 0; j < foundIDs.size(); j++) {
					String aFoundID = ((String) foundIDs.get(j) );
					if ( deviceIDs[k].equals( aFoundID ) ) {
						foundInList = true;
						break;
					}
				}
				
				// if not found, remove device
				if (!foundInList) {
					taskManager.devtool.deviceRemove( deviceIDs[k] );
				}
			}
		} // end of if command successful

		// finally
		return true;
	}
		
	/**
	 * Method sets a list of installed applications and their versions for a specified device.
	 * It adds the found devices directly to the Device instance's related variable.<br />
	 *
	 * Method also retrieves the webOS system version of a specified device.
	 * Because doing so directly is difficult it does not query the OS itself
	 * but rather it takes the version of a listed application (using <code>getDeviceInstalledApps()</code>).
	 * that is equal to the webOS system version. For now Calendar (<code>com.palm.app.calendar</code>)
	 * is the app of choice.
	 *
	 * @see webosdevtool.Device
	 * @see webosdevtool.InstalledApp
	 */
	private boolean processDeviceGetApplications() {
		
		// get app list
		InstalledApp[] apps = this.getDeviceInstalledApps( currentTask.getDestinationDevice().getID() );
		
		// try to find the device webOS version from an app
		if (apps != null) {
			
			// save to device
			currentTask.getDestinationDevice().setInstalledApps( apps );
			
			// loop over to find one app
			for (int i = 0; i < apps.length; i++) {
				
				if ( apps[i].getID().equals("com.palm.app.calendar") ) {
					// get its version and set the device version to the same
					currentTask.getDestinationDevice().setVersion( apps[i].getVersion() );
					
					// make sure the UI reflects the changes
					taskManager.devtool.updateItem( currentTask.getDestinationDevice() );
					
					break; // we're done
				}
			}
		} else {
			return false;
		}
		
		// finally
		return true;
	}
	
	private InstalledApp[] getDeviceInstalledApps(String deviceID) {
		
		//palm-launch -d <device> -l
		// <device> can be usb / tcp / port number / "name"
		
		// typical response
		/*
			com.palm.app.youtube 2.1.0 SDK "YouTube"
			* com.palm.app.messaging 2.1.0 SDK "Messaging"
			com.quickoffice.webos 1.0.696 "Quickoffice"
			com.palm.app.findapps 2.0.20900 "App Catalog"
			nl.sinds1984.pong 0.5.0 "Pong"
			nl.sinds1984.meteoinfo 0.2.0 "Meteo Info"
			com.palm.app.backup 3.0.0 SDK preview "Backup"
			* com.palm.app.calendar 3.0.0 SDK preview "Calendar"
			
			--- * means the application is currently running
		*/
		
		// make sure we don't fetch data for a non-active device
		if (deviceID.equals("0")) {
			return null;
		}
		
		// build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add("/opt/PalmSDK/Current/bin/palm-launch"); // palm-launch
	    commands.add("--device=" + deviceID ); // specify device
	    commands.add("-l"); // list apps
		
		// get result
		Object[] result = execute(commands);
		
		// only parse result if successful
		if ( ((Integer) result[0]) > 0) {
			return null;
		} else {
			// parse command output
			String appsString = ((String) result[1]);
			appsString = appsString.replaceFirst("^.+?\\n{1}", "");
			List<InstalledApp> foundApps = new ArrayList<InstalledApp>();
			
			// per item get info				
			// create regex pattern and matcher
			// <* >$2 $3 <SDK ><preview >"$5"
			Pattern scriptRegex = Pattern.compile("(\\*\\s)?(.+?)\\s{1}(.+?)\\s{1}(SDK\\s)?(preview\\s)?\"{1}(.+?)\"{1}\\n{1}");
			Matcher myMatcher = scriptRegex.matcher( appsString ); // input data
			
			// find matches (true if so, false if no match found anymore)
			while ( myMatcher.find() ) {
				
				// get result
				String name = myMatcher.group(6);
				String appID = myMatcher.group(2);
				String version = myMatcher.group(3);
				
				// add found app to the list
				foundApps.add( new InstalledApp(name, appID, version) );
			}
			
			// convert to regular InstalledApp array
			//InstalledApp[] foundInstalledApps = new InstalledApp[ foundApps.size() ];
			//foundApps.toArray(foundInstalledApps);
			InstalledApp[] foundInstalledApps = foundApps.toArray(new InstalledApp[ foundApps.size() ]);
			
			// save to Device instance
			return foundInstalledApps;
			
		} // end of if command successful
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processDeviceStart() {
		
		// define what to do
		if ( currentTask.getDestinationDevice().isEmulator() ) {
			// in case of emulator, open it
			// returns true if opening a file or directory was successful
			return fileOperator.openFile("/Applications/VirtualBox.app"); // used to be "/Applications/Palm Emulator.app"
		}
		
		// return successfully
		return true;
	}
	
	/**
	 * @return Boolean value indicating success (true) or failure (false).
	 */
	private boolean processEmulatorEnableHostMode() {
		
		// internal variables
		boolean sshSuccess = false;
		String feedbackMessage = "Host Mode cannot be enabled for non-emulator devices.";
		
		// only available on emulator, so check device
		if ( currentTask.getDestinationDevice().isEmulator() ) {
			
			feedbackMessage = "You can now open http://localhost:5580\nin a WebKit browser, such as Chrome or Safari.";
			
			// check version
			String version = currentTask.getDestinationDevice().getVersion();
			int versionINT = convertVersionToInt(version);
			// only for 1.4.1 - 1.4.5 versions (2.0 is enabled by default)
			if ( versionINT >= 141 && versionINT <= 145 ) {
				// TODO setup SSH tunneling on port 5580
				// ssh -p 5522 -L 5580:localhost:8080 root@localhost 
				
				// build the system command we want to run
			    List<String> commands = new ArrayList<String>();
			    commands.add("/bin/bash");
			    commands.add("ssh"); // ssh
   			    commands.add("-p");
   			    commands.add("5522");
   			    commands.add("-L");
   			    commands.add("5580:localhost:8080");
   			    commands.add("root@localhost");
			    
			    // execute the command
			    Object[] obj = execute(commands);
				int result = ((Integer) obj[0]); // cast as Integer
			
				// setup successful if result code is 0
				if (result == 0) {
					sshSuccess = true;
				}
			} else if ( versionINT >= 200) {
				// enabled by default
				sshSuccess= true;
				feedbackMessage += "\n\nIn the future you can directly open this URL\nin your browser, as the emulator has Host Mode\nenabled by default (SDK 2.0+).";
			} else {
				// no host mode possible
				feedbackMessage = "Sorry, Host Mode could not be enabled for this emulator (version "+version+").\nSDK 1.4.1 or higher is required.";
			}			
		}
		
		// set feedback
		currentTask.setReport(feedbackMessage);
			
		// finally
		return true;
	}
}