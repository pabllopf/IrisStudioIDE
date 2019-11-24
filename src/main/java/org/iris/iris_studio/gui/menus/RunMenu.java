package org.iris.iris_studio.gui.menus;

import org.iris.iris_studio.gui.util.Icons;
import org.iris.iris_studio.gui.util.MenuItems;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class RunMenu extends Menu {
	
	private final MenuItem compileFile;
	private final MenuItem build;
	private final MenuItem run;
	private final MenuItem debug;
	
	public RunMenu() {
		super("Run");
		
		compileFile = MenuItems.create("Compile file", Icons.get(Icons.ICON_COMPILE_FILE), null);
		build = MenuItems.create("Build", Icons.get(Icons.ICON_BUILD),  null);
		run = MenuItems.create("Run", Icons.get(Icons.ICON_RUN), null);
		debug = MenuItems.create("Debug", Icons.get(Icons.ICON_DEBUG), null);
		
		getItems().addAll(compileFile, build, run, debug);
	}

}
