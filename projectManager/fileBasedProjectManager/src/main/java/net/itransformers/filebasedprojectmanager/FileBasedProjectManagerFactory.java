package net.itransformers.filebasedprojectmanager;

import net.itransformers.projectmanagerapi.ProjectManagerAPI;
import net.itransformers.projectmanagerapi.ProjectManagerFactory;

/**
 * Created by niau on 10/11/16.
 */
public class FileBasedProjectManagerFactory implements ProjectManagerFactory {

    String projectTemplate;

    public FileBasedProjectManagerFactory(String projectTemplate) {
        this.projectTemplate = projectTemplate;
    }

    public String getProjectTemplate() {
        return projectTemplate;
    }

    public void setProjectTemplate(String projectTemplate) {
        this.projectTemplate = projectTemplate;
    }

    @Override
    public ProjectManagerAPI createProjectManager (String projectPath) {
        FileBasedProjectManager fileBasedProjectManager = new FileBasedProjectManager();
        return fileBasedProjectManager;
    }
}
