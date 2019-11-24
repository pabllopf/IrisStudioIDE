package org.iris.iris_studio.gui.menus;

import org.iris.iris_studio.projects.ProjectFile;
import org.iris.iris_studio.projects.ProjectFolder;

import javafx.scene.control.CheckBox;

public class NewFolderDialog extends NewDialogBase {
	
	private final CheckBox includeDir;

	public NewFolderDialog() {
		super("New Folder", "Create new folder", null);
		includeDir = new CheckBox("Add to include directories");
		contents.getChildren().add(includeDir);
	}

	public boolean addToIncludeDirs() {
		return includeDir.isSelected();
	}
	
	@Override
	protected ProjectFile getLastFile(ProjectFolder folder, String name) {
		return folder.createFolder(name);
	}

}
