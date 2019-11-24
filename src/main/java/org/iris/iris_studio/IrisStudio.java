package org.iris.iris_studio;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import org.iris.iris_studio.config.IrisConfig;
import org.iris.iris_studio.config.SelectWorkspaceDialog;
import org.iris.iris_studio.config.SelectWorkspaceDialog.SelectWorkspaceResult;
import org.iris.iris_studio.gui.BottomPane;
import org.iris.iris_studio.gui.IrisPane;
import org.iris.iris_studio.gui.MainPane;
import org.iris.iris_studio.gui.RootPane;
import org.iris.iris_studio.gui.ViewPane;
import org.iris.iris_studio.gui.editor.FileViews;
import org.iris.iris_studio.gui.menus.IrisToolsView;
import org.iris.iris_studio.gui.util.Theme;
import org.iris.iris_studio.projects.Workspace;
import org.iris.iris_studio.projects.io.LocalFileSystem;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public final class IrisStudio extends Application {
	
	private static final int DEFAULT_WINDOW_WIDTH = 1280;
	private static final int DEFAULT_WINDOW_HEIGHT = 720;

	private static IrisStudio instance;

	private static synchronized void setInstance(IrisStudio instance) {
		Objects.requireNonNull(instance);
		if(IrisStudio.instance != null) {
			throw new IllegalStateException("IrisStudio has been already initialized");
		}
		IrisStudio.instance = instance;
	}
	
	private static IrisStudio getInstance() {
		if(instance == null) {
			throw new IllegalStateException("IrisStudio has not been already initialized");
		}
		return instance;
	}
	
	public static Stage getWindow() {
		return getInstance().window;
	}
	
	public static Scene getScene() {
		return getWindow().getScene();
	}
	
	public static RootPane getRootPane() {
		return getInstance().rootPane;
	}
	
	public static IrisToolsView getToolsPane() {
		return getRootPane().getToolsPane();
	}
	
	public static MainPane getMainPane() {
		return getRootPane().getMainPane();
	}
	
	public static BottomPane getBottomPane() {
		return getRootPane().getBottomPane();
	}
	
	public static IrisPane getLeftPane() {
		return getMainPane().getLeftPane();
	}
	
	public static IrisPane getRightPane() {
		return getMainPane().getRightPane();
	}
	
	public static ViewPane getViewPaneAt(TabPanePosition position) {
		switch(position) {
		case BOTTOM_LEFT:
			return getLeftPane().getBottom();
		case BOTTOM_RIGHT:
			return getRightPane().getBottom();
		case TOP_LEFT:
			return getLeftPane().getTop();
		case TOP_RIGHT:
			return getRightPane().getTop();
		}
		return null;
	}
	
	public static <T extends Tab> Optional<T> findView(Class<T> clazz) {
		for(TabPanePosition position : TabPanePosition.values()) {
			Optional<T> tab = getViewPaneAt(position).findOfClass(clazz);
			if(tab.isPresent()) {
				return tab;
			}
		}
		return Optional.empty();
	}
	
	public static IrisConfig getConfig() {
		return getInstance().config;
	}
	
	public static Workspace getWorkspace() {
		return getInstance().workspace;
	}
	
	public static Theme getTheme() {
		return Theme.of(getConfig().getTheme());
	}
	
	public static void setTheme(Theme theme) {
		getConfig().setTheme(theme.stylesheet);
		Theme.setTheme(getScene(), theme);
		FileViews.applyFontToAllFileViews();
	}
	
	public static void exit() {
		getWindow().close();
	}
	
	private Stage window;
	private RootPane rootPane;
	private IrisConfig config;
	private Workspace workspace;

	public IrisStudio() {
		if(instance != null) {
			throw new IllegalStateException("IrisStudio has been already initialized");
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		setInstance(this);

		window = stage;
		
		config = new IrisConfig(this);
		workspace = loadWorkspace();

		rootPane = new RootPane();

		initWindow(stage);

		stage.show();
	}

	private Workspace loadWorkspace() {
		Workspace workspace;
		
		String workspacePath = config.getWorkspacePath();

		if(config.getAskWorkspacePath() || workspacePath == null || !Files.exists(Paths.get(workspacePath))) {
			workspace = askWorkspacePath();
		} else {
			workspace = new Workspace(new LocalFileSystem(Paths.get(workspacePath)));
		}

		if(config.getOpenProjects() != null) {
			for(String projectName : config.getOpenProjects().getOpenProjects()) {
				workspace.addProject(projectName);
			}
		}
		
		return workspace;
	}

	private Workspace askWorkspacePath() {

		Dialog<SelectWorkspaceResult> dialog = new SelectWorkspaceDialog();

		Optional<SelectWorkspaceResult> optional = dialog.showAndWait();

		if(!optional.isPresent()) {
			exitImmediately();
		}

		SelectWorkspaceResult result = optional.get();

		config.setAskWorkspacePath(result.isDoNotAskAgain());
		config.setWorkspacePath(result.getWorkspacePath());

		return new Workspace(new LocalFileSystem(Paths.get(result.getWorkspacePath())));
	}

	private void exitImmediately() {
		System.exit(1);
	}

	private void initWindow(Stage stage) {
		stage.setTitle("IrisStudio");
		stage.setScene(createScene());
		stage.setMaximized(true);
		setWindowListeners();
	}
	
	private Scene createScene() {

		Scene scene = new Scene(rootPane, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, false, SceneAntialiasing.BALANCED);
		
		Theme.setTheme(scene, getTheme());

		scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.isAltDown())
					event.consume();
			}
		});

		return scene;
	}

	private void setWindowListeners() {
		WindowEventHandlers handlers = new WindowEventHandlers();
		window.setOnCloseRequest(handlers.getOnCloseRequest());
		window.setOnHiding(handlers.getOnHiding());
		window.setOnHidden(handlers.getOnHidden());
		window.setOnShowing(handlers.getOnShowing());
		window.setOnShown(handlers.getOnShown());
	}


	private class WindowEventHandlers {

		private EventHandler<WindowEvent> getOnCloseRequest() {
			return e -> {
				System.out.println(">> Terminating IrisStudio...");
				FileViews.getAll().forEach(view -> view.shutdown());
				config.save();
			};
		}

		private EventHandler<WindowEvent> getOnHidden() {
			return e -> {

			};
		}

		private EventHandler<WindowEvent> getOnHiding() {
			return e -> {

			};
		}

		private EventHandler<WindowEvent> getOnShowing() {
			return e -> {

			};
		}

		private EventHandler<WindowEvent> getOnShown() {
			return e -> {

			};
		}

	}

}
