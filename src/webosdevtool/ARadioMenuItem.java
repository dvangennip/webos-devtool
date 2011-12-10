package webosdevtool;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import java.awt.Toolkit;

/**
 * Class extends a <code>JRadioButtonMenuItem</code> instance which defines a menu item, and includes its keyboard accelerator shortcut.
 */
public class ARadioMenuItem extends JRadioButtonMenuItem {
	
  // variables
  
  // constructors
  public ARadioMenuItem () {
  	this(new String(""), false, false, 0);
  }
  
  public ARadioMenuItem (String labelText) {
  	this(labelText, false, false, 0);
  }
  
  public ARadioMenuItem (String labelText, int keyCode) {
  	this(labelText, true, false, keyCode);
  }
  
  /**
   * Most complete constructor which is internally used by the other, simplified constructor versions.
   * @param labelText Label for the menu item.
   * @param useShortcut True if a keyboard shortcut is to be assigned.
   * @param useShortcutShift True if SHIFT key should be used for the keyboard shortcut combo.
   * @param keyCode A KeyEvent key code (integer) such as KeyEvent.VK_O.
   */
  public ARadioMenuItem (String labelText, boolean useShortcut, boolean useShortcutShift, int keyCode) {
  	
  	// assign name
  	super(labelText);
  	
  	// assign key shortcut
  	if (useShortcut) {
	  	if (useShortcutShift) {
	  		setAccelerator(KeyStroke.getKeyStroke(keyCode, 
	             Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + java.awt.Event.SHIFT_MASK));
	  	} else {
	  		setAccelerator(KeyStroke.getKeyStroke(keyCode, 
	             Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ));
	  	}
  	}
  } // end of constructor
}