package webosdevtool;

// Import packages

import webosdevtool.process.FileOperator;

import java.awt.Desktop;

import java.net.URI;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;

/**
 * Various <code>static</code> methods that support the viewing and browsing of HTML in text panes.
 * All methods are <code>static</code>, meaning there is no need to get an instance of this class to use the methods.
 */
public class DevtoolHTMLHelper {

	/**
	 * All methods are static so there is no need to construct an instance of this class.
	 */
	public DevtoolHTMLHelper() {}
	
	// Methods

	/**
	 * Applies HTML styling to a JEditorPane based on a specified stylesheet.
	 *
	 * Static method applies a HTMLEditorKit to the specified JEditorPane reference and styles
	 * this pane's stylesheet with the given CSS stylesheet. Because it works directly on the
	 * JEditorPane reference this method does not return anything.<br />
	 *
	 * This method should be called just after initialising the JEditorPane instance for which
	 * styling should be applied, at the very least before setting any content.
	 * 
	 * @param pane Instance of a <code>JEditorPane</code> for which the stylesheet will have CSS rules added.
	 * @param path Path to CSS stylesheet file which will be used to style the pane.
	 */
	public static void applyHTMLStyle(JEditorPane pane, String path) {
		
		// -- PREPARE PANE
		
		// add a HTMLEditorKit to the editor pane
		HTMLEditorKit kit = new HTMLEditorKit();
		pane.setEditorKit(kit);
				
		// get a stylesheet to add styles to
		StyleSheet styleSheet = kit.getStyleSheet();
		
		// -- GET STYLESHEET RULES FROM FILE
		
		String styleFile = FileOperator.getStreamContents( DevtoolHTMLHelper.class.getResourceAsStream(path) );
		
		if (styleFile != null) {
			// get the individual rules
			String[] styles = styleFile.split("\n");
			
			// -- ADD STYLE RULES
			
			// first line is skipped (can be used for some info)
			for (int i = 1; i < styles.length; i++) {
				styleSheet.addRule( styles[i] );
			}
		}
		
		// -- FINISH PANE
		
		// create a document from the now-styled kit and set it as template
		pane.setDocument( kit.createDefaultDocument() );
	}
	
	/**
	 * Method adds a HyperlinkListener to the specified Object.
	 *
	 * This way a user can browse to external links (e.g. http://www.sinds1984.nl/) in their default browser
	 * or continue to browser an internal documentation system without the current pane.
	 *
	 * @param pane Instance of a <code>JEditorPane</code> for which the HyperlinkListener will be installed.
	 * @to.do Does not resolve internal links, opens everything in the default browser.
	 */
	public static void addLinkListener(JEditorPane pane) {
		pane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent hev) {
				if (hev.getEventType() == EventType.ACTIVATED) {
					
					if (Desktop.isDesktopSupported() ) {
					 
						Desktop desktop = Desktop.getDesktop();
					 
						if ( desktop.isSupported( Desktop.Action.BROWSE ) ) {
						 
							try {
								URI uri = new URI( hev.getURL().toString() );
								desktop.browse( uri );
							}
							catch (Exception e) {
								System.out.println( e.getMessage() );
							}	
						}
					}
				}
			}
		});
	}
}