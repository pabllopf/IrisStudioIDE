package org.iris.iris_studio.projects;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProjectFolder extends ProjectFile implements Iterable<ProjectFile> {
	
	private final List<ProjectFile> files;

	public ProjectFolder(Project project, ProjectFolder parent, Path path) {
		super(project, parent, path, FileType.DIRECTORY);
		files = new ArrayList<>();
	}
	
	public ProjectFile createFile(String name, FileType type) {
		ProjectFile file = new ProjectFile(getProject(), this, getPath().resolve(name), type);
		getProject().getFileSystem().createFile(file.getPath());
		files.add(file);
		return file;
	}
	
	public ProjectFolder createFolder(String name) {
		ProjectFolder folder = new ProjectFolder(getProject(), this, getPath().resolve(name));
		getProject().getFileSystem().createDirectory(folder.getPath());
		files.add(folder);
		return folder;
	}
		
	public void add(ProjectFile file) {
		files.add(file);
	}
	
	public boolean delete(ProjectFile file) {
		if(remove(file)) {
			getProject().getFileSystem().delete(file.getPath());
			return true;
		}
		return false;
	}
	
	public boolean remove(ProjectFile file) {
		return files.remove(file);
	}
	
	public boolean remove(String name) {
		ProjectFile file = find(name);
		if(file == null) {
			return findAndRemove(name);
		} 
		remove(file);
		return true;
	}
	
	private boolean findAndRemove(String name) {
		return files.stream()
		.filter(f -> f.getType() == FileType.DIRECTORY)
		.map(f -> (ProjectFolder)f)
		.anyMatch(f -> f.remove(name));
	}

	public ProjectFile find(String pathStr) {
		return find(Paths.get(pathStr));
	}

	public ProjectFile find(Path path) {
		for(ProjectFile file : files) {
			if(file.getPath().equals(path)) {
				return file;
			}
			
			if(file.getType() == FileType.DIRECTORY) {
				ProjectFolder folder = (ProjectFolder) file;
				ProjectFile result = folder.find(path);
				if(result != null) {
					return result;
				}
			}
			
		}
		
		return null;
	}
	
	public boolean exists(String name) {
		return find(name) != null;
	}

	@Override
	public Iterator<ProjectFile> iterator() {
		return files.iterator();
	}
	
}
