package org.iris.iris_studio.gui.menus;

import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

public class IrisToolsView extends VBox {
	
	private final MenuBar menuBar;
	private final ToolBar toolBar;
	
	public IrisToolsView() {
		menuBar = new IrisMenuBar();
		toolBar = new IrisToolBar();
		
		getChildren().addAll(menuBar, toolBar);
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

	public ToolBar getToolBar() {
		return toolBar;
	}
	
}
