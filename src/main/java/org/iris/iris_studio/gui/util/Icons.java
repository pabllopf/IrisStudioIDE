package org.iris.iris_studio.gui.util;

import javafx.scene.image.Image;

public final class Icons {
	
	private Icons() {}
	
	private static final String ICONS_ROOT = "/icons/";
	
	public static final String ICON_NEW_PROJECT = "buttons/new_project.png";
	public static final String ICON_NEW_FILE = "buttons/new_file.png";
	public static final String ICON_NEW_FOLDER = "buttons/new_folder.png";
	public static final String ICON_OPEN_FOLDER = "buttons/open_folder.png";
	public static final String ICON_SAVE = "buttons/save.png";
	public static final String ICON_SAVE_ALL = "buttons/save_all.png";
	public static final String ICON_HEADER_FILE = "buttons/header_file.png";
	public static final String ICON_SOURCE_FILE = "buttons/source_file.png";
	public static final String ICON_TEXT_FILE = "buttons/text_file.png";
	public static final String ICON_REFRESH = "buttons/refresh.png";
	public static final String ICON_UNDO = "buttons/undo.png";
	public static final String ICON_REDO = "buttons/redo.png";
	public static final String ICON_CUT = "buttons/cut.png";
	public static final String ICON_COPY = "buttons/copy.png";
	public static final String ICON_PASTE = "buttons/paste.png";
	public static final String ICON_COMPILE_FILE = "buttons/compile_file.png";
	public static final String ICON_BUILD = "buttons/build.png";
	public static final String ICON_RUN = "buttons/run.png";
	public static final String ICON_DEBUG = "buttons/debug.png";
	public static final String ICON_GARBAGE = "buttons/garbage.png";
	
	public static Image get(String iconName) {
		return ImageCache.getImage(ICONS_ROOT + iconName);
	}

}
