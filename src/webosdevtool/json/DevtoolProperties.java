package webosdevtool.json;

public class DevtoolProperties {
	
	// Variables - all have default values to avoid init troubles
	
	/**
	 * Version refers to revision of this data format
	 */
	public int version = 0;
	
	// Preferences
	
	public String defaultBrowser = null;
	public boolean cleanLogOnRestart = false;
	
	// Recent projects
	
	public String recentlyOpened0 = null;
	public String recentlyOpened1 = null;
	public String recentlyOpened2 = null;
	public String recentlyOpened3 = null;
	public String recentlyOpened4 = null;
	public String recentlyOpened5 = null;
	public String recentlyOpened6 = null;
	public String recentlyOpened7 = null;
	public String recentlyOpened8 = null;
	public String recentlyOpened9 = null;
	public String openProject0 = null;
	public String openProject1 = null;
	public String openProject2 = null;
	public String openProject3 = null;
	public String openProject4 = null;
	public String openProject5 = null;
	public String openProject6 = null;
	public String openProject7 = null;
	public String openProject8 = null;
	public String openProject9 = null;
	
	// Constructor
	
	public DevtoolProperties () {}
	
	// Methods
	
	public String[] getRecentProjects() {
		
		// make an array
		String[] prjs = new String[10];
		
		// fill the new array
		prjs[0] = recentlyOpened0;
		prjs[1] = recentlyOpened1;
		prjs[2] = recentlyOpened2;
		prjs[3] = recentlyOpened3;
		prjs[4] = recentlyOpened4;
		prjs[5] = recentlyOpened5;
		prjs[6] = recentlyOpened6;
		prjs[7] = recentlyOpened7;
		prjs[8] = recentlyOpened8;
		prjs[9] = recentlyOpened9;
		
		// return array
		return prjs;
	}
	
	public String[] getOpenProjects() {
		
		// make an array
		String[] prjs = new String[10];
		
		// fill the new array
		prjs[0] = openProject0;
		prjs[1] = openProject1;
		prjs[2] = openProject2;
		prjs[3] = openProject3;
		prjs[4] = openProject4;
		prjs[5] = openProject5;
		prjs[6] = openProject6;
		prjs[7] = openProject7;
		prjs[8] = openProject8;
		prjs[9] = openProject9;
		
		// return array
		return prjs;
	}
	
	public void addOpenProject(String location) {
		
		// add it to first open slot
		String[] prjs = getOpenProjects();
		boolean success = false;
		
		// a value of null means the slot is available
		for (int i = 0; i < prjs.length; i++) {
			if (prjs[i] == null) {
				// add project location
				prjs[i] = location;
				// exit with success
				success = true;
				break;
			}
		}
		
		// if no free slot is available
		// pick first one (not ideal, but it is somewhat better than nothing)
		if (!success) {
			prjs[0] = location;
		}
		
		// finally, save array into variables
		openProject0 = prjs[0];
		openProject1 = prjs[1];
		openProject2 = prjs[2];
		openProject3 = prjs[3];
		openProject4 = prjs[4];
		openProject5 = prjs[5];
		openProject6 = prjs[6];
		openProject7 = prjs[7];
		openProject8 = prjs[8];
		openProject9 = prjs[9];
	}
	
	public void removeOpenProject(String location) {
		
		// try to find it
		String[] prjs = getOpenProjects();
		boolean found = false;
		
		for (int i = 0; i < prjs.length; i++) {
			if (prjs[i] != null) {
				if ( prjs[i].equals(location) ) {
					// remove it by setting value to null
					prjs[i] = null;
					// exit successfully
					found = true;
					break;
				}
			}
		}
		
		// finally, save array into variables
		// only necessary when found (and some value has been adjusted)
		if (found) {
			openProject0 = prjs[0];
			openProject1 = prjs[1];
			openProject2 = prjs[2];
			openProject3 = prjs[3];
			openProject4 = prjs[4];
			openProject5 = prjs[5];
			openProject6 = prjs[6];
			openProject7 = prjs[7];
			openProject8 = prjs[8];
			openProject9 = prjs[9];
		}
	}
	
	public void addRecentProject(String location) {
		
		// see if it is already available
		String[] prjs = getRecentProjects();
		boolean found = false;
		
		for (int i = 0; i < prjs.length; i++) {
			if (prjs[i] != null) {
				if ( prjs[i].equals(location) ) {
					found = true;
					break;
				}
			}
		}
		
		// if not found add it
		if (!found) {
			// shift all values down the list
			for (int i = 1; i < prjs.length; i++) {
				prjs[i] = prjs[i-1].toString(); // copy value instead of reference?
			}
			// add new value
			prjs[0] = location;
			
			// save array into variables
			recentlyOpened0 = prjs[0];
			recentlyOpened1 = prjs[1];
			recentlyOpened2 = prjs[2];
			recentlyOpened3 = prjs[3];
			recentlyOpened4 = prjs[4];
			recentlyOpened5 = prjs[5];
			recentlyOpened6 = prjs[6];
			recentlyOpened7 = prjs[7];
			recentlyOpened8 = prjs[8];
			recentlyOpened9 = prjs[9];
		}
	}
}