package org.iris.iris_studio.gui.projects;

import java.util.Optional;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.config.IrisConfig;
import org.iris.iris_studio.config.IrisConfig.OpenProjects;
import org.iris.iris_studio.config.IrisConfigWriter;
import org.iris.iris_studio.gui.View;
import org.iris.iris_studio.gui.editor.FileViews;
import org.iris.iris_studio.gui.menus.FileMenu;
import org.iris.iris_studio.gui.util.Icons;
import org.iris.iris_studio.projects.FileType;
import org.iris.iris_studio.projects.Project;
import org.iris.iris_studio.projects.ProjectFile;
import org.iris.iris_studio.projects.ProjectFolder;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class ProjectView extends View implements IrisConfigWriter {

	private final TreeView<ProjectFile> tree;

	public ProjectView() {
		super("Project Explorer");
		setGraphic(new ImageView(Icons.get(Icons.ICON_OPEN_FOLDER)));

		TreeItem<ProjectFile> root = new TreeItem<>();
		root.setExpanded(true);

		tree = new TreeView<>(root);
		tree.setShowRoot(false);	

		tree.setOnKeyPressed(e -> {

			if(e.getCode() == KeyCode.ENTER) {
				onEnterKeyPressed();
			} else if(e.getCode() == KeyCode.DELETE) {
				onDeleteKeyPressed();
			}

		});

		tree.setOnMouseClicked(e -> {

			if(tree.getSelectionModel().getSelectedIndex() == 0) {
				return;
			}

			if(e.getClickCount() != 2) {
				return;
			}

			TreeItem<ProjectFile> selectedNode = tree.getSelectionModel().getSelectedItem();

			if(selectedNode == null) {
				return;
			}

			ProjectFile file = selectedNode.getValue();

			if(file.getType() == FileType.DIRECTORY) {
				return;
			}
			
			FileViews.addOrSelect(file);
		});

		ContextMenu cm = new ContextMenu();
		cm.getItems().addAll(new FileMenu().getItems());
		setContextMenu(cm);
		tree.setContextMenu(cm);

		tree.setCellFactory(tree -> new ProjectTreeCell());

		if(IrisStudio.getConfig().getOpenProjects() != null) {
			addOpenProjects(IrisStudio.getConfig().getOpenProjects());
		}

		IrisStudio.getConfig().addConfigWriter(this);

		setContent(tree);		
	}

	private void onDeleteKeyPressed() {

		TreeItem<ProjectFile> selected = tree.getSelectionModel().getSelectedItem();

		if(selected == null) {
			return;
		}

		ProjectFile file = selected.getValue();

		if(file.getType() == FileType.DIRECTORY) {
			if(file.equals(file.getProject().getRoot())) {
				onDeleteProject(file.getProject());
			} else {
				onDeleteFolder((ProjectFolder) file);
			}
		} else {
			onDeleteFile(file);
		}

	}

	private void onDeleteFile(ProjectFile file) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete " + file.getName());
		alert.setHeaderText("Are you sure you want to delete this file?");
		alert.setContentText("The file will be deleted from disk");
		alert.getDialogPane().getChildren().add(new Label("HAY"));
		alert.showAndWait().ifPresent(button -> {
			if(button == ButtonType.OK) {
				file.getParent().delete(file);
				removeFile(file);
				FileViews.close(file);
			}
		});
	}

	private void onDeleteFolder(ProjectFolder folder) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete " + folder.getName());
		alert.setHeaderText("Are you sure you want to delete this folder?");
		alert.setContentText("The folder and all its files will be deleted from disk");
		alert.showAndWait().ifPresent(button -> {
			if(button == ButtonType.OK) {
				folder.getParent().delete(folder);
				removeFile(folder);
				FileViews.getAll().stream()
				.filter(v -> v.getFile().getParent().equals(folder))
				.forEach(v -> v.close());
			}
		});
	}

	private void onDeleteProject(Project project) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		
		Node graphic = alert.getDialogPane().getGraphic();
		
		CheckBox deleteFromDisk = new CheckBox("Delete from disk too");
		
		alert.setDialogPane(new DialogPane() {
			@Override
			protected Node createDetailsButton() {
				return deleteFromDisk;
			}
		});
		
		alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		alert.setTitle("Delete project " + project.getName());
		alert.setHeaderText("Are you sure you want to delete this project?");
		
		// Fool the dialog into thinking there is some expandable content
		// a Group won't take up any space if it has no children
		alert.getDialogPane().setExpandableContent(new Group());
		alert.getDialogPane().setExpanded(true);
		// Reset the dialog graphic using the default style
		alert.getDialogPane().setGraphic(graphic);

		alert.showAndWait().ifPresent(button -> {
			
			if(button == ButtonType.OK) {
				
				if(deleteFromDisk.isSelected()) {
					IrisStudio.getWorkspace().deleteProject(project.getName());
				}
				
				FileViews.getAll().stream()
					.filter(v -> v.getFile().getProject().equals(project))
					.forEach(v -> v.close());
				
				removeProject(project);
			}
		});
	}

	private void onEnterKeyPressed() {

		TreeItem<ProjectFile> selected = tree.getSelectionModel().getSelectedItem();

		if(selected == null) {
			return;
		}

		ProjectFile file = selected.getValue();

		if(file.getType() == FileType.DIRECTORY) {
			return;
		}

		FileViews.addOrSelect(file);
	}

	private void addOpenProjects(OpenProjects openProjects) {
		for(String projectName : openProjects.getOpenProjects()) {
			addProject(IrisStudio.getWorkspace().getProject(projectName));
		}
	}

	public void addProject(Project project) {
		addAllFiles(project.getRoot(), tree.getRoot());
	}

	public void addFile(ProjectFile file) {
		TreeItem<ProjectFile> root = tree.getRoot();

		TreeItem<ProjectFile> projectNode = root.getChildren().stream()
				.filter(c -> c.getValue().getProject().getName()
						.equals(file.getProject().getName()))
				.findAny().get();

		TreeItem<ProjectFile> folderNode = findFolderNode(projectNode, file);

		folderNode.getChildren().add(new TreeItem<>(file));
	}
	
	private TreeItem<ProjectFile> findFolderNode(TreeItem<ProjectFile> root, ProjectFile fileToAdd) {

		for(TreeItem<ProjectFile> fileNode : root.getChildren()) {

			if(fileToAdd.getPath().startsWith(fileNode.getValue().getPath())) {
				// Node must be a folder at this point
				if(fileToAdd.getPath().getParent().equals(fileNode.getValue().getPath())) {
					return fileNode;
				} else {
					return findFolderNode(fileNode, fileToAdd);
				}
			}
		}
		return root;
	}

	private void addAllFiles(ProjectFile root, TreeItem<ProjectFile> treeRoot) {
		if(root.getName().equals(Project.PROJECT_FILENAME)) {
			return;
		}
		
		TreeItem<ProjectFile> item = new TreeItem<>(root);

		if(root instanceof ProjectFolder) {
			ProjectFolder folder = (ProjectFolder)root;
			for(ProjectFile file : folder) {
				addAllFiles(file, item);
			}
		}

		treeRoot.getChildren().add(item);
	}

	private void removeFile(ProjectFile file) {
		TreeItem<ProjectFile> root = findFolderNode(tree.getRoot(), file);
		root.getChildren().removeIf(child -> child.getValue().equals(file));
	}

	private void removeProject(Project project) {
		tree.getRoot().getChildren().removeIf(child -> child.getValue().equals(project.getRoot()));
	}

	private class ProjectTreeCell extends TreeCell<ProjectFile> {

		@Override
		protected void updateItem(ProjectFile file, boolean empty) {
			super.updateItem(file, empty);

			if(empty) {
				reset();
			} else {
				setContents(file);
			}
		}

		private void setContents(ProjectFile file) {
			setText(file.getName());
			System.out.println(file.getName());

			switch(file.getType()) {
			case DIRECTORY:
				setGraphic(new ImageView(Icons.get(Icons.ICON_OPEN_FOLDER)));
				break;
			case HEADER:
				setGraphic(new ImageView(Icons.get(Icons.ICON_HEADER_FILE)));
				break;
			case RESOURCE:
				setGraphic(new ImageView(Icons.get(Icons.ICON_TEXT_FILE)));
				break;
			case SOURCE:
				setGraphic(new ImageView(Icons.get(Icons.ICON_SOURCE_FILE)));
				break;
			}
		}

		private void reset() {
			setText("");
			setGraphic(null);
			return;
		}

	}

	@Override
	public void writeConfig(IrisConfig config) {
		String[] openProjects = new String[tree.getRoot().getChildren().size()];

		for(int i = 0;i < openProjects.length;i++) {
			openProjects[i] = tree.getRoot().getChildren().get(i).getValue().getProject().getName();
		}

		config.setOpenProjects(new OpenProjects(openProjects));
	}

	public Optional<ProjectFile> getSelected() {
		TreeItem<ProjectFile> node = tree.getSelectionModel().getSelectedItem();
		return Optional.ofNullable(node == null ? null : node.getValue());
	}

}
