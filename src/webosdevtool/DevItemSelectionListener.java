/**
 * Implementation of SourceListSelectionListener interface.
 */
 
package webosdevtool;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

public class DevItemSelectionListener implements SourceListSelectionListener {
	
	// Reference variable
	private Devtool devtool;
	
	// Constructor
	public DevItemSelectionListener (Devtool myParent) {
		this.devtool = myParent;
	}
	
	// Required method in interface - called when item is selected
	public void sourceListItemSelected(SourceListItem item) {
		
		// get the item reference
		// if an non-item is selected an exception may occur
		// e.g. clicking on the category label or closing its view causes trouble
		// exception is ignored as nothing will happen
		try {
			DevSourceListItem itemExtended = (DevSourceListItem) item;
			DevSourceItem selectedDevSourceItem = itemExtended.getSourceItem();
		
			// update the situation
			this.devtool.setCurrentItem(selectedDevSourceItem);
		}
		catch (java.lang.Exception e) {}
	}
	
}