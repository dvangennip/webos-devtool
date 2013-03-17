/**
 * Class holds all data for a project.
 * All variables are private but relevant accessor methods are available.
 * Actions on a project are coordinated by this class.
 */
 
package webosdevtool;

/**
 * Extension of DevSourceItem
 */
public class Device extends DevSourceItem {
	
	// Class variables
	
	public static final int EMULATOR = 0;
	public static final int PHONE = 1;
	public static final int TABLET = 2;
	
	private boolean isEmulator;
	private int deviceType;
	private ARadioMenuItem menuItem;
	
	private InstalledApp[] installedApps;
	
	// Constructor
	
	public Device (Devtool myParent, String name, String nduid, String location) {
		
		// call DevSourceItem constructor
		// NOTE: name is not set yet, done below
		super(myParent, DevSourceItem.DEVICE, name, location);
		
		// set nduid as device ID
		this.setID(nduid);
		
		// use name to define the device
		name = name.trim().toLowerCase();
		// set name
		this.setName(name);
		
		// define device type
		if ( this.getName().equals("Emulator") ) {
			// set as emulator
			this.deviceType = Device.EMULATOR;
			this.isEmulator = true;
			this.setSmallIcon( getClass().getResource("/webosdevtool/images/device-emulator-16x16.png") );
			this.setLargeIcon( getClass().getResource("/webosdevtool/images/device-emulator-64x64.png") );
		} else {
			// set as non-emulator
			this.isEmulator = false;
			
			// decide between tablet and phone types
			// PHONE functions as a 'other..' category
			if ( this.getName().contains("TouchPad") ) {
				this.deviceType = Device.TABLET;
				this.setSmallIcon( getClass().getResource("/webosdevtool/images/device-tablet-16x16.png") );
				this.setLargeIcon( getClass().getResource("/webosdevtool/images/device-tablet-64x64.png") );
			} else {
				this.deviceType = Device.PHONE;
				this.setSmallIcon( getClass().getResource("/webosdevtool/images/device-phone-16x16.png") );
				this.setLargeIcon( getClass().getResource("/webosdevtool/images/device-phone-64x64.png") );
			}
			
			
		}
		
		// set version and app data
		// NOTE: the actual data is fed asynchronously, at least after initiating the device
		this.installedApps = null;
		this.setVersion("0");
		
		// set device menu item
		this.menuItem = getDevtool().devwindow.menuBar.addDeviceToMenu(this);
	}
	
	// Methods
	
	/**
	 * Set the device name. Method tries to beautify common device names.
	 * For example, 'emulator' becomes 'Emulator' and 'touchpad' becomes 'TouchPad'.
	 * This method overrides its super method but calls it at the end to actually store the name.
	 * @param name Name of the device
	 */
	@Override
	public void setName(String inName) {
		String name = inName.toLowerCase();
		
		if (name.contains("castle")) {
			result = "Palm Pre";
		} else if (name.contains("pixie")) {
			result = "Palm Pixi";
		} else if (name.contains("verizon") && name.contains("pixie")) {
			result = "Palm Pixi Plus";
		} else if (name.contains("castleplus")) {
			result = "Palm Pre Plus";
		} else if (name.contains("roadrunner")) {
			result = "Palm Pre2";
		} else if (name.contains("broadway")) {
			result = "HP Veer";
		} else if (name.contains("mantaray")) {
			result = "HP Pre3";
		} else if (name.contains("topaz")) {
			result = "HP TouchPad";
		} else if (name.contains("opal")) {
			result = "HP TouchPad Go";
		} else if (name.contains("stingray")) {
			result = "Stingray";
		} else if (name.contains("windsor")) {
			result = "Windsor";
		} else if (name.contains("sdk") || name.contains("emulator")) {
			result = "Emulator";
		}
		
		super.setName(newName);
	}
	
	/**
	 * Method extends the super dispose() method, which is called at the end.
	 */
	public void dispose() {
		
		// remove device menu item
		getDevtool().devwindow.menuBar.removeDeviceFromMenu( menuItem );
		
		// rest is handled by super method
		super.dispose();
	}
	
	/**
	 * Method sets the version and updates Enyo enabled status based on the version.
	 * Enyo detection is thus done superficially but as the Enyo framework is only available to devices
	 * running webOS versions 3 and above it works as intended.
	 * This method overrides the one in the super <code>DevSourceItem</code> class.
	 *
	 * @param newVersion String containing the new version number (e.g. <code>3.1.0</code>).
	 */
	@Override
	public void setVersion(String newVersion) {
		if (newVersion != null) {
			super.setVersion( newVersion );
		} else {
			super.setVersion("0");
		}
		
		// update the Enyo support variable
		// this is based on Device version as int value (see TaskHandler for details on regex)
		int noDotVersion = Integer.parseInt( getVersion().replaceAll("(\\d+)\\.{1}(\\d{1})\\.{1}(\\d{1})\\d*", "$1$2$3") );
		// currently Enyo is only supported on webOS devices running versions 1.4.5+
		if (noDotVersion >= 145) {
			this.setEnyoEnabled(true);
		} else {
			this.setEnyoEnabled(false);
		}
	}
	
	public int getDeviceType() {
		return deviceType;
	}
	
	public boolean isEmulator() {
		return isEmulator;
	}
	
	public ARadioMenuItem getMenuItem() {
		return menuItem;
	}
	
	public void setMenuItem(ARadioMenuItem menuitem) {
		this.menuItem = menuItem;
	}
	
	public void setSelected(boolean selected) {
		// update menu item
		menuItem.setSelected( selected );
	}
	
	public InstalledApp[] getInstalledApps() {
		return installedApps;
	}
	
	public boolean hasInstalledApp(String id) {
		InstalledApp app = getInstalledApp(id);
		if (app != null) {
			return true;
		}
		// else
		return false;
	}
	
	public InstalledApp getInstalledApp(String id) {
		// loop over installed apps to find id
		for (int i = 0; i < installedApps.length; i++) {
			
			if ( installedApps[i].getID().equals(id) ) {
				return installedApps[i];
			}
		}
		
		// else
		return null;
	}
	
	public void setInstalledApps(InstalledApp[] apps) {
		// store array of apps
		this.installedApps = apps;
	}
}