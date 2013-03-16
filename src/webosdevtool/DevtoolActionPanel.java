package webosdevtool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.explodingpixels.macwidgets.MacWidgetFactory;
 
/**
 * Class defines the actionable panel on which the GUI is laid out
 * to act on device or project options, e.g. the right pane with per-item info.
 */
public class DevtoolActionPanel extends JPanel {
 	
 	// Variables
 	
 	private Devtool devtool;
 	
 	private JPanel deviceInfoPanel;
 	private JPanel deviceButtonPanel;
 	private JPanel projectInfoPanel;
 	private JPanel projectButtonPanel;
 	
 	private JLabel deviceIconLabel;
 	private JLabel deviceNameLabel;
 	private JLabel deviceIDLabel;
 	private JLabel deviceVersionLabel;
 	private JLabel deviceLocationLabel;
 	
 	private JLabel projectIconLabel;
 	private JLabel projectNameLabel;
 	private JLabel projectAppIDLabel;
 	private JLabel projectVersionLabel;
 	
 	private JButton deviceLookupButton;
 	private JButton deviceResourceMonitorButton;
 	private JButton deviceSetDefaultButton;
 	private JButton deviceStartButton;
 	
 	private JButton projectLookupButton;
 	private JButton projectJSLintButton;
 	private JButton projectPackageButton;
 	private JButton projectInstallButton;
 	private JButton projectLaunchButton;
 	private JButton projectDeployButton;
 	private JButton projectUninstallButton;
 	private JButton projectRunButton;
 	private JButton projectOpenPalmLogButton;
 	
