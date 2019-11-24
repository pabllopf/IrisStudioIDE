package org.iris.iris_studio.projects.io;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FileSystem {
	
	protected static final String CHARSET_NAME = "UTF-8";
	
	private Path root;
	
	public FileSystem(Path root) {
		Objects.requireNonNull(root);
		this.root = root;
	}
	
	public final Path getRoot() {
		return root;
	}
	
	public final Path resolve(Path path) {
		return root.resolve(path);
	}
	
	public final Path resolve(String path) {
		return root.resolve(path);
	}
	
	public abstract boolean exists(Path path);
	
	public abstract boolean createFile(Path path);
	
	public abstract boolean createDirectory(Path path);
	
	public abstract boolean delete(Path path);
		
	public abstract String read(Path path);
	
	public abstract List<String> readLines(Path path);
	
	public abstract void write(Path path, String contents);
	
	public abstract void writeLines(Path path, Iterable<String> lines);
	
	public abstract void writeLines(Path path, String... lines);
	
	public abstract <T> void writeLines(Path path, Iterable<T> objects, Function<? super T, String> stringSupplier);
	
	public abstract void copy(Path source, Path destination);
}
