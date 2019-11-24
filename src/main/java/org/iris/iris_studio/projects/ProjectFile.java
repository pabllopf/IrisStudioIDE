package org.iris.iris_studio.projects;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.iris.iris_studio.util.FileUtils;

public class ProjectFile {
	
	private final Project project;
	private ProjectFolder parent;
	private Path path;
	private FileType type;
	
	public ProjectFile(Project project, ProjectFolder parent, Path path, FileType type) {
		this.project = project;
		this.setParent(parent);
		this.path = path;
		this.type = type;
	}
	
	public Project getProject() {
		return project;
	}

	public ProjectFolder getParent() {
		return parent;
	}

	public void setParent(ProjectFolder parent) {
		this.parent = parent;
	}

	public Path getPath() {
		return path;
	}
	
	public String getName() {
		return path.getFileName().toString();
	}

	public void setPath(Path path) {
		Objects.requireNonNull(path);
		this.path = path;
	}
	
	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public String readAll() {
		return FileUtils.readAllText(path);
	}
	
	public List<String> readAllLines() {
		return FileUtils.readAllLines(path);
	}
	
	public void write(String contents) {
		FileUtils.write(path, contents);
	}
	
}
