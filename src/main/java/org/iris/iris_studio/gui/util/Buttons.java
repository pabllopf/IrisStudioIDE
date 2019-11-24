package org.iris.iris_studio.gui.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class Buttons {
	
	private Buttons() {}
	
	public static Button create(Image icon, boolean traversable) {
		return create(icon, traversable, null);
	}
	
	public static Button create(Image icon, boolean traversable, EventHandler<ActionEvent> onAction) {
		Button button = new Button();
		button.setFocusTraversable(traversable);
		button.setOnAction(onAction);
		ImageView view = new ImageView(icon);
		button.setGraphic(view);
		return button;
	}
	
	public static Button create(String text, boolean traversable) {
		return create(text, traversable, null);
	}
	
	public static Button create(String text, boolean traversable, EventHandler<ActionEvent> onAction) {
		Button button = new Button(text);
		button.setFocusTraversable(traversable);
		button.setOnAction(onAction);
		return button;
	}
	
}