package org.iris.iris_studio.gui;

import org.iris.iris_studio.IrisStudio;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BottomPane extends HBox {
	
	private final Label workspaceLabel;
	private final MemoryInfo memoryInfo;
	
	public BottomPane() {
		setSpacing(720);
		
		workspaceLabel = new Label("Workspace: " + IrisStudio.getWorkspace().getFileSystem().getRoot().toString());
		memoryInfo = new MemoryInfo();
		
		getChildren().addAll(workspaceLabel, memoryInfo);
	}

	public Label getWorkspaceLabel() {
		return workspaceLabel;
	}

	public MemoryInfo getMemoryInfo() {
		return memoryInfo;
	}

}
