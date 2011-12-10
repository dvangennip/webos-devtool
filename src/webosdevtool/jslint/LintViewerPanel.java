package webosdevtool.jslint;

//import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

public class LintViewerPanel extends JScrollPane {
	
	// Variables
	
	private JEditorPane contentPane;
	
	// Constructor
	
	public LintViewerPanel () {
		// default
		super();
		
		// Init text pane
		contentPane = new JEditorPane();
		contentPane.setEditable(false);
		contentPane.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		
		// Init content type
		contentPane.setContentType("text/html; charset=UTF-8");
		
		// get the pane styled
		LintOutputStyler.applyHTMLStyle( contentPane );
		
		// Put the editor pane in this scroll pane.
		this.setViewportView(contentPane);
		
		// Adjust pane appearance
		IAppWidgetFactory.makeIAppScrollPane(this);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setPreferredSize(new Dimension(650, 350));
		this.setMinimumSize(new Dimension(300, 200));
		
		// Init content
		setDefaultContent();
	}
	
	// Methods
	
	/**
	 * @param content HTML text as <code>String</code>
	 */
	protected void setContent(String content) {
		
		// put in the HTML text
		contentPane.setText(content);
		
		// make scrollpane focus on the top, not the bottom after setting content
		// invokeLater method fires after all events have returned
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
		   public void run() { 
		      getVerticalScrollBar().setValue(0);
		   }
		});
	}
	
	/**
	 * @param text Normal text as <code>String</code> which will get HTML'ised and set
	 */
	protected void setPlainTextContent(String text) {
		setContent("<html><head></head><body>" + text + "</body></html>");
	}
	
	protected void setDefaultContent() {
		setPlainTextContent("Please wait while analysis is in progress...");
	}
}