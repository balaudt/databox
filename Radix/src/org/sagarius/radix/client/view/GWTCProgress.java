/*
 * Copyright 2007 Manuel Carrasco Moñino. (manuel_carrasco at users.sourceforge.net) 
 * http://code.google.com/p/gwtchismes
 * Most part of this code has been borrowed to Bjarne Matzen, Bjarne[dot]Matzen[at]gmail[dot]com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sagarius.radix.client.view;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * <p>
 * <b>A progress bar with time remaining calculation.</b>
 * <p>
 * 
 * @author Bjarne Matzen & Manuel Carrasco Moñino
 * 
 *         <h3>Features</h3>
 *         <ul>
 *         <li>show it as a modal dialog</li> <li>display some text above or at
 *         the left of the progress bar</li> <li>time remaining, percent,
 *         done/total, and velocity</li> <li>internationalizable elements</li>
 *         </ul>
 * 
 *         <h3>Example</h3>
 * 
 *         <pre>
 * final GWTCProgress progressBar = new GWTCProgress(20,
 * 		GWTCProgress.SHOW_TIME_REMAINING | GWTCProgress.SHOW_TEXT);
 * progressBar.setText(&quot;Doing something...&quot;);
 * RootPanel.get().add(progressBar);
 * 
 * Timer t = new Timer() {
 * 	public void run() {
 * 		int progress = progressBar.getProgress() + 4;
 * 		if (progress &gt; 100)
 * 			cancel();
 * 		progressBar.setProgress(progress);
 * 	}
 * };
 * t.scheduleRepeating(500);
 * </pre>
 * 
 *         <h3>Configuration options</h3>
 *         <ul>
 *         <li>SHOW_TIME_REMAINING show the calculated time remaining. It is
 *         recalculated each time the progress bar is updated</li> <li>SHOW_TEXT
 *         show element with text description</li> <li>SHOW_LEFT_TEXT show text
 *         at left position</li> <li>SHOW_NUMBERS show element with numerical
 *         data</li> <li>SHOW_AS_DIALOG shos as a dialog</li>
 *         </ul>
 * 
 *         <h3>CSS Style Rules</h3>
 *         <ul>
 *         <li>.GWTCProgress { GWTCDatePicket container }</li> <li>
 *         .GWTCProgress-dialog { dependent style when the progress bar is shown
 *         as dialog }</li> <li>.GWTCProgress .prg-numbers { element with
 *         numeric data }</li> <li>.GWTCProgress .prg-time { element with time
 *         remaining data }</li> <li>.GWTCProgress .prg-title { element with
 *         title }</li> <li>.GWTCProgress .prg-bar-outer { outer part of the
 *         progress bar} <li>.GWTCProgress .prg-bar-inner { inner part of the
 *         progress bar } <li>.GWTCProgress .prg-bar-inner .prg-bar-element {
 *         each element in the progress bar} <li>.GWTCProgress .prg-bar-inner
 *         .prg-bar-done { elements that represents done precent in the progress
 *         bar } <li>.GWTCProgress .prg-bar-inner .prg-bar-blank { remainder
 *         elements }
 *         <ul>
 * 
 */

public class GWTCProgress extends Composite {

	public static final String StyleCProgress = "GWTCProgress";
	public static final String StyleCProgressDlg = "dialog";
	public static final String StyleCPrgNumbers = "prg-numbers";
	public static final String StyleCPrgTime = "prg-time";
	public static final String StyleCPrgText = "prg-title";

	public static final String StyleCBarOuter = "prg-bar-outer";
	public static final String StyleCBarInner = "prg-bar-inner";

	public static final String StyleCBarDone = "prg-bar-done";
	public static final String StyleCBarBlank = "prg-bar-blank";
	public static final String StyleCBarElement = "prg-bar-element";

	public static final int SHOW_TIME_REMAINING = 1;
	public static final int SHOW_TEXT = 2;
	public static final int SHOW_LEFT_TEXT = 16;
	public static final int SHOW_NUMBERS = 4;
	public static final int SHOW_AS_DIALOG = 8;
	public static final int SHOW_DEFAULT = SHOW_AS_DIALOG | SHOW_NUMBERS
			| SHOW_TEXT | SHOW_TIME_REMAINING;

	private DialogBox progressDlg = null;
	private FlexTable contentTable = new FlexTable();
	private Grid elementGrid = null;
	private Label remainLabel = new Label();
	private Label textLabel = new Label();
	private Label numberLabel = new Label();

	private long startTime = System.currentTimeMillis();
	private int elements = 20;
	private int progress = 0;

