/**
 * Class holds all data for a project.
 * All variables are private but relevant accessor methods are available.
 * Actions on a project are coordinated by this class.
 */
 
package webosdevtool;

import webosdevtool.json.AppInfo;
import webosdevtool.json.Framework;
import webosdevtool.json.Sources;

import java.net.URL;

/**
 * Extension of DevSourceItem
 */
public class Project extends DevSourceItem {
	
	// Class variables

	private AppInfo appInfo;
	private Framework framework;
	private String[] sources;
	private boolean hasPackage;
	private boolean hasService;
	
	// Constructor

	public Project (Devtool myParent, String name, String folder) {
		
		// call DevSourceItem constructor
		super(myParent, DevSourceItem.PROJECT, name, folder);
		
		// do everything else in separate method which can also be called separately
		this.update();
	}
	
	// Methods
	
	/**
	 * @return Array of source file locations in <code>String</code> format.
	 */
	@Override
	public String[] getSources() {
		return sources;
	}
	
	/**
	 * @return The application info based on <code>appinfo.json</code> file, returned as <code>AppInfo</code> Object.
	 */
	public AppInfo getAppInfo() {
		return appInfo;
	}
	
	/**
	 * @return True if there is an <code>app_package</code> folder.
	 */
	@Override
	public boolean hasPackage() {
		return hasPackage;
	}
	
	/**
	 * @return True if there is an <code>app_package</code> folder.
	 */
	@Override
	public boolean hasService() {
		return hasService;
	}
	
	/**
	 * Method gets the info for this project from its files
	 * Variables are then updated
	 */
	@Override
	public void update() {
		
		// get info on project, based on folder location
		appInfo = this.getDevtool().fileOperator.getProjectAppInfo( getLocation() );
		framework = this.getDevtool().fileOperator.getProjectFramework( getLocation() );
		this.setJSFrameworkType( this.getDevtool().fileOperator.checkProjectFolderValidity( getLocation() ) );

		sources = this.getDevtool().fileOperator.getProjectSources( getLocation() );
		// check if some sources were returned, else it may be an Enyo app
		if (sources == null) {
			// get it again
			sources = this.getDevtool().fileOperator.getProjectEnyoSources( getLocation() );
		}
		// check for availability of package folder
		hasPackage = this.getDevtool().fileOperator.checkFolderValidity(getLocation() + "/app_package");
		// check for availability of service folder
		hasService = this.getDevtool().fileOperator.checkFolderValidity(getLocation() + "/app_service");
		// TODO: add service sources
		/*if (hasService) {
			sources = sources.concat( this.getDevtool().fileOperator.getProjectSources( getLocation() ); );
		}*/
		
		// set info
		this.setName( appInfo.title );
		this.setID( appInfo.id );
		this.setVersion( appInfo.version );
		
		// set small icon
		if (appInfo.miniicon != null) {
			try {
				URL iconURL = new URL( "file:" + getLocation() + "/app_src/" + appInfo.miniicon );
				this.setSmallIcon( iconURL );
			}
			catch (java.net.MalformedURLException mue) {
				System.out.println("Icon could not be loaded: "+mue);
			}
		} else {
			// set no icon
			this.setSmallIcon( getClass().getResource("/webosdevtool/images/icon-default-16x16.png") );
		}
		
		// set large icon (unique one or default)
		if (appInfo.icon != null) {
			try {
				URL iconURL = new URL( "file:" + getLocation() + "/app_src/" + appInfo.icon );
				this.setLargeIcon( iconURL );
			}
			catch (java.net.MalformedURLException mue) {
				System.out.println("Icon could not be loaded: "+mue);
			}
		} else {
			// set default icon instead
			this.setLargeIcon( getClass().getResource("/webosdevtool/images/icon-default-64x64.png") );
		}
		
		// ask for update of view
		this.getDevtool().updateItem( this );
	} // end of update()
}