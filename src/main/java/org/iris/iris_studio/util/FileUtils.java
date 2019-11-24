package org.iris.iris_studio.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtils {
	
	private FileUtils() {}
	
	public static String getFileExtension(File file) {
	    return getFileExtension(file.getName());
	}
	
	public static String getFileExtension(String filename) {
	    final int index = filename.lastIndexOf(".");
	    if(index <= 0) {
	        return "";
	    }
	    return filename.substring(index);
	}
	
	public static String readAllText(String path) {
		return readAllText(Paths.get(path));
	}
	
	public static String readAllText(Path path) {
		try {
			return new String(Files.readAllBytes(path), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> readAllLines(String path) {
		return readAllLines(Paths.get(path));
	}
	
	public static List<String> readAllLines(Path path) {
		try {
			return Files.readAllLines(path, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void write(String path, String contents) {
		write(Paths.get(path), contents);
	}
	
	public static void write(Path path, String contents) {
		try {
			Files.write(path, contents.getBytes(Charset.forName("UTF-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
