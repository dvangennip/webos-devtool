package webosdevtool.process;

import webosdevtool.json.*;

import java.lang.StringBuffer;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.awt.Desktop;

import java.net.URI;

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

/**
 * Class performs file system operations.
 * Opening, reading, parsing and writing of files can be done via its methods, hereby creating support for other methods.
 */
public class FileOperator {
	
	// Variables
	
	// Constructor
	
	public FileOperator () { }
	
	// Methods
	
	/**
	 * Method checks a project folder on its validity.
	 * It means it checks whether a project folder contains a <code>app_src</code> and <code>bin</code> folder.
	 * It also checks the validity of its appinfo.json and sources.json files.
	 *
	 * @param projectFolder Location of the project folder.
	 * @return True if the project is found to be valid.
	 */
	public static boolean checkProjectFolderValidity (String projectFolder) {
	    
	    // check if project folder is valid
		if ( !checkFolderValidity(projectFolder) ) {
			return false;
		}
		// check if src folder is valid
		if ( !checkFolderValidity(projectFolder + "/app_src") ) {
			return false;
		}
		// check if bin folder is valid
		if ( !checkFolderValidity(projectFolder + "/bin") ) {
			return false;
		}
		// check if appinfo.json is valid
		if ( !checkAppInfoFileValidity(projectFolder) ) {
			return false;
		}
		// check if sources.json is valid (Mojo apps only)
		if ( !checkSourcesFileValidity(projectFolder) ) {
			// if not valid perhaps it is an Enyo framework application
			if ( !checkDependsFileValidity(projectFolder) ) {
				// when also wrong, the folder is just wrong
				return false;
			}
		}
		
		// if no problems occured folder structure is valid
		return true;
	}
	
	/**
	 * Uses <code>checkFileValidity</code> to check appinfo.json file.
 	 * @return True if the file is found to be valid.
	 */
	public static boolean checkAppInfoFileValidity (String srcFolder) {
		return checkFileValidity( srcFolder + "/app_src/appinfo.json" );
	}
	
	/**
	 * Uses <code>checkFileValidity</code> to check sources.json file (for Mojo apps).
	 * @return True if the file is found to be valid.
	 */
	public static boolean checkSourcesFileValidity (String srcFolder) {
		return checkFileValidity( srcFolder + "/app_src/sources.json" );
	}
	
	/**
	 * Uses <code>checkFileValidity</code> to check depends.js file (for Enyo apps).
	 * @return True if the file is found to be valid.
	 */
	public static boolean checkDependsFileValidity (String srcFolder) {
		return checkFileValidity( srcFolder + "/app_src/depends.js" );
	}
	
	/**
	 * Checks whether a folder exists and can be read by the current user.
 	 * @return True if the folder is found to be valid.
	 */
	public static boolean checkFolderValidity (String folderLocation) {
		// try to open file
		File folder = new File(folderLocation);
		
		// check validity - existence and ability to read from it
		if ( folder.canRead() ) {
			//no problems occured
			return true;
		}
		
		// something happened, file cannot be considered valid
		return false;
	}
	
	/**
	 * Checks whether a file exists and can be read by the current user.
	 * This method does not check whether the contents of a file make sense,
	 * just availability and readability.
 	 * @return True if the file is found to be valid.
	 */
	public static boolean checkFileValidity (String fileLocation) {
		// try to open file
		File file = new File(fileLocation);
		
		// check validity - existence and ability to read from it
		if ( file.canRead() ) {
			//no problems occured
			return true;
		}
		
		// something happened, file cannot be considered valid
		return false;
	}
	
	public static AppInfo getProjectAppInfo (String projectFolder) {
		
		// init so it can be returned
		AppInfo appInfo = null;
		
		// open appinfo.json
		File appInfoFile = new File(projectFolder+"/app_src/appinfo.json");
		
		// check validity
		if ( appInfoFile.canRead() ) {
			
			Gson gson = new Gson();
			try {
				appInfo = gson.fromJson( getFileContents(appInfoFile), AppInfo.class);
			}
			catch (JsonParseException jse) {
				System.out.println("JSON error while parsing appinfo");
			}
		}
		
		return appInfo;
	}
	
	public static Framework getProjectFramework (String projectFolder) {
		
		// init so it can be returned
		Framework framework = null;
		
		// open appinfo.json
		File frameworkFile = new File(projectFolder+"/app_src/framework_config.json");
		
		// check validity
		if ( frameworkFile.canRead() ) {
			
			Gson gson = new Gson();
			try {
				framework = gson.fromJson( getFileContents(frameworkFile), Framework.class);
			}
			catch (JsonParseException jse) {
				System.out.println("JSON error while parsing framework");
			}
		}
		
		return framework;
	}
	
