package org.iris.iris_studio.gui;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.gui.util.Theme;

import javafx.scene.control.Dialog;

public abstract class IrisDialog<T> extends Dialog<T> {
	
	public IrisDialog() {
		Theme.setTheme(getDialogPane().getScene(), IrisStudio.getTheme());
	}

}
