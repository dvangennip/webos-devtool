/**
 * Implementation of SourceListSelectionListener interface.
 */
 
package webosdevtool.jslint;

import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

public class LintItemSelectionListener implements SourceListSelectionListener {
	
	// Reference variable
	private LintFrame lintFrame;
	
	// Constructor
	public LintItemSelectionListener (LintFrame myParent) {
		this.lintFrame = myParent;
	}
	
	// Required method in interface - called when item is selected
	public void sourceListItemSelected(SourceListItem item) {
		
		// get the item reference
		// if an non-item is selected an exception may occur
		// e.g. clicking on the category label or closing its view causes trouble
		// exception is ignored as nothing will happen
		try {
			LintSourceListItem itemExtended = (LintSourceListItem) item;
		
			// update the situation
			lintFrame.setCurrentItem(itemExtended);
		}
		catch (java.lang.Exception e) {}
	}
}