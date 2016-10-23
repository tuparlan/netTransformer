package net.itransformers.projectmanagerapi;

/**
 * Created by niau on 10/11/16.
 */


public interface ProjectManagerAPI  {

    void createProject(String projectName, String projectTemplate) throws ProjectManagerException;
    String[] getProjectNames();
    void deleteProject(String projectName) throws ProjectManagerException;

}
