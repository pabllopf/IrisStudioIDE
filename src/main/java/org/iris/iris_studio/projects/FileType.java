package org.iris.iris_studio.projects;

public enum FileType {
	SOURCE,
	HEADER,
	RESOURCE,
	DIRECTORY;
	
	/**
	 * Get the corresponding FileType for a given file extension.
	 * This method does not work for directory files.
	 * 
	 *@param extension the file extension
	 *
	 *@return the {@code FileType} for the extension
	 *  
	 * */
	public static FileType getTypeForExtension(String extension) {
		switch(extension) {
		case ".cpp":
		case ".cc":
		case ".c":
			return FileType.SOURCE;
		case ".h":
		case ".hh":
		case ".hpp":
			return FileType.HEADER;
		case "":
			return FileType.RESOURCE;
		default:
			return null;
		}
	}
	
}
