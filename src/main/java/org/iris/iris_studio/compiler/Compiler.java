package org.iris.iris_studio.compiler;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iris.iris_studio.gui.log.ErrorsView;
import org.iris.iris_studio.gui.log.LogView;
import org.iris.iris_studio.gui.projects.ProjectView;

import static org.iris.iris_studio.IrisStudio.*;

public class Compiler{
    private String log;
    private String nameProject;
    private String exePath;
    private String directoryToCompilePath;

    public Compiler()
    {
        log = "";
        nameProject = "";
        exePath = "";
        directoryToCompilePath = "";
    }

    public void run() {
        try {
            Runtime.getRuntime().exec("cmd /c start cmd.exe /k " + exePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean buildProject() {
        if(findView(ProjectView.class).get().getSelected().orElse(null) == null)
        {
            UpdateLog("Build Failed [select a project to build]");
            return false;
        }

        nameProject = findView(ProjectView.class).get().getSelected().get().getProject().getName();
        exePath = getConfig().getWorkspacePath() + "\\" + nameProject + ".exe";
        directoryToCompilePath = getConfig().getWorkspacePath() + "\\" + nameProject;

        if(compile(exePath, directoryToCompilePath)){
            UpdateLog("Build Successful");
            return true;
        }else {
            UpdateLog("Build Failed");
            return false;
        }
    }

    private void UpdateLog(String message) {
        log += message + " ";

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        log +=  "[" + dateFormat.format(date) + "]";

        findView(LogView.class).get().UpdateLog(log);
    }

    private boolean compile(String exePath, String directoryToCompile) {
        StringBuilder sb = new StringBuilder();

        sb.append(new File(getClass().getClassLoader().getResource("MinGW/bin/g++.exe").getFile()).getAbsolutePath());
        sb.append(" -I" + directoryToCompile + "\\include");
        sb.append(" -o" + exePath);

        List<String> filesToCompile = getAllFiles(directoryToCompile);
        for (String filePath : filesToCompile) {
            if(filePath.contains(".cpp") && !filePath.contains(".h.gch") && !filePath.contains(".h")) {
                sb.append(" " + filePath);
            }
        }
        return execInCmd(sb.toString());
    }

    private List<String> getAllFiles(String directoryToCompile) {
        List<String> output = new ArrayList<>();
        File[] files = new File(directoryToCompile).listFiles();
        if(files != null) {
            for(File file : files) {
                if(file.isDirectory()) {
                    output.addAll(getAllFiles(file.getAbsolutePath()));
                }else{
                    output.add(file.getAbsolutePath());
                }
            }
        }
        return output;
    }

    private boolean execInCmd(String command) {
        int counterError = 0;
        String line = "";
        findView(ErrorsView.class).get().clean();

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader is = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = is.readLine()) != null){
                findView(ErrorsView.class).get().addError(line);
                counterError++;
            }
        } catch (IOException e) {
            return false;
        }

        return (counterError <= 0) ? true : false;
    }
}



