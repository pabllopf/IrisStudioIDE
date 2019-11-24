package org.iris.iris_studio.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.config.SelectWorkspaceDialog.SelectWorkspaceResult;
import org.iris.iris_studio.gui.util.Icons;
import org.iris.iris_studio.gui.util.Theme;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.util.Callback;

public class SelectWorkspaceDialog extends Dialog<SelectWorkspaceResult> {

	private final Label title;
	private final Button selectButton;
	private final CheckBox checkBox;
	private final SelectWorkspaceResult result;

	public SelectWorkspaceDialog() {
		setTitle("Select Workspace");
		initModality(Modality.APPLICATION_MODAL);
		
		Theme.setTheme(getDialogPane().getScene(), IrisStudio.getTheme());

		result = new SelectWorkspaceResult();

		title = new Label("Select Workspace folder");

		Label icon = new Label();
		icon.setGraphic(new ImageView(Icons.get(Icons.ICON_OPEN_FOLDER)));

		TextField pathField = new TextField();
		pathField.setMinWidth(400);

		selectButton = new Button("...");
		selectButton.setOnAction(e -> {

			DirectoryChooser chooser = new DirectoryChooser();

			File folder = chooser.showDialog(getDialogPane().getScene().getWindow());

			if(folder != null) {
				result.workspacePath = folder.toString();
				pathField.setText(folder.toString());
			}

		});

		checkBox = new CheckBox("Do not ask again");

		VBox contents = new VBox();
		contents.setSpacing(5);

		VBox pathArea = new VBox();
		pathArea.setSpacing(3);

		// Icon [text-field-with-path] [...]
		HBox hbox = new HBox();
		hbox.setSpacing(2);

		Label errorLabel = new Label();
		errorLabel.setVisible(false);
		errorLabel.setTextFill(Color.DARKRED);

		hbox.getChildren().addAll(icon, pathField, selectButton);

		pathArea.getChildren().addAll(title, hbox, errorLabel);

		contents.getChildren().addAll(pathArea, checkBox);

		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		Node okButton = getDialogPane().lookupButton(ButtonType.OK);
		okButton.setVisible(false);

		pathField.textProperty().addListener((observable, oldText, newText) -> {
			boolean empty = newText == null || newText.trim().isEmpty();
			boolean fileExists = Files.exists(Paths.get(newText));
			okButton.setVisible(!empty && fileExists);
			if(!okButton.isVisible()) {
				if(empty) {
					errorLabel.setText("\tThis field cannot be empty");
				} else if(!fileExists) {
					errorLabel.setText("\tFolder does not exist");
				}
			}
			errorLabel.setVisible(!okButton.isVisible());
		});

		getDialogPane().setContent(contents);

		setResultConverter(new Callback<ButtonType, SelectWorkspaceDialog.SelectWorkspaceResult>() {

			@Override
			public SelectWorkspaceResult call(ButtonType buttonType) {
				if(buttonType != ButtonType.OK) {
					return null;
				}
				if(result.workspacePath == null || result.workspacePath.trim().isEmpty()) {
					return null;
				}
				
				result.doNotAskAgain = !checkBox.isSelected();
				
				return result;
			}
		});

	}

	public class SelectWorkspaceResult {

		String workspacePath;
		boolean doNotAskAgain;

		public String getWorkspacePath() {
			return workspacePath;
		}

		public boolean isDoNotAskAgain() {
			return doNotAskAgain;
		}

	}

}
