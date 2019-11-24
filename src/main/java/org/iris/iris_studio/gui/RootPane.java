package org.iris.iris_studio.gui;

import org.iris.iris_studio.gui.menus.IrisToolsView;

import javafx.scene.layout.BorderPane;

public class RootPane extends BorderPane {
	
	private IrisToolsView toolsView;
	private MainPane mainPane;
	private BottomPane bottomPane;
	
	public RootPane() {
		toolsView = new IrisToolsView();
		mainPane = new MainPane();
		bottomPane = new BottomPane();
		
		setTop(toolsView);
		setCenter(mainPane);
		setBottom(bottomPane);
	}
	
	public IrisToolsView getToolsPane() {
		return toolsView;
	}
	
	public MainPane getMainPane() {
		return mainPane;
	}
	
	public BottomPane getBottomPane() {
		return bottomPane;
	}
	
}
