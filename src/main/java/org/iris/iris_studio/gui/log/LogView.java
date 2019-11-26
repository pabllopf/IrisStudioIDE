package org.iris.iris_studio.gui.log;

import org.iris.iris_studio.gui.View;

import javafx.scene.control.TextArea;

public class LogView extends View {
	
	private final TextArea logArea;
	
	public LogView() {
		super("Log");
		
		logArea = new TextArea();
		logArea.setEditable(false);
		
		setContent(logArea);
	}

	public void UpdateLog(String message) {
		logArea.appendText("\n" + message);
	}

    public void AppendToLog(String message) {
        logArea.appendText(message);
    }

}