	private String secondsMessage = "Time remaining: {0} Seconds";
	private String minutesMessage = "Time remaining: {0} Minutes";
	private String hoursMessage = "Time remaining: {0} Hours";
	private String percentMessage = "{0}%";
	private String totalMessage = "{0}% {1}/{2} ";

	private boolean showRemaining = false;
	private boolean showText = false;
	private boolean showLeftText = false;
	private boolean showNumbers = false;
	private boolean showAsDialog = false;

	protected GWTCProgress() {
	}

	/**
	 * Base constructor for this widget
	 * 
	 * @param elements
	 *            The number of elements (bars) to show on the progress bar
	 * @param options
	 *            The display options for the progress bar
	 */
	public GWTCProgress(int elements, int options) {
		initialize(options, elements);
	}

	/**
	 * Constructor without options
	 * 
	 * @param elements
	 *            The number of elements (bars) to show on the progress bar
	 */
	public GWTCProgress(int elements) {
		this(elements, 0);
	}

	public void initialize(int options, int elements) {
		// Read the options and set variables
		if ((options & SHOW_TIME_REMAINING) == SHOW_TIME_REMAINING)
			showRemaining = true;
		if ((options & SHOW_TEXT) == SHOW_TEXT)
			showText = true;
		if ((options & SHOW_NUMBERS) == SHOW_NUMBERS)
			showNumbers = true;
		if ((options & SHOW_AS_DIALOG) == SHOW_AS_DIALOG)
			showAsDialog = true;
		if ((options & SHOW_LEFT_TEXT) == SHOW_LEFT_TEXT)
			showText = showLeftText = true;

		// Set element count
		this.elements = elements;

		// Styling
		contentTable.setStyleName(StyleCProgress);
		numberLabel.setStyleName(StyleCPrgNumbers);
		remainLabel.setStyleName(StyleCPrgTime);
		textLabel.setStyleName(StyleCPrgText);

		// Create the out container
		Grid containerElementGrid = new Grid(1, 1);
		containerElementGrid.setStyleName(StyleCBarOuter);
		containerElementGrid.setCellPadding(0);
		containerElementGrid.setCellSpacing(0);
		// Create container for elements
		elementGrid = new Grid(1, elements);
		elementGrid.setStyleName(StyleCBarInner);
		elementGrid.setCellPadding(0);
		elementGrid.setCellSpacing(0);
		containerElementGrid.setWidget(0, 0, elementGrid);
		// Create elements
		for (int loop = 0; loop < elements; loop++) {
			Grid elm = new Grid(1, 1);
			elm.setHTML(0, 0, "");
			elm.setStyleName(StyleCBarDone);
			elm.addStyleName(StyleCBarElement);
			elementGrid.setWidget(0, loop, elm);
		}

		// Set up the surrounding flex table based on the options
		int row = 0;
		int col = 0;
		if (showLeftText)
			contentTable.setWidget(row, col++, textLabel);
		else if (showText)
			contentTable.setWidget(row++, col, textLabel);
		if (showNumbers)
			contentTable.setWidget(row, col + 1, numberLabel);
		contentTable.setWidget(row++, col, containerElementGrid);
		contentTable.setWidget(row++, col, remainLabel);

		// Initialize progress bar
		setProgress(0);
		if (showAsDialog) {
			// Create the background
			// put the container into a dialog
			progressDlg = new DialogBox();
			progressDlg.setWidget(contentTable);
			progressDlg.setStyleName(StyleCProgress);
			progressDlg.addStyleDependentName(StyleCProgressDlg);
			progressDlg.center();
			hide();
			// Initialize this composite with an empty element
			initWidget(new SimplePanel());
		} else {
			initWidget(contentTable);
		}
	}

	public void hide() {
		contentTable.setVisible(false);
		if (!showAsDialog)
			return;
		progressDlg.hide();
	}

	public void show() {
		contentTable.setVisible(true);
		if (!showAsDialog)
			return;
		progressDlg.center();
	}

	public void setProgress(int percent) {
		setProgress(percent, 0, 0);
	}

	public void setProgress(int done, int total) {
		int percent = total > 0 ? done * 100 / total : 0;
		setProgress(percent, done, total);
	}

