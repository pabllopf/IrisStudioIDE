package org.iris.iris_studio.gui.log;

import org.iris.iris_studio.gui.View;

import javafx.scene.control.ListView;

public class ErrorsView extends View {
	
	private final ListView<String> errorList;
	
	public ErrorsView() {
		super("Errors");
		
		errorList = new ListView<>();
		
		setContent(errorList);
	}

}
