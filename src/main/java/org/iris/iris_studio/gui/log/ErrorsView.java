package org.iris.iris_studio.gui.log;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import org.fxmisc.richtext.Caret;
import org.fxmisc.richtext.CodeArea;
import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.gui.View;

import javafx.scene.control.ListView;
import org.iris.iris_studio.gui.editor.FileView;
import org.iris.iris_studio.gui.editor.FileViews;
import org.iris.iris_studio.gui.menus.IrisToolsView;
import org.iris.iris_studio.projects.ProjectFile;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

import static org.iris.iris_studio.IrisStudio.findView;

public class ErrorsView extends View {
	
	private final ListView<String> errorList;
	
	public ErrorsView() {
		super("Errors");
		
		errorList = new ListView<>();
		
		setContent(errorList);
	}

	public void addError(String message) {
		if(!message.contains("error")){return;}
		errorList.getItems().add(message);


		errorList.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle(javafx.scene.input.MouseEvent event) {

				String[] compilerMessage = errorList.getSelectionModel().getSelectedItem().split(":");
				String path = compilerMessage[0] + ":" + compilerMessage[1];
				int line = Integer.parseInt(compilerMessage[2]);
				ObservableList<Tab> tabs = IrisStudio.getRightPane().getTop().getTabs();
				for (Tab tab: tabs) {
					if(path.contains(tab.getText()))
					{
						FileView file = (FileView) tab;
						IrisStudio.getRightPane().getTop().select(file);
						file.getCodeArea().selectRange(line-1,0,line, 0);
					}
				}




				//IrisStudio.getRightPane().getTop().getTabs().get(1).setDisable(true);
				//System.out.println(file.readAll());
			}
		});

		setContent(errorList);
	}

	public void clean() {
		errorList.getItems().clear();
		setContent(errorList);
	}
}
