/**
 *
 */
 
package webosdevtool.jslint;

import java.util.List;

import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;

public class LintSourceList extends SourceList {
	
	// Variables
	
	private LintFrame lintFrame;
	
	private SourceListModel sourceListModel;
	private SourceListCategory defaultCategory;
	
	// Constructor
	
	public LintSourceList (LintFrame myParent) {
		this(new SourceListModel(), myParent);
	}
	
	public LintSourceList (SourceListModel model, LintFrame myParent) {
		
		// call super constructor
		super(model);
		this.useIAppStyleScrollBars();
		this.sourceListModel = model;
		
		// reference to main class
		this.lintFrame = myParent;
		
		// add categories
		defaultCategory = new SourceListCategory("Sources");
		sourceListModel.addCategory(defaultCategory);
		
		// add selection listener
		this.addSourceListSelectionListener( new LintItemSelectionListener(lintFrame) );
	}
	
	// Methods ------------------------------------------------------
	
	protected void setProjectName(String name) {
		if (name != null) {
			defaultCategory.setText(name);
		} else {
			defaultCategory.setText("Unknown project");
		}
	}
	
	protected void removeAllItems() {
		
		List<SourceListItem> items = defaultCategory.getItems();
		
		// remove all if available
		for (int i = items.size(); i > 0; i--) {
			removeFromSourceList( items.get(i-1) );
		}
	}
  
	/**
	 *
	 */
	protected void addToSourceList(SourceListItem item) {
	
		try {
			sourceListModel.addItemToCategory(item, defaultCategory);
		}
		catch(java.lang.IllegalStateException ise) {
			System.out.println("IllegalStateException while adding device to source list");
		}
		
	}
	
	/**
	 *
	 */
	protected void removeFromSourceList(SourceListItem item) {
		
		try {
			sourceListModel.removeItemFromCategory(item, defaultCategory);
		}
		catch(java.lang.IllegalStateException ise) {
			System.out.println("IllegalStateException while removing device from source list");
		}
		
	}
}