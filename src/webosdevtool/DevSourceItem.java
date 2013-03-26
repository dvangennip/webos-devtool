/**
 * Interface that bridges devices and projects as source items.
 * Similarity is required as the action panel should be able
 * to access both in a transparent manner.
 */

package webosdevtool;

import java.net.URL;

import java.awt.Image;
import javax.swing.ImageIcon;

import com.explodingpixels.macwidgets.SourceListItem;

public class DevSourceItem {
	
	// Class variables
	
	public static final int DEVICE = 0;
	public static final int PROJECT = 1;
	
	private Devtool devtool;
	private boolean isDevice = false;
	private boolean isEnabled = false;
	private int jsFrameworkType = 0;
	private String name;
	private String version;
	private String uniqueID;
	private String location;
	private ImageIcon iconSmall;
	private ImageIcon iconLarge;
	private DevSourceListItem sourceItem;
	
	// Constructor
	
	public DevSourceItem(Devtool myParent, int sourceType, String name, String location) {
		
		this.devtool = myParent;
		if (sourceType == DEVICE) {
			this.isDevice = true;
		}
		// set name
		this.setName(name);
		// set location (i.e. folder or unique identifier)
		this.setLocation(location);
		// set version
		this.setVersion("0.0.0");
		// set uniqueID (init variable)
		this.setID(null);
		
		// if valid add to sourcelist
		iconSmall = new ImageIcon();
		iconLarge = new ImageIcon();
		this.sourceItem = new DevSourceListItem(this.name, this.iconSmall, this);
		if (this.isDevice) {
			devtool.devSourceList.addDeviceToSourceList(this.sourceItem);
		} else {
			devtool.devSourceList.addProjectToSourceList(this.sourceItem);
		}
		
		// TODO - when not true?
		this.isEnabled = true;
	}
	
	// Methods ---------------------------------------------------
	
	protected void dispose() {
		// remove all external references
		if (isDevice) {
			devtool.devSourceList.removeDeviceFromSourceList(sourceItem);
		} else {
			devtool.devSourceList.removeProjectFromSourceList(sourceItem);
		}
	}
	
	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
		// check if name is not null
		if (this.name == null) {
			if (this.isDevice) {
				this.name = "Unknown device";
			} else {
				this.name = "Untitled project";
			}
		}
		// check if sourceItem is initialised - otherwise exception on construction
		if (sourceItem != null) {
			sourceItem.setText(this.name);
		}
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public boolean isDevice() {
		return isDevice;
	}
	
	public boolean isProject() {
		return !isDevice; // inverse
	}
	
	public Devtool getDevtool() {
		return devtool;
	}
	
	public SourceListItem getSourceListItem() {
		return sourceItem;
	}
	
	public ImageIcon getSmallIcon() {
		return iconSmall;
	}
	
	protected void setSmallIcon(URL iconResource) {
		// icon cannot be a new ImageIcon as ref is lost
		// thus current object should be updated
		iconSmall.setImage( new ImageIcon(iconResource).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH) );
	}
	
	public ImageIcon getLargeIcon() {
		return iconLarge;
	}
	
	protected void setLargeIcon(URL iconResource) {
		// icon cannot be a new ImageIcon as ref is lost
		// thus current object should be updated
		iconLarge.setImage( new ImageIcon(iconResource).getImage() );
	}
		
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		if (location != null) {
			this.location = location;
		} else {
			if (isDevice) {
					this.location = "tcp";
			} else {
				this.location = "";
			}
		}
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String newVersion) {
		if (newVersion != null) {
			this.version = newVersion;
		} else {
			this.version = "0";
		}
	}
	
	/**
	 * @return String with either appID (e.g., com.palm.app) or device NDUID (e.g., c69ddacef0160fada37edd4c89a26412420db120)
	 */
	public String getID() {
		return uniqueID;
	}
	
	public void setID(String id) {
		if (id != null) {
			this.uniqueID = id;
		} else {
			this.uniqueID = "?";
		}
	}
	
	/**
	 * Method returns true if item requires / supports an Enyo framework application, false if else (e.g. Mojo framework).
	 * For a project this means it is an Enyo application, for a device it means the device supports Enyo apps.
	 *
	 * @return True if Enyo framework is required / supported.
	 */
	public boolean isEnyoEnabled() {
		return (this.jsFrameworkType > 0);
	}

	/**
	 * @return The Enyo version as integer (0: no Enyo, 1: Enyo1, 2: Enyo2)
	 */
	public int getJSFrameworkType() {
		return this.jsFrameworkType;
	}
	
	/**
	 * If the item supports (<code>Device</code>) or requires (<code>Project</code>) the Enyo framework it is set via this method.
	 *
	 * @see getJSFrameworkType For an overview of the values that can be set.
	 * @param b Integer value which is set to the version of Enyo.
	 */
	public void setJSFrameworkType(int v) {
		if (v >= 0)
			this.jsFrameworkType = v;
	}
	
	/**
	 * Needs be overriden
	 */
	public String[] getSources() {
		return null;
	}
	
	/**
	 * Needs be overriden
	 */
	public boolean hasPackage() {
		return false;
	}
	
	/**
	 * Needs be overriden
	 */
	public boolean hasService() {
		return false;
	}
	
	/**
	 * Needs be overriden, intends to update device or project variables.
	 */
	public void update() {}
}