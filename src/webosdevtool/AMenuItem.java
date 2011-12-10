package webosdevtool;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.Toolkit;

/**
 * Class extends a JMenuItem instance which defines a menu item, and includes its keyboard accelerator shortcut.
 */
public class AMenuItem extends JMenuItem {
  
  // constructors
  
  public AMenuItem () {
  	this(new String(""), false, false, 0, false);
  }
  
  public AMenuItem (String labelText) {
  	this(labelText, false, false, 0, false);
  }
  
  public AMenuItem (String labelText, int keyCode) {
  	this(labelText, true, false, keyCode, false);
  }
  
  public AMenuItem (String labelText, int keyCode, boolean noAppleKey) {
  	this(labelText, true, false, keyCode, noAppleKey);
  }
  
  public AMenuItem (String labelText, boolean useShortcut, boolean useShortcutShift, int keyCode) {
  	this(labelText, useShortcut, useShortcutShift, keyCode, false);
  }
  
  /**
   * Most complete constructor which is internally used by the other, simplified constructor versions.
   * @param labelText Label for the menu item.
   * @param useShortcut True if a keyboard shortcut is to be assigned.
   * @param useShortcutShift True if SHIFT key should be used for the keyboard shortcut combo.
   * @param noAppleKey True if the Apple command key \u2318 should not be used for the shortcut (e.g. for F5 key events).
   * @param keyCode A KeyEvent key code (integer) such as KeyEvent.VK_O.
   */
  public AMenuItem (String labelText, boolean useShortcut, boolean useShortcutShift, int keyCode, boolean noAppleKey) {
  	
  	// assign name
  	super(labelText);
  	
  	// assign key shortcut
  	if (useShortcut) {
  		if (noAppleKey) {
  			// can be used for Fx-keys, thus without cmd
  			setAccelerator(KeyStroke.getKeyStroke(keyCode, 0));
  		} else if (useShortcutShift) {
  			// cmd + shift + key
	  		setAccelerator(KeyStroke.getKeyStroke(keyCode, 
	             Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + java.awt.Event.SHIFT_MASK));
	  	} else {
	  		// cmd + key
	  		setAccelerator(KeyStroke.getKeyStroke(keyCode, 
	             Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() ));
	  	}
  	}
  } // end of constructor
}