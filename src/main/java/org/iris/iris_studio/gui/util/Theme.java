package org.iris.iris_studio.gui.util;

import java.util.Objects;

import javafx.scene.Scene;

public enum Theme {
	
	LIGHT_THEME("/themes/light_theme.css"),
	DARK_THEME("themes/dark_theme.css");
	
	public final String stylesheet;
	
	private Theme(String stylesheet) {
		this.stylesheet = stylesheet;
	}
	
	public static Theme of(String stylesheet) {
		for(Theme theme : values()) {
			if(Objects.equals(theme.stylesheet, stylesheet)) {
				return theme;
			}
		}
		return null;
	}
	
	public static void setTheme(Scene scene, Theme theme) {
		scene.getStylesheets().clear();
		if(theme.stylesheet != null) {
			scene.getStylesheets().add(theme.stylesheet);
		}
	}

}
