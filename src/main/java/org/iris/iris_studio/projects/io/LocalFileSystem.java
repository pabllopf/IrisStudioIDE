package org.iris.iris_studio.projects.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalFileSystem extends FileSystem {
	
	private final Logger logger = Logger.getLogger(LocalFileSystem.class.getName());

	public LocalFileSystem(Path root) {
		super(root);
	}

	@Override
	public boolean exists(Path path) {
		return resolve(path).toFile().exists();
	}

	@Override
	public boolean createFile(Path path) {
		try {
			Files.createFile(path);
			return true;
		} catch (IOException e) {
			logError(e);
		}
		return false;
	}

	@Override
	public boolean createDirectory(Path path) {
		try {
			Files.createDirectory(path);
			return true;
		} catch (IOException e) {
			logError(e);
		}
		return false;
	}

	@Override
	public boolean delete(Path path) {
		try {
			if(Files.exists(path)) {
				
				if(!path.toFile().isDirectory()) {
					Files.delete(path);
					
				} else {
					
					Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			            @Override
			            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
			                Files.delete(path);
			                return FileVisitResult.CONTINUE;
			            }

			            @Override
			            public FileVisitResult postVisitDirectory(Path directory, IOException ioException) throws IOException {
			                Files.delete(directory);
			                return FileVisitResult.CONTINUE;
			            }
			        });
				}
				
			}
		} catch (IOException e) {
			logError(e);
		}
		return false;
	}

	@Override
	public String read(Path path) {
		try {
			return new String(Files.readAllBytes(path), Charset.forName(CHARSET_NAME));
		} catch (IOException e) {
			logError(e);
		}
		return null;
	}

	@Override
	public List<String> readLines(Path path) {
		try {
			return Files.readAllLines(path);
		} catch (IOException e) {
			logError(e);
		}
		return null;
	}

	@Override
	public void write(Path path, String contents) {
		try {
			Files.write(path, contents.getBytes(Charset.forName(CHARSET_NAME)));
		} catch (IOException e) {
			logError(e);
		}
	}

	@Override
	public void writeLines(Path path, Iterable<String> lines) {
		try {
			Files.write(path, lines, Charset.forName(CHARSET_NAME));
		} catch (IOException e) {
			logError(e);
		}
	}

	@Override
	public void writeLines(Path path, String... lines) {
		writeLines(path, Arrays.asList(lines));
	}
	
	@Override
	public <T> void writeLines(Path path, Iterable<T> objects, Function<? super T, String> stringSupplier) {
		List<String> lines = new ArrayList<>();
		objects.forEach(obj -> lines.add(stringSupplier.apply(obj)));
		writeLines(path, lines);
	}
	
	@Override
	public void copy(Path source, Path destination) {
		try {
			Files.copy(source, destination);
		} catch (IOException e) {
			logError(e);
		}
	}
	
	private void logError(Exception e) {
		logger.log(Level.SEVERE, e.getMessage(), e);
	}

}
