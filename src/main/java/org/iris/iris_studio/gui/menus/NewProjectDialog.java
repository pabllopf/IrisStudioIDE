package org.iris.iris_studio.gui.menus;

import java.nio.file.Files;
import java.nio.file.Path;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.gui.IrisDialog;
import org.iris.iris_studio.projects.Project;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Callback;

public class NewProjectDialog extends IrisDialog<Project> {
	
	private final TextField projectName;
	private final CheckBox helloWorld;
	private final ComboBox<String> version;
	
	public NewProjectDialog() {
		setTitle("New Project");
		initModality(Modality.APPLICATION_MODAL);
		
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		Node okButton = getDialogPane().lookupButton(ButtonType.OK);
		
		projectName = new TextField();
		projectName.setMinWidth(400);
		Label projectNameTitle = new Label("Project Name: ");
		Label projectNameError = new Label();
		projectNameError.setTextFill(Color.DARKRED);
		
		projectName.textProperty().addListener((observable, oldText, newText) -> {
			
			Path path = IrisStudio.getWorkspace().getFileSystem().getRoot();
			
			boolean exists = Files.exists(path.resolve(newText));
			boolean empty = newText.trim().isEmpty();
			
			if(!exists) {
				projectNameError.setText("A file already exists with this name");
			} else if(empty) {
				projectNameError.setText("Name cannot be empty");
			}
			
			okButton.setVisible(!empty && !exists);
			projectNameError.setVisible(exists || empty);
			
		});
		
		VBox contents = new VBox();
		contents.setSpacing(10);
		
		HBox projectNameHBox = new HBox(projectNameTitle, projectName);
		projectNameHBox.setSpacing(5);
		VBox projectNameVBox = new VBox(projectNameHBox, projectNameError);
		projectNameVBox.setSpacing(3);
		
		contents.getChildren().add(projectNameVBox);
		
		helloWorld = new CheckBox("Create with 'Hello World' sample");
		
		contents.getChildren().add(helloWorld);
		
		version = new ComboBox<>();
		version.getItems().addAll("C++11", "C++14", "C++17", "C++20");
		version.getSelectionModel().select(0);
		Label versionTitle = new Label("Select C++ version");
		
		HBox versionHBox = new HBox(versionTitle, version);
		versionHBox.setSpacing(5);
		
		contents.getChildren().add(versionHBox);
		
		getDialogPane().setContent(contents);
		
		setResultConverter(new Callback<ButtonType, Project>() {
			
			@Override
			public Project call(ButtonType buttonType) {
				if(buttonType != ButtonType.OK) {
					return null;
				} 
				return IrisStudio.getWorkspace()
						.createProject(projectName.getText(), helloWorld.isSelected());
			}
		});
		
	}
	
}