 	// Constructor
 	public DevtoolActionPanel (Devtool myParent) {
 		
 		// call super class constructor
 		super(new BorderLayout());
 		
 		// reference to main class
 		this.devtool = myParent;
 		
 		// pane colours
 		Color devicePaneColour = new Color(0.75f, 0.75f, 0.75f);
 		Color projectPaneColour = new Color(0.72f, 0.73f, 0.77f);
 		
 	  // device info panel -------------------------------------
 	  
 	  	// labels:
 	  	// ⌘ \u2318 apple / cmd key
 	  	// ⌥ \u2325 alt / option key
 	  	// ⇧ \u21E7 shift key
 	  	// ⌃ \u2303 ctrl key
 		
 		// info panel
 		deviceInfoPanel = new JPanel(new BorderLayout());
		deviceInfoPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		deviceInfoPanel.setBackground(devicePaneColour);
		
		// create labels
		Icon deviceIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/icon-default-64x64.png") );
		deviceIconLabel = new JLabel(deviceIcon);
		deviceIconLabel.setBorder(BorderFactory.createEmptyBorder(0,10,10,20));
		deviceNameLabel = new JLabel("");
		deviceNameLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
		deviceNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		deviceIDLabel = new JLabel("ID.  ");
		deviceIDLabel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		deviceVersionLabel = new JLabel(" V.  ");
		deviceVersionLabel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		deviceLocationLabel = new JLabel(" @.  ");
		deviceLocationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		// left info panel meant for icon
		JPanel deviceInfoLeftPanel = new JPanel();
		deviceInfoLeftPanel.setLayout(new BoxLayout(deviceInfoLeftPanel, BoxLayout.Y_AXIS));
		deviceInfoLeftPanel.setBackground(devicePaneColour);
		deviceInfoLeftPanel.add(deviceIconLabel);
		// info panel has labels on the right in a separate pane
		JPanel deviceInfoRightPanel = new JPanel();
		deviceInfoRightPanel.setLayout(new BoxLayout(deviceInfoRightPanel, BoxLayout.Y_AXIS));
		deviceInfoRightPanel.setBackground(devicePaneColour);
		deviceInfoRightPanel.add(deviceNameLabel);
		deviceInfoRightPanel.add(deviceIDLabel);
		deviceInfoRightPanel.add(deviceVersionLabel);
		deviceInfoRightPanel.add(deviceLocationLabel);
		
		deviceInfoPanel.add(deviceInfoLeftPanel, BorderLayout.WEST);
		deviceInfoPanel.add(deviceInfoRightPanel, BorderLayout.CENTER);
		
		// button panel goes horizontal - left to right
 		deviceButtonPanel = new JPanel();
 		deviceButtonPanel.setLayout(new BoxLayout(deviceButtonPanel, BoxLayout.X_AXIS));
 		deviceButtonPanel.setPreferredSize(new Dimension(450,50));
		deviceButtonPanel.setMaximumSize(new Dimension(1000,50)); // large in x direction
		deviceButtonPanel.setMinimumSize(new Dimension(400,50));
		deviceButtonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		// device buttons
		
		// set as default button
		Icon dSetDefaultIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-device-selected-false.png") );
		deviceSetDefaultButton = new JButton(dSetDefaultIcon);
		deviceSetDefaultButton.setToolTipText("Set this device as default for installing, et cetera.");
		deviceSetDefaultButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				setDeviceAsDefault(true);
				devtool.setCurrentDevice();
			}
	    });
		deviceButtonPanel.add(deviceSetDefaultButton);
		
		// add spacer
		deviceButtonPanel.add( MacWidgetFactory.createSpacer(20,0) );
		
		// lookup button
		Icon dlookupIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-lookup.png") );
		deviceLookupButton = new JButton(dlookupIcon);
		deviceLookupButton.setToolTipText("Reveal device location in Finder");
		deviceLookupButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				//devtool.deviceRevealInFinder("currentitem");
				devtool.deviceRevealInFinder("currentitem");
			}
	    });
		deviceButtonPanel.add(deviceLookupButton);
		
		// open resource monitor button
		Icon dResourceMonitorIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-resource-monitor.png") );
		deviceResourceMonitorButton = new JButton(dResourceMonitorIcon);
		deviceResourceMonitorButton.setToolTipText("Open Resource Monitor");
		deviceResourceMonitorButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.openResourceMonitor("currentitem");
			}
	    });
		deviceButtonPanel.add(deviceResourceMonitorButton);
		
		// add spacer
		deviceButtonPanel.add( MacWidgetFactory.createSpacer(20,0) );
		
		// start button
		Icon dStartIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-emulator-start.png") );
		deviceStartButton = new JButton(dStartIcon);
		deviceStartButton.setToolTipText("Start device");
		deviceStartButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.deviceStart("currentitem");
			}
	    });
		deviceButtonPanel.add(deviceStartButton);
 		
 	  // project info panel -------------------------------------
 		 		
 		// info panel
 		projectInfoPanel = new JPanel(new BorderLayout());
		projectInfoPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		projectInfoPanel.setBackground(projectPaneColour);
		
		// create labels
		Icon projectIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/icon-default-64x64.png") );
		projectIconLabel = new JLabel(projectIcon);
		projectIconLabel.setBorder(BorderFactory.createEmptyBorder(0,10,10,20));
		projectNameLabel = new JLabel("");
		projectNameLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
		projectNameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		projectAppIDLabel = new JLabel("ID.  ");
		projectAppIDLabel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		projectVersionLabel = new JLabel(" V.  ");
		projectVersionLabel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		// left info panel meant for icon
		JPanel projectInfoLeftPanel = new JPanel();
		projectInfoLeftPanel.setLayout(new BoxLayout(projectInfoLeftPanel, BoxLayout.Y_AXIS));
		projectInfoLeftPanel.setBackground(projectPaneColour);
		projectInfoLeftPanel.add(projectIconLabel);
		// info panel has labels on the right in a separate pane
		JPanel projectInfoRightPanel = new JPanel();
		projectInfoRightPanel.setLayout(new BoxLayout(projectInfoRightPanel, BoxLayout.Y_AXIS));
		projectInfoRightPanel.setBackground(projectPaneColour);
		projectInfoRightPanel.add(projectNameLabel);
		projectInfoRightPanel.add(projectAppIDLabel);
		projectInfoRightPanel.add(projectVersionLabel);
		
		projectInfoPanel.add(projectInfoLeftPanel, BorderLayout.WEST);
		projectInfoPanel.add(projectInfoRightPanel, BorderLayout.CENTER);
		
		// project button panel
 		
 		// button panel goes horizontal - left to right
 		projectButtonPanel = new JPanel();
 		projectButtonPanel.setLayout(new BoxLayout(projectButtonPanel, BoxLayout.X_AXIS));
 		projectButtonPanel.setPreferredSize(new Dimension(450,50));
		projectButtonPanel.setMaximumSize(new Dimension(1000,50)); // large in x direction
		projectButtonPanel.setMinimumSize(new Dimension(400,50));
		projectButtonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		// project buttons
		
		// lookup button
		Icon lookupIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-lookup.png") );
		projectLookupButton = new JButton(lookupIcon);
		projectLookupButton.setToolTipText("Reveal project folder in Finder (\u2318F)");
		projectLookupButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectRevealInFinder();
			}
	    });
		projectButtonPanel.add(projectLookupButton);
		
		// add spacer
		projectButtonPanel.add( MacWidgetFactory.createSpacer(20,0) );
		
		// JSLint button
		Icon jsLintIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-jslint.png") );
		projectJSLintButton = new JButton(jsLintIcon);
		projectJSLintButton.setToolTipText("Scan with JS Lint (\u2318J)");
		projectJSLintButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectJSLint();
			}
	    });
		projectButtonPanel.add(projectJSLintButton);
		
		// add spacer
		projectButtonPanel.add( MacWidgetFactory.createSpacer(20,0) );

		// deploy button
	    Icon deployIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-deploy.png") );
		projectDeployButton = new JButton(deployIcon);
		projectDeployButton.setToolTipText("Deploy Enyo2 project (\u23180)");
        projectDeployButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectDeploy();
			}
	    });
	    projectButtonPanel.add(projectDeployButton);
		
		// package button
		Icon packageIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-package.png") );
		projectPackageButton = new JButton(packageIcon);
		projectPackageButton.setToolTipText("Package project (\u23181)");
        projectPackageButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectPackage();
			}
	    });
	    projectButtonPanel.add(projectPackageButton);
	    
	    // install button
	    Icon installIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-install.png") );
		projectInstallButton = new JButton(installIcon);
		projectInstallButton.setToolTipText("Install project on device (\u23182)");
        projectInstallButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectInstall();
			}
	    });
	    projectButtonPanel.add(projectInstallButton);
	    
	    // launch button
	    Icon launchIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-launch.png") );
		projectLaunchButton = new JButton(launchIcon);
		projectLaunchButton.setToolTipText("Launch project on device (\u23183)");
        projectLaunchButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectLaunch();
			}
	    });
	    projectButtonPanel.add(projectLaunchButton);
	    
	    // uninstall button
	    Icon uninstallIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-uninstall.png") );
		projectUninstallButton = new JButton(uninstallIcon);
		projectUninstallButton.setToolTipText("Uninstall project from device (\u23186)");
        projectUninstallButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectUninstall();
			}
	    });
	    projectButtonPanel.add(projectUninstallButton);
	    
	    // add spacer
		projectButtonPanel.add( MacWidgetFactory.createSpacer(20,0) );
	    
	    // run button
	    Icon runIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-run.png") );
		projectRunButton = new JButton(runIcon);
		projectRunButton.setToolTipText("Package, install and run project on device (\u2318R)");
        projectRunButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectRun();
			}
	    });
	    projectButtonPanel.add(projectRunButton);
	    
	    // add spacer
		projectButtonPanel.add( MacWidgetFactory.createSpacer(20,0) );
	    
	    // open palm-log button
	    Icon openPalmLogIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-palm-log.png") );
		projectOpenPalmLogButton = new JButton(openPalmLogIcon);
		projectOpenPalmLogButton.setToolTipText("Open palm-log window to follow app on device (\u2318L)");
        projectOpenPalmLogButton.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				devtool.projectOpenPalmLog();
			}
	    });
	    projectButtonPanel.add(projectOpenPalmLogButton);
	    
 		// add panels to main pane
 		this.add(deviceInfoPanel, BorderLayout.CENTER);
 		this.add(deviceButtonPanel, BorderLayout.SOUTH);
 	}
 	
 	// Methods -----------------------------------------------
 	
 	protected void switchToProjectView() {
 		// replace panels in main pane
 		this.removeAll();
 		this.add(projectInfoPanel, BorderLayout.CENTER);
 		this.add(projectButtonPanel, BorderLayout.SOUTH);
 		this.validate();
 		this.repaint();
 	}
 	
 	protected void switchToDeviceView() {
 		// replace panels in main pane
 		this.removeAll();
 		this.add(deviceInfoPanel, BorderLayout.CENTER);
 		this.add(deviceButtonPanel, BorderLayout.SOUTH);
 		this.validate();
 		this.repaint();
 	}
 	
 	protected void setDeviceAsDefault(boolean d) {
 		// if this device is selected as default
 		// update button icon accordingly
 		Icon dSetDefaultIcon;
 		if (d) {
 			dSetDefaultIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-device-selected-true.png") );
 		} else {
 			dSetDefaultIcon = new ImageIcon( getClass().getResource("/webosdevtool/images/button-device-selected-false.png") );
 		}
 		deviceSetDefaultButton.setIcon(dSetDefaultIcon);
 	}
 	
 	/**
 	 * Method adds or removes emulator specific actionable items
 	 */
 	protected void setDeviceAsEmulator(boolean emulator) {
 		
 		// just for safety set enabled status appropriately
 		deviceStartButton.setEnabled(emulator);
 		
 		if (emulator) {
 			// add launch button
 			deviceButtonPanel.add( deviceStartButton );
 		} else {
 			// remove launch button
 			deviceButtonPanel.remove( deviceStartButton );
 		}
 	}
 	
 	protected void setDeviceName(String name) {
 		deviceNameLabel.setText(name);
 	}
 	
 	protected void setDeviceID(String deviceID) {
 		if (deviceID == null) {
 			deviceID = "?";
 		}
 		String newID = "ID.  " + deviceID;
 		deviceIDLabel.setText(newID);
 	}
 	
 	protected void setDeviceVersion(String version) {
 		if (version == null) {
 			version = "?";
 		}
 		String newVersion = " V.  " + version;
 		deviceVersionLabel.setText(newVersion);
 	}
 	
 	protected void setDeviceLocation(String location) {
 		if (location == null) {
 			location = "?";
 		}
 		String newLocation = " @.  " + location;
 		deviceLocationLabel.setText(newLocation);
 	}
 	
 	protected void setDeviceIcon(ImageIcon icon) {
 		deviceIconLabel.setIcon( icon );
 	}
 	
 	protected void setProjectName(String name) {
 		projectNameLabel.setText(name);
 	}
 	
 	protected void setProjectAppID(String appID) {
 		String newID = "ID.  " + appID;
 		projectAppIDLabel.setText(newID);
 	}
 	
 	protected void setProjectVersion(String version) {
 		String newVersion = " V.  " + version;
 		projectVersionLabel.setText(newVersion);
 	}
 	
 	protected void setProjectIcon(ImageIcon icon) {
 		projectIconLabel.setIcon( icon );
 	}
 	
}