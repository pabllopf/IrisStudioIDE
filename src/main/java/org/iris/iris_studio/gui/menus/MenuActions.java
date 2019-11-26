package org.iris.iris_studio.gui.menus;

import java.awt.*;
import java.io.File;
import java.util.Optional;

import javafx.scene.Node;
import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.compiler.Compiler;
import org.iris.iris_studio.gui.editor.FileViews;
import org.iris.iris_studio.gui.log.LogView;
import org.iris.iris_studio.gui.projects.ProjectView;
import org.iris.iris_studio.projects.Project;
import org.iris.iris_studio.projects.ProjectFile;
import javafx.concurrent.Task;

import javafx.event.Event;
import javafx.stage.DirectoryChooser;

import static org.iris.iris_studio.IrisStudio.findView;
import static org.iris.iris_studio.IrisStudio.getToolsPane;

public final class MenuActions {

	private MenuActions() {}

	public static void newProject(Event event) {
		NewProjectDialog dialog = new NewProjectDialog();
		dialog.showAndWait().ifPresent(project -> {
			Optional<ProjectView> view = IrisStudio.findView(ProjectView.class);
			view.ifPresent(v -> {
				v.addProject(project);
				v.select();
			});
		});
	}
	
	public static void openProject(Event event) {
		DirectoryChooser chooser = new DirectoryChooser();
		File dir = chooser.showDialog(IrisStudio.getWindow());
		if(dir != null) {
			for(String file : dir.list()) {
				if(file.equals(Project.PROJECT_FILENAME)) {
					if(IrisStudio.getWorkspace().contains(dir.getName())) {
						// Already open
						return;
					}
					IrisStudio.getWorkspace().addProject(dir.getName());
					Optional<ProjectView> view = IrisStudio.findView(ProjectView.class);
					view.ifPresent(v -> v.addProject(IrisStudio.getWorkspace().getProject(dir.getName())));
					return;
				}
			}
		}
	}

	public static void newFolder(Event event) {
		NewFolderDialog dialog = new NewFolderDialog();
		dialog.showAndWait().ifPresent(files -> {
			Optional<ProjectView> view = IrisStudio.findView(ProjectView.class);
			view.ifPresent(v -> {
				for(ProjectFile file : files) {
					v.addFile(file);
				}
				v.select();
			});
		});
	}

	public static void newFile(Event event) {
		NewFileDialog dialog = new NewFileDialog();
		dialog.showAndWait().ifPresent(files -> {
			Optional<ProjectView> view = IrisStudio.findView(ProjectView.class);
			view.ifPresent(v -> {
				for(ProjectFile file : files) {
					v.addFile(file);
				}
				v.select();
			});
		});
	}
	
	public static void save(Event event) {
		FileViews.getSelected().ifPresent(view -> view.save());
	}
	
	public static void saveAll(Event event) {
		FileViews.getAll().forEach(view -> view.save());
	}
	
	public static void refresh(Event event) {
		// TODO
	}
	
	public static void undo(Event event) {
		FileViews.getSelected().ifPresent(view -> {
			view.getCodeArea().undo();
		});
	}
	
	public static void redo(Event event) {
		FileViews.getSelected().ifPresent(view -> {
			view.getCodeArea().redo();
		});
	}
	
	public static void cut(Event event) {
		FileViews.getSelected().ifPresent(view -> {
			view.getCodeArea().cut();
		});
	}
	
	public static void copy(Event event) {
		FileViews.getSelected().ifPresent(view -> {
			view.getCodeArea().copy();
		});
	}
	
	public static void paste(Event event) {
		FileViews.getSelected().ifPresent(view -> {
			view.getCodeArea().paste();
		});
	}

	public static void build(Event event) {
		FileViews.getAll().forEach(view -> view.save());

		final Node build = getToolsPane().getToolBar().getItems().get(14);
		final Node run = getToolsPane().getToolBar().getItems().get(15);
		final Compiler compiler = new Compiler();
		final LogView logView = findView(LogView.class).get();
		Thread thread = new Thread(() -> {
			long startTime = System.currentTimeMillis();
			build.setDisable(true);
			run.setDisable(true);

			compiler.buildProject();

			build.setDisable(false);
			run.setDisable(false);
			long endTime = System.currentTimeMillis();
			logView.AppendToLog(" in " + (endTime - startTime) + "ms");
			return;
		});
		thread.start();
	}

	public static void run(Event event) {
		FileViews.getAll().forEach(view -> view.save());

		final Node build = getToolsPane().getToolBar().getItems().get(14);
		final Node run = getToolsPane().getToolBar().getItems().get(15);
		final Compiler compiler = new Compiler();
		final LogView logView = findView(LogView.class).get();
		Thread thread = new Thread(() -> {
			long startTime = System.currentTimeMillis();
			build.setDisable(true);
			run.setDisable(true);

			if(compiler.buildProject()) {
				compiler.run();
			}

			build.setDisable(false);
			run.setDisable(false);
			long endTime = System.currentTimeMillis();
			logView.AppendToLog(" in " + (endTime - startTime) + "ms");
			return;
		});
		thread.start();
	}
	
	public static void exit(Event event) {
		IrisStudio.exit();
	}

}
