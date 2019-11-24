package org.iris.iris_studio.gui.menus;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.gui.util.Theme;

import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WindowMenu extends Menu {
	
	private final Menu showView;
	private final Menu theme;

	public WindowMenu() {
		super("Window");
		
		showView = new Menu("Show view...");
		theme = new ThemeMenu();
		
		getItems().addAll(showView, new SeparatorMenuItem(), theme);
	}
	
	private class ThemeMenu extends Menu {
		
		public ThemeMenu() {
			super("Set Theme");
			
			for(Theme theme : Theme.values()) {
				Label label = new Label(normalizeThemeName(theme.name()));
				Tooltip tooltip = new Tooltip();
				tooltip.setAutoHide(false);
				tooltip.setAutoFix(true);
				ImageView imageView = new ImageView(loadImageForTheme(theme));
				// label.setGraphic(imageView);
				tooltip.setGraphic(imageView);
				label.setTooltip(tooltip);
				CustomMenuItem item = new CustomMenuItem(label);
				item.setOnAction(e -> IrisStudio.setTheme(theme));
				getItems().add(item);
			}
			
		}
		
		private Image loadImageForTheme(Theme theme) {
			String name = null;
			
			switch(theme) {
			case DARK_THEME:
				name = "/images/dark_theme_thumb.png";
				break;
			case LIGHT_THEME:
				name = "/images/light_theme_thumb.png";
				break;
			}
			
			return new Image(getClass().getResourceAsStream(name), 512, 512, true, true);
		}

		private String normalizeThemeName(String name) {
			String shortName = name.substring(0, name.indexOf('_'));
			return shortName.charAt(0) + shortName.substring(1).toLowerCase();
		}
		
	}
	
}
