package webosdevtool.jslint;

// Import packages

import webosdevtool.DevtoolHTMLHelper;

import javax.swing.JEditorPane;

/**
 * LintOutputStyler is given a JSlint report HTML String which it parses and returns with
 * changes made and additions done to the HTML output. Together with a HTMLKit method for CSS like styling
 * this class helps to style a JSLint report in a more reader friendly format.<br />
 * There is no need for initialising this class as its method are all of the static type.
 * Any changes desired to the generated output should be done in this class's source code,
 * so in short you can have any style as long as it is the default one.<br />
 * NOTE: This styler's method are based on output from <a href="http://code.google.com/p/jslint4java/">jslint4java</a> by
 * <a href="http://happygiraffe.net/blog/tag/jslint4java/">Dominic Mitchell</a> and of course the original
 * JSLint by Douglas Crockford on <a href="http://jslint.com/">jslint.com</a>.
 */
public class LintOutputStyler {

	/**
	 * All methods are static so there is no need to construct a LintOutputStyler instance.
	 */
	public LintOutputStyler() {}
	
	// Methods
	
	/**
	 * This method styles a JSLint report and returns it. Together with style rules the
	 * actual HTML can be more readable.<br />
	 * It does the following things:
	 * <ul>
	 * <li>Clearify the sections (Errors, Functions, and Properties).</li>
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Add correct indenting for code evidence.</li>
 	 * <li>Clearify <code>code</code> pieces as being <code>code</code> and not report comments.</li>
 	 * <li>Wraps each error in a separate DIV so these can styled to look indeed separate.</li>
	 * </ul>
	 *
	 * @param unstyledReport Raw report as it comes from the jslint4java report generator.
	 * @return Styled HTML report as String.
	 */
	public static String styleReport(String unstyledReport) {
		
		String report = unstyledReport;
		
		// make it proper HTML
		report = "<html><head><title>jslint</title></head><body>" + report + "</body></html>";
		
		// clearify section headers
		report = report.replaceFirst("<i>Error:</i>", "<h2>Errors:</h2>");
		report = report.replaceFirst("<div id=functions>", "<div id=\"functions\"><h2>Functions:</h2>");
		report = report.replaceFirst("<br><pre id=properties>", "<br><h2>Properties:</h2><pre id=\"properties\">");
		
		// fix indentation
		// replace series of white space chars with a fixed number of non-break spaces
		report = report.replaceAll("\\s{3,}", "&nbsp;&nbsp;&nbsp;");
		
		// clearify code among text
		// uses .+? for reluctant matching (as little as possible per match)
		report = report.replaceAll("Expected '(.+?)' and instead saw '(.+?)'\\.", "Expected <code>$1</code> and instead saw <code>$2</code>");
		report = report.replaceAll("Expected an identifier and instead saw '(.+?)'\\.", "Expected an identifier and instead saw <code>$1</code>");
		report = report.replaceAll("'(.+?)' is already defined", "<code>$1</code> is already defined");
		report = report.replaceAll("Unexpected '(.+?)'\\.", "Unexpected <code>$1</code>");
		
		// wrap errors in separate <div> elements
		report = report.replaceAll("(<p>Problem){1}.+?(</p>){1}.+?(</p>){1}", "<div class=\"error\">$0</div>");
		report = report.replaceAll("(<p><i>Implied global:</i>){1}(.+?(</p>){1})", "<div class=\"warning\">$1<br />$2</div>");
		report = report.replaceAll("(<p><i>Unused variable:</i>){1}(.+?(</p>){1})", "<div class=\"warning\">$1<br />$2</div>");
		
		//System.out.println("\n\n*****" + report + "***");
		return report;
	}
	
	/**
	 * @param pane HTMLEditorKit of a JEditorPane for which the stylesheet will have CSS rules added.
	 * @see webosdevtool.DevtoolHTMLHelper
	 */
	public static void applyHTMLStyle(JEditorPane pane) {
		DevtoolHTMLHelper.applyHTMLStyle(pane, "/webosdevtool/images/jslint.css");
	}
}