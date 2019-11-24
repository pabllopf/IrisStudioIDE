package org.iris.iris_studio.gui.util;

import java.nio.file.Files;

import org.iris.iris_studio.gui.IrisDialog;
import org.iris.iris_studio.projects.FileType;
import org.iris.iris_studio.projects.ProjectFile;
import org.iris.iris_studio.projects.ProjectFolder;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Dialogs {
	
	public static Dialog<ProjectFile> newTextFileDialog(ProjectFolder folder) {
		return new NewProjectFileDialog(folder, FileType.RESOURCE, Icons.get(Icons.ICON_TEXT_FILE));
	}
	
	public static Dialog<ProjectFile> newHeaderFileDialog(ProjectFolder folder) {
		return new NewProjectFileDialog(folder, FileType.HEADER, Icons.get(Icons.ICON_HEADER_FILE));
	}
	
	public static Dialog<ProjectFile> newSourceFileDialog(ProjectFolder folder) {
		return new NewProjectFileDialog(folder, FileType.SOURCE, Icons.get(Icons.ICON_SOURCE_FILE));
	}
	
	public static Dialog<ProjectFile> newFolderDialog(ProjectFolder folder) {
		return new NewProjectFolderDialog(folder);
	}
	
	private static class NewProjectFolderDialog extends NewProjectFileDialog {

		public NewProjectFolderDialog(ProjectFolder root) {
			super(root, FileType.DIRECTORY, Icons.get(Icons.ICON_NEW_FOLDER));
			
			setResultConverter(new Callback<ButtonType, ProjectFile>() {
				
				@Override
				public ProjectFile call(ButtonType buttonType) {
					if(buttonType != ButtonType.OK) {
						return null;
					}
					return root.createFolder(fileNameField.getText());
				}
			});
			
		}
		
	}
	
	private static class NewProjectFileDialog extends IrisDialog<ProjectFile> {
		
		protected final Label fileNameFieldTitle;
		protected final TextField fileNameField;
		protected final Label errorLabel;
		
		public NewProjectFileDialog(ProjectFolder root, FileType type, Image icon) {
			setTitle("New File");
			initModality(Modality.APPLICATION_MODAL);
			
			Stage stage = (Stage) getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon);
			
			getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			Node okButton = getDialogPane().lookupButton(ButtonType.OK);
			
			fileNameFieldTitle = new Label("File name: ");
			
			errorLabel = new Label();
			errorLabel.setTextFill(Color.DARKRED);
			
			fileNameField = new TextField();
			fileNameField.setMinWidth(400);
			fileNameField.textProperty().addListener((observable, oldText, newText) -> {
				boolean empty = newText.trim().isEmpty();
				boolean exists = Files.exists(root.getPath().resolve(newText));

				if(empty) {
					errorLabel.setText("Name cannot be empty");
				} else if(exists) {
					errorLabel.setText("A file with this name already exists");
				}
				
				okButton.setVisible(!empty && !exists);
				errorLabel.setVisible(!okButton.isVisible());
			});
			
			HBox hbox = new HBox(fileNameFieldTitle, fileNameField);
			hbox.setSpacing(5);
			
			VBox contents = new VBox(hbox);
			
			getDialogPane().setContent(contents);
			
			setResultConverter(new Callback<ButtonType, ProjectFile>() {
				
				@Override
				public ProjectFile call(ButtonType buttonType) {
					if(buttonType != ButtonType.OK) {
						return null;
					}
					return root.createFile(fileNameField.getText(), type);
				}
			});
			
		}
		
	}

}
