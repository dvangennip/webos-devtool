package webosdevtool.jslint;

import com.explodingpixels.macwidgets.SourceListItem;

/**
 * SourceListItem extension class
 */
public class LintSourceListItem extends SourceListItem {
	
	// Variables
	
	private String source;
	private String report;
	
	// Constructors
	
	public LintSourceListItem (String name, String path) {
		this(name, path, null);
	}
	
	public LintSourceListItem (String name, String path, String report) {
		super(name);
		this.source = path;
		this.report = report;
	}
	
	// Methods
	
	/**
	 * @return String of the source file path
	 */
	protected String getSource() {
		return source;
	}
	
	/**
	 * @param path String of the source file path
	 */
	protected void setSource(String path) {
		source = path;
	}
	
	/**
	 * @return The actual JSLint report as HTML String
	 */
	protected String getReport() {
		return report;
	}
	
	/**
	 * @param r The actual JSLint report as HTML String
	 */
	protected void setReport(String r) {
		report = r;
	}
}