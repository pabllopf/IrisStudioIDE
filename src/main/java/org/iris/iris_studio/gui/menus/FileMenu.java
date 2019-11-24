package org.iris.iris_studio.gui.menus;

import org.iris.iris_studio.gui.util.Icons;
import org.iris.iris_studio.gui.util.MenuItems;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class FileMenu extends Menu {
	
	private final Menu newMenu;
	private final MenuItem openProject;
	private final MenuItem save;
	private final MenuItem saveAll;
	private final MenuItem refresh;
	private final MenuItem exit;
	
	public FileMenu() {
		super("File");
		setMnemonicParsing(false);
		
		newMenu = new NewMenu();
		
		openProject = MenuItems.create("Open Project", Icons.get(Icons.ICON_OPEN_FOLDER),
				new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), MenuActions::openProject);
		
		save = MenuItems.create("Save", Icons.get(Icons.ICON_SAVE),
				new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), MenuActions::save);
		
		saveAll = MenuItems.create("Save all", Icons.get(Icons.ICON_SAVE_ALL), 
				new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN), MenuActions::saveAll);
		
		refresh = MenuItems.create("Refresh", Icons.get(Icons.ICON_REFRESH), new KeyCodeCombination(KeyCode.F5), MenuActions::refresh);
		
		exit = MenuItems.create("Exit", MenuActions::exit);

		getItems().addAll(newMenu, openProject, new SeparatorMenuItem(),
				save, saveAll, refresh, new SeparatorMenuItem(),
				exit);
	}

}
