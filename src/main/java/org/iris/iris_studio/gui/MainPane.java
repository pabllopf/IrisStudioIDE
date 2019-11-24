package org.iris.iris_studio.gui;

import java.util.List;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.config.IrisConfig;
import org.iris.iris_studio.config.IrisConfig.Layout;
import org.iris.iris_studio.config.IrisConfig.Layout.LayoutPane;
import org.iris.iris_studio.config.IrisConfig.OpenFiles;
import org.iris.iris_studio.config.IrisConfigWriter;
import org.iris.iris_studio.gui.editor.FileView;
import org.iris.iris_studio.projects.Project;
import org.iris.iris_studio.projects.ProjectFile;
import org.iris.iris_studio.projects.Workspace;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainPane extends SplitPane implements IrisConfigWriter {
	
	private static MainPane instance;

	public static MainPane getCenterPane() {
		return instance;
	}
	
	private final IrisPane left;
	private final IrisPane right;

	public MainPane() {
		instance = this;
		
		setOrientation(Orientation.HORIZONTAL);

		left = new IrisPane();
		right = new IrisPane();

		IrisStudio.getWindow().showingProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setDividerPosition(0, 0.2);
				getScene().getWindow().showingProperty().removeListener(this);
			}
		});

		setUserLayout();

		IrisStudio.getConfig().addConfigWriter(this);

		getItems().addAll(left, right);
	}
	
	public IrisPane getLeftPane() {
		return left;
	}
	
	public IrisPane getRightPane() {
		return right;
	}

	public void setUserLayout() {

		final Layout layout = IrisStudio.getConfig().getLayout();

		setLayout(layout.getLeft(), left);

		setLayout(layout.getRight(), right);

	}

	public Layout getUserLayout() {
		return new Layout(getLayoutPane(left), getLayoutPane(right));
	}

	@Override
	public void writeConfig(IrisConfig config) {
		config.setLayout(getUserLayout());
		setOpenTabsAndFiles(config);
	}

	private void setOpenTabsAndFiles(IrisConfig config) {
		TabPane editor = right.getTop();
		final int size = editor.getTabs().size();
		
		String[] openFiles = new String[size];
		
		for(int i = 0;i < size;i++) {
			ProjectFile file =  ((FileView)editor.getTabs().get(i)).getFile();
			openFiles[i] = file.getProject().getName() + "$$" + file.getPath().toString();
		}
		
		config.setOpenFiles(new OpenFiles(openFiles));
	}

	private LayoutPane getLayoutPane(IrisPane pane) {
		return new LayoutPane(getTabClasses(pane.getTop().getTabs()),
				getTabClasses(pane.getBottom().getTabs()));
	}

	private String[] getTabClasses(List<Tab> tabs) {
		if(tabs.isEmpty()) {
			return null;
		}

		String[] classes = new String[tabs.size()];

		for(int i = 0;i < classes.length;i++) {
			classes[i] = tabs.get(i).getClass().getName();
		}

		return classes;
	}

	private void setLayout(LayoutPane layout, IrisPane pane) {

		if(layout.getTop() != null) {
			for(String clazz : layout.getTop()) {
				addToPane(pane.getTop(), clazz);
			}
		}

		if(layout.getBottom() != null) {
			for(String clazz : layout.getBottom()) {
				addToPane(pane.getBottom(), clazz);
			}
		}

	}

	private void addToPane(ViewPane pane, String className) {
		Class<? extends View> clazz = getClassOfView(className);
		
		if(clazz.equals(FileView.class)) {
			// We need to associate a FileView with a file, so this is a special case
			addFileViewToPane(pane);
		} else {
			pane.addOrSelect(clazz);
		}
	}
	
	private void addFileViewToPane(ViewPane pane) {
		OpenFiles files = IrisStudio.getConfig().getOpenFiles();
		if(!files.hasNext()) {
			throw new IllegalStateException("FileView requested but no file to associate with");
		}
		
		Workspace ws = IrisStudio.getWorkspace();
		String openFileStr = files.pop();
		Project project = ws.getProject(openFileStr.substring(0, openFileStr.indexOf("$$")));
		ProjectFile file = project.getRoot().find(openFileStr.substring(openFileStr.indexOf("$$")+2));
				
		pane.addOrSelect(FileView.class, file);
	}

	@SuppressWarnings("unchecked")
	private Class<? extends View> getClassOfView(String name) {
		try {
			return (Class<? extends View>) Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
