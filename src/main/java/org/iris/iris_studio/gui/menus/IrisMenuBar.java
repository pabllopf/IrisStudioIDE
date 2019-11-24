package org.iris.iris_studio.gui.menus;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class IrisMenuBar extends MenuBar {
	
	private final Menu fileMenu;
	private final Menu editMenu;
	private final Menu runMenu;
	private final Menu windowMenu;
	
	public IrisMenuBar() {
		setFocusTraversable(false);
		
		fileMenu = new FileMenu();
		editMenu = new EditMenu();
		runMenu = new RunMenu();
		windowMenu = new WindowMenu();
		
		getMenus().addAll(fileMenu, editMenu, runMenu, windowMenu);
	}

}
