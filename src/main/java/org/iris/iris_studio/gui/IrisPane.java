package org.iris.iris_studio.gui;

import org.iris.iris_studio.IrisStudio;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

public class IrisPane extends SplitPane {
	
	private static final float DEFAULT_DIVIDER_POSITION = 0.8f;
	
	private final ViewPane top;
	private final ViewPane bottom;
	
	public IrisPane() {
		setOrientation(Orientation.VERTICAL);
			
		top = new ViewPane();
		bottom = new ViewPane();
	
		IrisStudio.getWindow().showingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				
				setDividerPosition(0, DEFAULT_DIVIDER_POSITION);
				
				getScene().getWindow().showingProperty().removeListener(this);
			}
		});
		
		getItems().addAll(top, bottom);
	}

	public ViewPane getTop() {
		return top;
	}

	public ViewPane getBottom() {
		return bottom;
	}
	
}
