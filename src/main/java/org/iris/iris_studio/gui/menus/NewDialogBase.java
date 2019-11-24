package org.iris.iris_studio.gui.menus;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.gui.IrisDialog;
import org.iris.iris_studio.gui.projects.ProjectView;
import org.iris.iris_studio.projects.FileType;
import org.iris.iris_studio.projects.ProjectFile;
import org.iris.iris_studio.projects.ProjectFolder;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Callback;

public abstract class NewDialogBase extends IrisDialog<List<ProjectFile>> {

	protected final TextField fileName;
	protected final VBox contents;
	
	public NewDialogBase(String title, String header, String content) {
		setTitle(title);
		setHeaderText(header);
		setContentText(content);
				
		initModality(Modality.APPLICATION_MODAL);
		
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		Node okButton = getDialogPane().lookupButton(ButtonType.OK);
		
		fileName = new TextField();
		fileName.setMinWidth(400);
		Label projectNameTitle = new Label("Name: ");
		Label projectNameError = new Label();
		projectNameError.setTextFill(Color.DARKRED);
		
		fileName.textProperty().addListener((observable, oldText, newText) -> {
			
			Optional<Path> path = toPath(newText);
			
			if(path.isPresent()) {
				Path rootPath = IrisStudio.getWorkspace().getFileSystem().getRoot();
				boolean exists = Files.exists(rootPath.resolve(path.get()));
				boolean empty = newText.trim().isEmpty();
				
				if(!exists) {
					projectNameError.setText("A file already exists with this name");
				} else if(empty) {
					projectNameError.setText("Name cannot be empty");
				}
				
				okButton.setVisible(!empty && !exists);
				projectNameError.setVisible(exists || empty);
				
			} else {
				fileName.setText(oldText);
			}
			
		});
		
		contents = new VBox();
		contents.setSpacing(10);
		
		HBox projectNameHBox = new HBox(projectNameTitle, fileName);
		projectNameHBox.setSpacing(5);
		VBox projectNameVBox = new VBox(projectNameHBox, projectNameError);
		projectNameVBox.setSpacing(3);
		
		contents.getChildren().add(projectNameVBox);
		
		getDialogPane().setContent(contents);
				
		setResultConverter(new Callback<ButtonType, List<ProjectFile>>() {
			
			@Override
			public List<ProjectFile> call(ButtonType buttonType) {
				if(buttonType != ButtonType.OK) {
					return null;
				} 
				
				Optional<ProjectView> projectView = IrisStudio.findView(ProjectView.class);
								
				if(!projectView.isPresent()) {
					showNoParentFolderSelectedDialog();
					return null;
				}
				
				Optional<ProjectFile> projectFile = projectView.get().getSelected();
				
				if(!projectFile.isPresent()) {
					showNoParentFolderSelectedDialog();
					return null;
				}
				
				ProjectFile file = projectFile.get();
				
				ProjectFolder folder = file.getType() == FileType.DIRECTORY 
						? (ProjectFolder) file 
						: (ProjectFolder) file.getProject().getRoot().find(file.getPath().getParent());
						
				String[] files = fileName.getText().split("/");
				
				List<ProjectFile> result = new ArrayList<>(files.length);
								
				if(files.length > 1) {
					for(int i = 0;i < files.length-1;i++) {
						folder = folder.createFolder(files[i]);
						result.add(folder);
					}
									
					result.add(getLastFile(folder, files[files.length-1]));
					
				} else {
					result.add(getLastFile(folder, fileName.getText()));
				}
				
				return result;
			}

			private void showNoParentFolderSelectedDialog() {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("No parent folder selected");
				alert.setContentText("Select a parent directory first");
				alert.showAndWait();
			}

		});
				
	}

	private Optional<Path> toPath(String newText) {
		try {
			return Optional.of(Paths.get(newText));
		} catch(InvalidPathException e) {
			return Optional.empty();
		}
	}

	protected abstract ProjectFile getLastFile(ProjectFolder folder, String name);
	
}
