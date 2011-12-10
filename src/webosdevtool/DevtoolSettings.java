package webosdevtool;

import webosdevtool.process.FileOperator;
import webosdevtool.json.DevtoolProperties;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * Getting and setting preferences and recently opened projects.
 * <br />
 * Preferences<br />
 * <ul>
 *  <li>default browser</li>
 *  <li>Clear palm-log window on restart of logging</li>
 * </ul>
 * <br/>
 *
 * Project history<br />
 * <ul>
 *  <li>10 recent projects</li>
 *  <li>projects that were open last time</li>
 * </ul>
 */
 public class DevtoolSettings {
 	
 	// Variables
 	
 	/**
	 * Version refers to revision of data format
	 */
 	private static final int version = 1;
 	private static final String appSupportFolder = "/Users/username/Library/Application Support/";
	private static final String appFolderName = "WebOSdevtool";
 	
 	// Constructor
 	
 	public DevtoolSettings () {}
 	
 	// Methods
 	
 	public static String getPropertyDefaultBrowser() {
 		DevtoolProperties ps = getSettings();
 		return ps.defaultBrowser;
 	}
 	
 	public static void setPropertyDefaultBrowser(String browser) {
 		
 		if (browser != null) {
	 		// first get settings
	 		DevtoolProperties ps = getSettings();
	 		
	 		// adjust value
	 		ps.defaultBrowser = browser;
	 		
	 		// finally
	 		saveSettings(ps);
 		}
 	}
 	
 	public static boolean getPropertyClearLog() {
 		DevtoolProperties ps = getSettings();
 		return ps.cleanLogOnRestart;
 	}
 	
 	public static void setPropertyClearLog(boolean state) {
 		
 		// first get settings
 		DevtoolProperties ps = getSettings();
 		
 		// adjust value
 		ps.cleanLogOnRestart = state;
 		
 		// finally
 		saveSettings(ps);
 	}
 	
 	public static String[] getPreviouslyOpenProjects() {
 		DevtoolProperties ps = getSettings();
 		return ps.getOpenProjects();
 	}
 	
 	public static void addOpenProject(String location) {
 		
 		if (location != null) {
	 		// first get settings
	 		DevtoolProperties ps = getSettings();
	 		
	 		// get open projects
	 		String[] prjs = ps.getOpenProjects();
	 		// add to open projects list
	 		ps.addOpenProject(location);
	 		// add to recent projects list
	 		ps.addRecentProject(location);
	 		
	 		// finally
	 		saveSettings(ps);
 		}
 	}
 	
 	public static void removeOpenProject(String location) {
 		
 		if (location != null) {
	 		// first get settings
	 		DevtoolProperties ps = getSettings();
	 		
	 		// remove from open projects list
	 		ps.removeOpenProject(location);
	 		
	 		// finally
	 		saveSettings(ps);
 		}
 	}
 	
 	public static String[] getRecentProjects() {
 		DevtoolProperties ps = getSettings();
 		return ps.getRecentProjects();
 	}
 	
 	private static DevtoolProperties getSettings() {
 		
 		// TODO
 		String settingsLocation = null;
 		
 		// first check if file is available
 		if ( FileOperator.checkFileValidity(null) ) {
 			
 			// get data
 			DevtoolProperties ps = null;
 			Gson gson = new Gson();
 			
			try {
				ps = gson.fromJson( FileOperator.getFileContents(settingsLocation), DevtoolProperties.class);
			}
			catch (JsonParseException jse) {
				System.out.println("JSON error while parsing DevtoolProperties:\n"+jse);
			}
 			
 			// check version, if incompatible do something
 			if (ps.version == version) {
 				return ps;
 			}
 		}
 		// if settings file not available have it created
 		else {
 			return createSettings();
 		}
 		
 		return null;
 	}
 	
 	private static void saveSettings(DevtoolProperties properties) {
 		//
 	}
 	
 	private static DevtoolProperties createSettings() {
 		// create folder in /Libary/Application Support/
 		if ( FileOperator.checkFolderValidity(null) ) {
 			// check if app-specific folder is already available
 			if ( !FileOperator.checkFolderValidity(null) ) {
 				// if not, create it
 				FileOperator.createDirectory(null);
 			}
 		}
 		
 		// create new DevtoolProperties instance
 		DevtoolProperties ps = new DevtoolProperties();
 		// set data format version to this class format version
 		ps.version = version;
 		
 		// save the new settings
 		saveSettings(ps);
 		
 		return ps;
 	}
 }