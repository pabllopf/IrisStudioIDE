package org.iris.iris_studio.gui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.TabPanePosition;
import org.iris.iris_studio.config.IrisConfig;
import org.iris.iris_studio.config.IrisConfig.FontInfo;
import org.iris.iris_studio.gui.ViewPane;
import org.iris.iris_studio.projects.ProjectFile;

import javafx.scene.text.Font;

public final class FileViews {
	
	private static final String DEFAULT_FONT_NAME = "Consolas";
	private static final double DEFAULT_FONT_SIZE = 12.0;
	
	private static Font font;
	private static String fontStyle;
	
	static {
		
		IrisConfig config = IrisStudio.getConfig();
		
		if(config.getFontInfo() != null) {
			font = Font.font(config.getFontInfo().getName(), config.getFontInfo().getSize());
		} else {
			font = Font.font(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE);
			config.setFontInfo(new FontInfo(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE));
		}
		
		fontStyle = bakeFontStyle();
		
		config.addConfigWriter(cf -> cf.setFontInfo(new FontInfo(font.getName(), font.getSize())));
	}

	public static Font getFont() {
		return font;
	}
	
	public static void setFont(String family, double size) {
		Objects.requireNonNull(family);
		Objects.requireNonNull(size);
		font = Font.font(family, size);
		fontStyle = bakeFontStyle();
		applyFontToAllFileViews();
	}
	
	public static void applyFont(FileView view) {
		view.getCodeArea().setStyle(fontStyle);
	}
	
	public static void applyFontToAllFileViews() {
		getAll().forEach(FileViews::applyFont);
	}

	public static void close(ProjectFile file) {
		get(file).ifPresent(view -> view.close());
	}
	
	public static List<FileView> getAll() {
		List<FileView> list = new ArrayList<>();
		for(TabPanePosition position : TabPanePosition.values()) {
			list.addAll(IrisStudio.getViewPaneAt(position).findAllOfClass(FileView.class));
 		}
		return list;
	}
	
	public static Optional<FileView> getSelected() {
		for(TabPanePosition position : TabPanePosition.values()) {
			Optional<FileView> selected = IrisStudio.getViewPaneAt(position).getSelected(FileView.class);
			if(selected.isPresent()) {
				return selected;
			}
 		}
		return Optional.empty();
	}
	
	public static FileView addOrSelect(ProjectFile file) {
		// First of all, search within every single tabpane
		// a fileview displaying the given file
		Optional<FileView> view = get(file);
		
		if(view.isPresent()) {
			return view.get();
		}
		// If not found, create a new one an attach it to the top-right pane
		return create(file);
	}
	
	public static Optional<FileView> get(ProjectFile file) {
		for(TabPanePosition position : TabPanePosition.values()) {
			FileView view = find(position, file);
			if(view != null) {
				return Optional.of(view);
			}
		}
		return Optional.empty();
	}

	private static FileView create(ProjectFile file) {
		FileView view = IrisStudio.getViewPaneAt(TabPanePosition.TOP_RIGHT)
			.addOrSelect(FileView.class, file);
		return view;
	}

	private static FileView find(TabPanePosition position, ProjectFile file) {
		ViewPane pane = IrisStudio.getViewPaneAt(position);
		List<FileView> views = pane.findAllOfClass(FileView.class);
		if(views == null) {
			return null;
		}
		return views.stream()
				.filter(view -> view.getFile().equals(file))
				.findAny().orElse(null);
	}
	
	private static String bakeFontStyle() {
		return String.format("-fx-font-name: %s; -fx-font-size: %dpx;",
				font.getName(), (int)font.getSize());
	}

}
