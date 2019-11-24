package org.iris.iris_studio.gui.menus;

import org.iris.iris_studio.projects.FileType;
import org.iris.iris_studio.projects.ProjectFile;
import org.iris.iris_studio.projects.ProjectFolder;
import org.iris.iris_studio.util.FileUtils;

public class NewFileDialog extends NewDialogBase {
	
	public NewFileDialog() {
		super("New File", "Create new file", null);
	}

	@Override
	protected ProjectFile getLastFile(ProjectFolder folder, String name) {
		FileType type = FileType.getTypeForExtension(FileUtils.getFileExtension(name));
		ProjectFile file = folder.createFile(name, type);
		if(type == FileType.HEADER) {
			fillWithHeaderGuard(file);
		}
		return file;
	}
	
	private void fillWithHeaderGuard(ProjectFile headerFile) {
		String fileName = headerFile.getName().toUpperCase().replaceAll("\\.", "_");
		String projectName = headerFile.getProject().getName().toUpperCase();
		String guardName = projectName + '_' + fileName;
		
		headerFile.write("#ifndef " + guardName + "\n#define " + guardName + "\n\n#endif");
	}

}
