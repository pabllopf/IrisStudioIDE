package org.iris.iris_studio.projects;

import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;

import org.iris.iris_studio.projects.io.FileSystem;

public class Workspace {
	
	private static final String CONFIG_FILENAME = ".worskpace";
		
	private final FileSystem fileSystem;
	private final Path configFilePath;
	private final SortedMap<String, Project> projects;

	public Workspace(FileSystem fs) {
		fileSystem = fs;
		configFilePath = fs.resolve(CONFIG_FILENAME);
		
		if(!fs.exists(configFilePath)) {
			fs.createFile(configFilePath);
		}
		
		projects = new TreeMap<>();
		
		loadProjects();
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}
	
	public Iterable<Project> getProjects() {
		return projects.values();
	}
	
	public Project createProject(String name, boolean helloWorld) {
		Project project = Project.newProject(this, name, helloWorld);
		projects.put(name, project);
		updateWorkspaceFile();
		return project;
	}
	
	public void addProject(String name) {
		Project project = Project.openProject(this, name);
		projects.put(name, project);
	}
	
	public void deleteProject(String name) {
		fileSystem.delete(projects.get(name).getPath());
		projects.remove(name);
		updateWorkspaceFile();
	}
	
	public boolean contains(String name) {
		return projects.containsKey(name);
	}
	
	public Project getProject(String name) {
		return projects.get(name);
	}
	
	private void updateWorkspaceFile() {
		if(!fileSystem.exists(configFilePath)) {
			fileSystem.createFile(configFilePath);
		}
		
		fileSystem.writeLines(configFilePath, projects.values(), Project::getName);
	}
	
	private void loadProjects() {
		fileSystem.readLines(configFilePath).forEach(this::addProject);
	}
	
}