	/**
	 * Returns an array of source file locations for Mojo apps.
	 *
	 * @param projectFolder String value of the project folder location (root).
	 * @return <code>String</code> array of Javascript source files
	 */
	public static String[] getProjectSources (String projectFolder) {
		
		// use a Vector as storage
		Vector sourceData = new Vector();
		
		// open sources.json
		File sourcesFile = new File(projectFolder+"/app_src/sources.json");
		
		// check validity
		if ( sourcesFile.canRead() ) {
			
			// get data from file
			String data = getFileContents(sourcesFile);
			
			// use regex to get javascript locations from data string
			
			// GETS app <anything except ,> *.js STRING AT ONCE FROM UNEDITED DATA
			// ((app){1}([^,]+)(\.js){1})
			// $0
			
			// create regex pattern and matcher
			Pattern scriptRegex = Pattern.compile("((app){1}([^,]+)(\\.js){1})");
			Matcher myMatcher = scriptRegex.matcher(data);
			
			// find matches (true if so, false if no match found anymore)
			while ( myMatcher.find() ) {
				
				// get result
				String scriptMatch = myMatcher.group();
				
				// remove all instances of \ to get a valid URL
				scriptMatch = scriptMatch.replaceAll("\\\\", "");
				
				// add to Vector
				sourceData.add( scriptMatch );
			}
		}
		
		// only return a String[] if there is data
		if (sourceData.size() == 0) {
			return null;
		} else {
			// create String array from Vector so it can be returned
			String[] sources = new String[sourceData.size()];
			try {
				sourceData.toArray(sources);
			}
			catch (java.lang.ArrayStoreException ase) {}
			catch (java.lang.NullPointerException npe) {}
			
			return sources;
		}
	}
	
	/**
	 * Returns an array of source file locations for Enyo apps.
	 * NOTE: Enyo's <code>depends.js</code> files are not included in the sources array.
	 *
	 * @param projectFolder String value of the project folder location (root).
	 * @return <code>String</code> array of Javascript source files
	 */
	public static String[] getProjectEnyoSources (String projectFolder) {
		
		// use a Vector as storage
		Vector sourceData = new Vector();
		
		// recursively work over each depends.js file, starting at the main one
		getProjectEnyoDependsSources(projectFolder+"/app_src/", sourceData);
		// when done sourceData should have some data
		
		// only return a String[] if there is data
		if (sourceData.size() == 0) {
			return null;
		} else {
			// create String array from Vector so it can be returned
			String[] sources = new String[sourceData.size()];
			try {
				sourceData.toArray(sources);
			}
			catch (java.lang.ArrayStoreException ase) {}
			catch (java.lang.NullPointerException npe) {}
			
			return sources;
		}
	}
	
	/**
	 * Recursively goes over <code>depends.js</code> files to get all Javascript source files for Enyo apps.
	 *
	 * If the given location is a folder, it will try to read a <code>depends.js</code> file in this folder.
	 * For this <code>depends.js</code> file it gets the listed source files
	 * (if of Javascript type, based on <code>.js</code> extension).
	 * When a folder is found this method will call itself again for that folder.
	 * The process continues until all source files are found.
	 *
	 * @param location <code>String</code> value of the source file location.
	 * @param srcData <code>Vector</code> to add found source files to as String values.
	 */
	private static void getProjectEnyoDependsSources (String location, Vector srcData) {
		
		// open the folder location's depends.js file
		File sourcesFile = new File(location+"depends.js");
		
		// check validity
		if ( sourcesFile.canRead() ) {
			
			// get data from file
			String data = getFileContents(sourcesFile);
			
			// use regex to get javascript locations from data string
			// GETS "SomeFileNameOrFolder", STRING AT ONCE FROM UNEDITED DATA
			// \"(.+?)\",?
			// $1
			
			// create regex pattern and matcher
			Pattern scriptRegex = Pattern.compile("\"(.+?)\",?");
			Matcher myMatcher = scriptRegex.matcher(data);
			
			// find matches (true if so, false if no match found anymore)
			while ( myMatcher.find() ) {
				
				// get result
				String scriptMatch = myMatcher.group(1);
				
				// if match ends in .js add it to Vector
				if ( scriptMatch.matches(".+\\.js$") ) {
					
					String scriptLocation = location + scriptMatch;
					// remove absolute location part (everything up to /app_src/) to retain a relative location
					// as it is used within the rest of the program
					scriptLocation = scriptLocation.replaceFirst(".+?\\/app_src\\/", "");
					
					// add file location to Vector
					srcData.add( scriptLocation );
				}
				// else if it a folder (ends with /) invoke this method recursively for this folder
				else if ( scriptMatch.matches(".+\\/$") ) {
					// invoke with correct location
					getProjectEnyoDependsSources(location+scriptMatch, srcData);
				}	
			} // end while loop
		} // end can_read_file
	}
	
	/**
	 * Opens a file or directory using the OS default application or
	 * file manager in case the specified path is a folder.
	 * @param path File or folder path to open.
	 * @return True if the path was opened successfully.
	 */
	public static boolean openFile(String path) {
		
		// native Java way using Desktop class
		File location = new File( path );
		if ( location.exists() && Desktop.isDesktopSupported() ) {
			Desktop d = Desktop.getDesktop();
			try {
				d.open(location);
			}
			catch (java.io.IOException ioe) {
		    	System.out.println("IOException while opening file: "+ioe);
		    	// return false in this case
		    	return false;
		    }
		} else {
			// too bad...
			return false;
		}
		
		// return successfully
		return true;
	}
	