	/**
	 * Set the current progress as a percentage
	 * 
	 * @param percentage
	 *            Set current percentage for the progress bar
	 */
	public void setProgress(int percentage, int done, int total) {
		percentage = Math.min(Math.max(percentage, 0), 100);
		progress = percentage;

		int completed = elements * percentage / 100;
		for (int loop = 0; loop < elements; loop++) {
			Grid elm = (Grid) elementGrid.getWidget(0, loop);
			if (loop < completed) {
				elm.setStyleName(StyleCBarDone);
				elm.addStyleName(StyleCBarElement);
			} else {
				elm.setStyleName(StyleCBarBlank);
				elm.addStyleName(StyleCBarElement);
			}
		}
		DOM.setInnerHTML(remainLabel.getElement(), "&nbsp;");
		DOM.setInnerHTML(numberLabel.getElement(), "&nbsp;");

		long soFar = System.currentTimeMillis() - startTime;
		if (percentage > 0) {
			if (showRemaining) {
				long remaining = (soFar * (100 - percentage) / percentage) / 1000;
				String remainText = secondsMessage;
				if (remaining > 120) {
					remaining = remaining / 60;
					remainText = minutesMessage;
					if (remaining > 120) {
						remaining = remaining / 60;
						remainText = hoursMessage;
					}
				}
				remainLabel.setText(GWTCHelper.internationalize(remainText, ""
						+ remaining));
			}
		} else {
			// If progress is 0, reset the start time
			startTime = System.currentTimeMillis();
		}
		if (showNumbers) {
			String message = (total > 0) ? totalMessage : percentMessage;
			long velocity = soFar > 0 ? ((done * 1000) / soFar) : 0;
			Object[] os = { "" + percentage, "" + done, "" + total,
					"" + velocity };
			numberLabel.setText(GWTCHelper.internationalize(message, os));
		}
	}

	/**
	 * Get the current progress as a percentage
	 * 
	 * @return Current percentage for the progress bar
	 */
	public int getProgress() {
		return (progress);
	}

	/**
	 * Get the text displayed above the progress bar
	 * 
	 * @return the text
	 */
	public String getText() {
		return this.textLabel.getText();
	}

	/**
	 * Set the text displayed above the progress bar
	 * 
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.textLabel.setText(text);
	}

	/**
	 * Get the message used to format the time remaining text for hours
	 * 
	 * @return the hours message
	 */
	public String getHoursMessage() {
		return hoursMessage;
	}

	/**
	 * Set the message used to format the time remaining text below the progress
	 * bar. There are 3 messages used for hours, minutes and seconds
	 * respectively.
	 * 
	 * The message must contain a placeholder for the value. The placeholder
	 * must be {0}. For example, the following is a valid message:
	 * 
	 * "Hours remaining: {0}"
	 * 
	 * @param hoursMessage
	 *            the hours message to set
	 */
	public void setHoursMessage(String hoursMessage) {
		this.hoursMessage = hoursMessage;
	}

	/**
	 * Get the message used to format the time remaining text for minutes
	 * 
	 * @return the minutesMessage
	 */
	public String getMinutesMessage() {
		return minutesMessage;
	}

	/**
	 * Set the message used to format the time remaining text below the progress
	 * bar. There are 3 messages used for hours, minutes and seconds
	 * respectively.
	 * 
	 * The message must contain a placeholder for the value. The placeholder
	 * must be {0}. For example, the following is a valid message:
	 * 
	 * "Minutes remaining: {0}"
	 * 
	 * @param minutesMessage
	 *            the minutes message to set
	 */
	public void setMinutesMessage(String minutesMessage) {
		this.minutesMessage = minutesMessage;
	}

	/**
	 * Get the message used to format the time remaining text for seconds
	 * 
	 * @return the secondsMessage
	 */
	public String getSecondsMessage() {
		return secondsMessage;
	}

	/**
	 * Set the message used to format the time remaining text below the progress
	 * bar. There are 3 messages used for hours, minutes and seconds
	 * respectively.
	 * 
	 * The message must contain a placeholder for the value. The placeholder
	 * must be {0}. For example, the following is a valid message:
	 * 
	 * "Seconds remaining: {0}"
	 * 
	 * @param secondsMessage
	 *            the secondsMessage to set
	 */
	public void setSecondsMessage(String secondsMessage) {
		this.secondsMessage = secondsMessage;
	}

	public String getPercentMessage() {
		return percentMessage;
	}

	/**
	 * Set the message used to format the progress in percent units.
	 * 
	 * The message must contain a placeholder for the value. The placeholder
	 * must be {0}.
	 * 
	 * @param percentMessage
	 *            the percent message to set
	 */
	public void setPercentMessage(String percentMessage) {
		this.percentMessage = percentMessage;
	}

	public String getTotalMessage() {
		return totalMessage;
	}

	/**
	 * Set the message to show when the process has finished
	 * 
	 * @param totalMessage
	 */
	public void setTotalMessage(String totalMessage) {
		this.totalMessage = totalMessage;
	}

}
