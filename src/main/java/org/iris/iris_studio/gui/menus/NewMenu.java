package org.iris.iris_studio.gui.menus;

import org.iris.iris_studio.gui.util.Icons;
import org.iris.iris_studio.gui.util.MenuItems;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class NewMenu extends Menu {
	
	private final MenuItem newProject;
	private final MenuItem newDirectory;
	private final Menu newFile;
	private final MenuItem newHeaderFile;
	private final MenuItem newSourceFile;
	private final MenuItem newPlainFile;
	
	public NewMenu() {
		super("New...");
		setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		setMnemonicParsing(false);
		
		newProject = MenuItems.create("Project", Icons.get(Icons.ICON_NEW_PROJECT), MenuActions::newProject);
		
		newDirectory = MenuItems.create("Directory", Icons.get(Icons.ICON_NEW_FOLDER), MenuActions::newFolder);
				
		newFile = new Menu("File");
		
		newHeaderFile = MenuItems.create("Header", Icons.get(Icons.ICON_HEADER_FILE), MenuActions::newFile);
		newSourceFile = MenuItems.create("Source", Icons.get(Icons.ICON_SOURCE_FILE),  MenuActions::newFile);
		newPlainFile = MenuItems.create("Text", Icons.get(Icons.ICON_TEXT_FILE),  MenuActions::newFile);
		
		newFile.getItems().addAll(newHeaderFile, newSourceFile, newPlainFile);
		getItems().addAll(newProject, newDirectory, newFile);
	}

	
}
