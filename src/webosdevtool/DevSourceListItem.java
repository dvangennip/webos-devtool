/**
 * SourceListItem extension class
 * Required to add a variable that refers back to its DevSourceItem instance.
 */
 
package webosdevtool;

import javax.swing.ImageIcon;

import com.explodingpixels.macwidgets.SourceListItem;

public class DevSourceListItem extends SourceListItem {
	
	private DevSourceItem refToThisDevSourceItem;
	
	public DevSourceListItem (String name, ImageIcon icon, DevSourceItem ref) {
		super(name, icon);
		refToThisDevSourceItem = ref;
	}
	
	protected DevSourceItem getSourceItem() {
		return refToThisDevSourceItem;
	}
}