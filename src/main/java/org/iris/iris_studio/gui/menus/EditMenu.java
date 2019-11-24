package org.iris.iris_studio.gui.menus;

import org.iris.iris_studio.gui.util.Icons;
import org.iris.iris_studio.gui.util.MenuItems;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class EditMenu extends Menu {
	
	private final MenuItem undo;
	private final MenuItem redo;
	private final MenuItem cut;
	private final MenuItem copy;
	private final MenuItem paste;
	private final MenuItem closeTab;
	private final MenuItem closeAllTabs;
	private final MenuItem selectAll;
	private final MenuItem find;
	private final MenuItem findAndReplace;
	
	public EditMenu() {
		super("Edit");
		
		undo = MenuItems.create("Undo", Icons.get(Icons.ICON_UNDO), MenuActions::undo);
		redo = MenuItems.create("Redo", Icons.get(Icons.ICON_REDO), MenuActions::redo);
		cut = MenuItems.create("Cut", Icons.get(Icons.ICON_CUT), MenuActions::cut);
		copy = MenuItems.create("Copy", Icons.get(Icons.ICON_COPY), MenuActions::copy);
		paste = MenuItems.create("Paste", Icons.get(Icons.ICON_PASTE), MenuActions::paste);
		closeTab = MenuItems.create("Close current tab", null);
		closeAllTabs = MenuItems.create("Close all tabs", null);
		selectAll = MenuItems.create("Select all", null);
		find = MenuItems.create("Find...", null);
		findAndReplace = MenuItems.create("Find and replace...", null);
		
		getItems().addAll(
				undo, redo, new SeparatorMenuItem(),
				cut, copy, paste, new SeparatorMenuItem(), 
				closeTab, closeAllTabs, new SeparatorMenuItem(),
				selectAll, new SeparatorMenuItem(),
				find, findAndReplace);
	}
	
}
