package org.iris.iris_studio.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.util.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public final class IrisConfig {
	
	private static final File CONFIG_FILE = new File(IrisConfig.class.getResource("/config.json").getFile());
	
	private final ConfigProperties properties;
	private final Collection<IrisConfigWriter> writers;
	
	public IrisConfig(IrisStudio app) {
		// Only IrisStudio can instanciate this class
		Objects.requireNonNull(app);
		
		properties = ConfigProperties.load();
		writers = new ArrayList<>();
	}
	
	public void addConfigWriter(IrisConfigWriter writer) {
		writers.add(writer);
	}
	
	public void removeConfigWriter(IrisConfigWriter writer) {
		writers.remove(writer);
	}
	
	public Layout getLayout() {
		return properties.layout;
	}
	
	public void setLayout(Layout layout) {
		properties.layout = layout;
	}
	
	public String getWorkspacePath() {
		return properties.workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		properties.workspacePath = workspacePath;
	}

	public Boolean getAskWorkspacePath() {
		return properties.askWorkspacePath;
	}

	public void setAskWorkspacePath(Boolean askWorkspacePath) {
		properties.askWorkspacePath = askWorkspacePath;
	}
	
	public OpenFiles getOpenFiles() {
		return properties.openFiles;
	}

	public void setOpenFiles(OpenFiles openFiles) {
		properties.openFiles = openFiles;
	}

	public OpenProjects getOpenProjects() {
		return properties.openProjects;
	}

	public void setOpenProjects(OpenProjects openProjects) {
		properties.openProjects = openProjects;
	}
	
	public String getTheme() {
		return properties.theme;
	}
	
	public void setTheme(String theme) {
		properties.theme = theme;
	}
	
	public FontInfo getFontInfo() {
		return properties.font;
	}
	
	public void setFontInfo(FontInfo fontInfo) {
		properties.font = fontInfo;
	}

	public void save() {
		for(IrisConfigWriter writer : writers) {
			writer.writeConfig(this);
		}
		writeConfig();
	}
	
	private void writeConfig() {
		FileUtils.write(CONFIG_FILE.toString(),
				new Gson().toJson(properties));
	}
	
	private static final class ConfigProperties {
		
		private static ConfigProperties load() {
			System.out.println("Loading configuration properties...");
			String json = FileUtils.readAllText(CONFIG_FILE.toString());
			debugPrintJson(json);
			return new Gson().fromJson(json, ConfigProperties.class);
		}
		
		private static void debugPrintJson(String json) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			System.out.println(gson.toJson(JsonParser.parseString(json)));
		}

		private Layout layout;
		private OpenFiles openFiles;
		private OpenProjects openProjects;
		private String workspacePath;
		private Boolean askWorkspacePath;
		private String theme;
		private FontInfo font;
		
	}
	
	public static class FontInfo {
		
		private final String name;
		private final double size;
		
		public FontInfo(String name, double size) {
			this.name = name;
			this.size = size;
		}
		
		public String getName() {
			return name;
		}
		
		public double getSize() {
			return size;
		}
		
	}

	public static final class Layout {
		
		private final LayoutPane left;
		private final LayoutPane right;
		
		public Layout(LayoutPane left, LayoutPane right) {
			this.left = left;
			this.right = right;
		}
		
		public LayoutPane getLeft() {
			return left;
		}

		public LayoutPane getRight() {
			return right;
		}

		public static final class LayoutPane {
			
			private final String[] top;
			private final String[] bottom;
			
			public LayoutPane(String[] top, String[] bottom) {
				this.top = top;
				this.bottom = bottom;
			}

			public String[] getTop() {
				return top;
			}

			public String[] getBottom() {
				return bottom;
			}
			
		}	
		
	}
	
	public static class OpenFiles {
		
		private final String[] filePaths;
		private transient int index = 0;

		public OpenFiles(String[] filePaths) {
			this.filePaths = filePaths;
		}

		public String[] getFilePaths() {
			return filePaths;
		}
		
		public boolean isEmpty() {
			return filePaths == null || filePaths.length == 0;
		}
		
		public int getIndex() {
			return index;
		}
		
		public void reset() {
			index = 0;
		}
		
		public boolean hasNext() {
			return index < filePaths.length;
		}
		
		public String pop() {
			return filePaths[index++];
		}
		
	}
	
	public static class OpenProjects {
		
		private final String[] openProjects;

		public OpenProjects(String[] openProjects) {
			this.openProjects = openProjects;
		}

		public String[] getOpenProjects() {
			return openProjects;
		}

	}

}
