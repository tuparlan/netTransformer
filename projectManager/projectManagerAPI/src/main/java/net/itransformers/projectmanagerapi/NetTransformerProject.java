package net.itransformers.projectmanagerapi;

/**
 * Created by niau on 10/11/16.
 */
public class NetTransformerProject {
    String projectName;
    String projectType;
    String projectPath;

    public NetTransformerProject(String projectName, String projectType, String projectPath) {
        this.projectName = projectName;
        this.projectType = projectType;
        this.projectPath = projectPath;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
