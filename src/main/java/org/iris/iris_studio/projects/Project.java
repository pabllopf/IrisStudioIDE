package org.iris.iris_studio.projects;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import org.iris.iris_studio.projects.io.FileSystem;
import org.iris.iris_studio.util.FileUtils;

public class Project {
	
	public static final String PROJECT_FILENAME = ".project";

	static Project newProject(Workspace workspace, String name, boolean helloWorld) {
		Project project = new Project(workspace, name);
		project.setup(helloWorld);
		return project;
	}

	static Project openProject(Workspace workspace, String name) {
		Project project = new Project(workspace, name);
		project.scanAllFiles();
		return project;
	}

	private final ProjectFolder root;
	private final Workspace workspace;

	private Project(Workspace worspace, String name) {
		this.workspace = worspace;
		this.root = new ProjectFolder(this, null, workspace.getFileSystem().resolve(name));
	}

	private Project(Workspace workspace, String name, boolean helloWorld) {
		Objects.requireNonNull(workspace);
		this.workspace = workspace;
		Path path = workspace.getFileSystem().resolve(name);
		workspace.getFileSystem().createDirectory(path);
		root = new ProjectFolder(this, null, path);
		setup(helloWorld);
	}
	
	public Path getPath() {
		return root.getPath();
	}

	public ProjectFolder getRoot() {
		return root;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public FileSystem getFileSystem() {
		return workspace.getFileSystem();
	}

	public String getName() {
		return getPath().getFileName().toString();
	}

	private void scanAllFiles() {
		scanAllChildren(root, root.getPath().toFile());
	}

	private void scanAllChildren(ProjectFolder root, File rootFile) {
		for(File file : rootFile.listFiles()) {
			FileType type = file.isDirectory() 
					? FileType.DIRECTORY
					: FileType.getTypeForExtension(FileUtils.getFileExtension(file));
			
			ProjectFile projectFile = type == FileType.DIRECTORY 
					? new ProjectFolder(this, root, root.getPath().resolve(file.getName()))
					: new ProjectFile(this, root, root.getPath().resolve(file.getName()), type);
					
			root.add(projectFile);
					
			if(file.isDirectory()) {
				scanAllChildren((ProjectFolder) projectFile, file);
			}
		}
	}

	private void setup(boolean helloWorld) {
		FileSystem fs = workspace.getFileSystem();
		fs.createDirectory(getPath());
		root.createFile(PROJECT_FILENAME, FileType.RESOURCE);
		if(helloWorld) {
			createHelloWorld();
		}
	}

	private void createHelloWorld() {
		ProjectFolder include = root.createFolder("include");
		include.createFile("header.h", FileType.HEADER);
		String headerName =  getName().toUpperCase().replaceAll(" ", "_") +"_HEADER_H";
		getFileSystem().writeLines(include.getPath().resolve("header.h"), 
				"#ifndef " + headerName,
				"#define " + headerName,
				"",
				"#endif");

		ProjectFolder src = root.createFolder("src");
		src.createFile("main.cpp", FileType.SOURCE);
		getFileSystem().writeLines(src.getPath().resolve("main.cpp"),
				"#include <iostream>",
				"#include \"header.h\"",
				"\n",
				"int main()",
				"{\n",
				"\tstd::cout << \"Hello World!\" << std::endl;",
				"\n\treturn 0;",
				"\n}\n");
	}

}
