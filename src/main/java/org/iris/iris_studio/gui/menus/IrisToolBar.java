package org.iris.iris_studio.gui.menus;

import org.iris.iris_studio.gui.util.Buttons;
import org.iris.iris_studio.gui.util.Icons;

import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

public class IrisToolBar extends ToolBar {
	
	private final Button newProject;
	private final Button openProject;
	
	private final Button saveFile;
	private final Button saveAll;
	
	private final Button undo;
	private final Button redo;
	private final Button cut;
	private final Button copy;
	private final Button paste;
	
	private final Button compileFile;
	private final Button build;
	private final Button run;
	private final Button debug;
	
	public IrisToolBar() {
		newProject = Buttons.create(Icons.get(Icons.ICON_NEW_PROJECT), false, MenuActions::newProject);
		openProject = Buttons.create(Icons.get(Icons.ICON_OPEN_FOLDER), false, MenuActions::openProject);
		
		saveFile = Buttons.create(Icons.get(Icons.ICON_SAVE), false, MenuActions::save);
		saveAll = Buttons.create(Icons.get(Icons.ICON_SAVE_ALL), false, MenuActions::saveAll);
		
		undo = Buttons.create(Icons.get(Icons.ICON_UNDO), false, MenuActions::undo);
		redo = Buttons.create(Icons.get(Icons.ICON_REDO), false, MenuActions::redo);
		cut = Buttons.create(Icons.get(Icons.ICON_CUT), false, MenuActions::cut);
		copy = Buttons.create(Icons.get(Icons.ICON_COPY), false, MenuActions::copy);
		paste = Buttons.create(Icons.get(Icons.ICON_PASTE), false, MenuActions::paste);
		
		
		compileFile = Buttons.create(Icons.get(Icons.ICON_COMPILE_FILE), false);
		build = Buttons.create(Icons.get(Icons.ICON_BUILD), false, MenuActions::build);
		run = Buttons.create(Icons.get(Icons.ICON_RUN), false, MenuActions::run);
		debug = Buttons.create(Icons.get(Icons.ICON_DEBUG), false);
		
		getItems().addAll(
				newProject, openProject, new Separator(),
				saveFile, saveAll, new Separator(),
				undo, redo, new Separator(), 
				cut, copy, paste, new Separator(),
				compileFile, build, run, debug);
	}

	public Button buttonBuild()
	{
		return this.build;
	}
}
