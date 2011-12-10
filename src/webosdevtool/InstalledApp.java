package webosdevtool;

/**
 * Class InstalledApp holds information about one application which was found to be installed on a device.
 */
public class InstalledApp {
	
	private String name;
	private String id;
	private String version;
	
	/**
	 * Default constructor to store application data.
	 *
	 * @param name Name of the app, e.g. Calendar.
	 * @param id Unique ID String of the application, e.g. <code>com.palm.app.calendar</code>.
	 * @param version Version number as String, e.g. 2.1.0
	 * @see webosdevtool.Device
	 */
	public InstalledApp (String name, String id, String version) {
		if (name != null) {
			this.name = name;
		} else {
			this.name = "Unknown app";
		}
		if (id != null) {
			this.id = id;
		} else {
			this.id = "no.id.known";
		}
		if (version != null) {
			this.version = version;
		} else {
			this.version = "0";
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getVersion() {
		return this.version;
	}
}