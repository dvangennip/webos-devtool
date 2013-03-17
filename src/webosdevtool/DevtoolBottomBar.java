/**
 * Adaptation of BottomBar
 */
package webosdevtool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JButton;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.MacIcons;

/**
 * webOSdevtool bottom bar with activity label in the center
 *
 * @author rayvanderborght / adapted by dvg
 */
public class DevtoolBottomBar extends BottomBar
{
    // Variables
    
    private Devtool devtool = null;
    private final JLabel activityIndicator = MacWidgetFactory.createEmphasizedLabel("Ready");
    private JButton addButton = new JButton(MacIcons.PLUS);
	private JButton removeButton = new JButton(MacIcons.MINUS);
    private JButton refreshButton;

    // Constructor
    
    public DevtoolBottomBar(BottomBarSize size, Devtool myParent)
    {
    	// assign reference to main class and call super constructor
    	super(size);
    	devtool = myParent;
        
        // add button
        addButton.setToolTipText("Open a project (\u2318O)");
        addButton.putClientProperty("JButton.buttonType", "textured");
        addButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectOpen(null);
			}
	    });
	    
	    // remove button
	    removeButton.setToolTipText("Close selected project (\u2318W)");
       	removeButton.putClientProperty("JButton.buttonType", "textured");
       	removeButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectClose();
			}
	    });

        // refresh button
        ImageIcon refreshIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-small-refresh.png") );
        refreshButton = new JButton(refreshIcon);
        refreshButton.setToolTipText("Refresh everything (F5)");
        refreshButton.putClientProperty("JButton.buttonType", "textured");
        refreshButton.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                devtool.refreshAll();
            }
        });
        
        // add to bottombar
        this.addComponentToLeft(addButton);
        this.addComponentToLeft(removeButton);
        this.addComponentToCenter(activityIndicator);
        this.addComponentToRight(refreshButton);
    }
    
    // Methods
    
    /** */
    public void setActivityIndicator(int numberOfTasks) {
        
        String text = "Ready";
        
        if (numberOfTasks == 1) {
        	text = "Processing 1 task...";
        } else if (numberOfTasks > 1) {
        	text = "Processing " + numberOfTasks + " tasks...";
        }
        
        this.activityIndicator.setText(text);
    }
    
    /**
     * Takes care of setting up the menu correctly for focus on project
     * while disabling non-sensible menu items.
     */
    protected void setDeviceView() {
    	// only close single (current) project makes no sense
    	removeButton.setEnabled(false);
    }
    
    /**
     * Takes care of setting up the menu correctly for focus on project
     */
    protected void setProjectView() {
    	// enable everything that might be disabled
    	removeButton.setEnabled(true);
    }
}