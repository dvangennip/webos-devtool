/**
 *
 */
 
package webosdevtool;

import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;

public class DevtoolSourceList extends SourceList {
	
	// Variables
	
	private Devtool devtool;
	
	private SourceListModel sourceListModel = null;
	private SourceListCategory deviceCategory = null;
	private SourceListCategory projectCategory = null;
	
	// Constructor
	
	public DevtoolSourceList (Devtool myParent) {
		this(new SourceListModel(), myParent);
	}
	
	public DevtoolSourceList (SourceListModel model, Devtool myParent) {
		
		// call super constructor
		super(model);
		this.useIAppStyleScrollBars();
		this.sourceListModel = model;
		
		// reference to main class
		this.devtool = myParent;
		
		// add categories
		deviceCategory = new SourceListCategory("Devices");
		sourceListModel.addCategory(deviceCategory);
		
		projectCategory = new SourceListCategory("Projects");
		sourceListModel.addCategory(projectCategory);
		
		// add selection listener
		this.addSourceListSelectionListener( new DevItemSelectionListener(devtool) );
	}
	
	// Methods ------------------------------------------------------
	
	// Accessor methods
  
	/**
	 *
	 */
	protected void addDeviceToSourceList(SourceListItem item) {
	
		try {
			sourceListModel.addItemToCategory(item, deviceCategory);
		}
		catch(java.lang.IllegalStateException ise) {
			System.out.println("IllegalStateException while adding device to source list");
		}
		
	}
	
	/**
	 *
	 */
	protected void removeDeviceFromSourceList(SourceListItem item) {
		
		try {
			sourceListModel.removeItemFromCategory(item, deviceCategory);
		}
		catch(java.lang.IllegalStateException ise) {
			System.out.println("IllegalStateException while removing device from source list");
		}
		
	}
	
	/**
	 *
	 */
	protected void addProjectToSourceList(SourceListItem item) {
		
		try {
			sourceListModel.addItemToCategory(item, projectCategory);
		}
		catch(java.lang.IllegalStateException ise) {
			System.out.println("IllegalStateException while adding project to source list");
		}
		
	}
	
	/**
	 *
	 */
	protected void removeProjectFromSourceList(SourceListItem item) {
		
		try {
			sourceListModel.removeItemFromCategory(item, projectCategory);
		}
		catch(java.lang.IllegalStateException ise) {
			System.out.println("IllegalStateException while removing project from source list");
		}
		
	}
}