	/**
	 * Opens a webpage (based on URL path) using the OS default browser.
	 * @param path File or folder path to open.
	 * @return True if the path was opened successfully.
	 */
	public static boolean openWebpageInBrowser(String path) {
		
		// native Java way using Desktop class
		if (Desktop.isDesktopSupported() ) {
					 
			Desktop desktop = Desktop.getDesktop();
		 
			if ( desktop.isSupported( Desktop.Action.BROWSE ) ) {
			 
				try {
					String URIString = path; 
					URI uri = new URI( URIString );
					desktop.browse( uri );
				}
				catch (java.net.URISyntaxException use) {
					System.out.println( use.getMessage() );
					return false;
				}
				catch (java.io.IOException ioe) {
					System.out.println( ioe.getMessage() );
					return false;
				}	
			}
		}
		
		// return successfully
		return true;
	}
	
	/**
	 * Tests whether a file exists and can be read.
	 * @param path File or folder path to check.
	 * @return True if the path was checked successfully.
	 */
	public static boolean fileExists(String path) {
		File location = new File( path );
		
		if (location.exists() && location.canRead()) {
			return true;
		}
		// else
		return false;
	}
	
	public static BufferedReader getFileReader(String sourcePath) {
		
		try {
			File file = new File(sourcePath);
			
			if ( file.canRead() ) {
					BufferedReader bfReader = new BufferedReader(new FileReader(file));;
					return bfReader;
			}
		}
		catch (java.io.FileNotFoundException fnfe) {
			System.out.println("FileOperator: file not found while getting contents");
			return null;
		}
		catch (java.io.IOException ioe) {
			System.out.println("FileOperator: IO exception while getting contents");
			return null;
		}
		
		// if it all fails
		return null;
	}
	
	/**
	 * Method reads from the specified <code>InputStream</code> and returns the resulting content
	 * as one String upon completion. This method is not meant to read from a continuous stream as it
	 * blocks. Also linefeed characters (\n) are preserved in the output.
	 * @param input <code>InpuStream</code> of content to read.
	 * @return String of file contents or <code>null</code> if file could not be read.
	 */
	public static String getStreamContents (InputStream input) {
		
		// output String
		String output = "";
		
		// init stream
		InputStream inputStream = input;
		
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			
			// TODO figure out how to break the IO lock here
			while ( (line = bufferedReader.readLine()) != null) {
				
				// add new content to editor pane of LogFrame
				output += line+"\n"; // preserve linefeed character
			}
		}
		catch (java.io.IOException ioe) {
			// TODO handle this better
			ioe.printStackTrace();
		}
		catch (java.lang.Throwable t) {
			// TODO handle this better
			t.printStackTrace();
		}
		finally {
			try {
				// finally close the streams
				bufferedReader.close();
				inputStream.close();
			}
			catch (java.io.IOException ioe) {
				// ignore this one
			}
		}
		
		// only return the output if it holds some data
		if (output.length() > 0) {
			return output;
		}
		// else return without success
		return null;
	}
	
	/**
	 * @param sourcePath String of filepath to read.
	 * @return String of file contents or <code>null</code> if file could not be read.
	 */
	public static String getFileContents (String sourcePath) {
		File file = new File(sourcePath);
		
		if ( file.canRead() ) {
			return getFileContents(file);
		}
		
		// else return without success
		return null;
	}
	
	/**
	 * Opens a file or directory using the OS default application or
	 * file manager in case the specified path is a folder.
	 * @param sourceFile <code>File</code> instance to read.
	 * @return String of file contents or <code>null</code> if file could not be read.
	 */
	private static String getFileContents (File sourceFile) {
				
		// read file contents
		StringBuffer sb = null; // needs be initialised to be able to return it
		try {
			BufferedReader fr = new BufferedReader(new FileReader(sourceFile));
		    sb = new StringBuffer();
		    String data;
		    while( (data = fr.readLine() ) != null) sb.append(data);
		}
		catch (java.io.FileNotFoundException fnfe) {
			System.out.println("FileOperator: file not found while getting contents");
		}
		catch (java.io.IOException ioe) {
			System.out.println("FileOperator: IO exception while getting contents");
		}
	    
	    // return string representation
		return sb.toString();
	}
	
	/**
	 * Method creates a new directory for the given location if it does not yet exist.
	 * @param location Path of the directory to create.
	 * @return True if the directory was created successfully.
	 */
	public static boolean createDirectory(String location) {
		
		File newDir = new File(location);
		
		if ( newDir.exists() ) {
			// no need to do anything
			return true;
		} else {
			// try to create directory
			return newDir.mkdir();
		}
	}
}