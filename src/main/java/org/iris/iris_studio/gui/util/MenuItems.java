package org.iris.iris_studio.gui.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;

public class MenuItems {
	
	public static MenuItem create(String name, EventHandler<ActionEvent> onClick) {
		return create(name, (KeyCombination)null, onClick);
	}
	
	public static MenuItem create(String name, KeyCombination accelerator, EventHandler<ActionEvent> onClick) {
		MenuItem item = new MenuItem(name);
		item.setOnAction(onClick);
		if(accelerator != null) {
			item.setAccelerator(accelerator);
		}
		item.setMnemonicParsing(false);
		return item;
	}
	
	public static MenuItem create(String name, Image icon, EventHandler<ActionEvent> onClick) {
		return create(name, icon, null, onClick);
	}
	
	public static MenuItem create(String name, Image icon, KeyCombination accelerator, EventHandler<ActionEvent> onClick) {
		MenuItem item = new MenuItem(name);
		item.setOnAction(onClick);
		if(accelerator != null) {
			item.setAccelerator(accelerator);
		}
		ImageView view = new ImageView(icon);
		item.setGraphic(view);
		item.setMnemonicParsing(false);
		return item;
	}

}
