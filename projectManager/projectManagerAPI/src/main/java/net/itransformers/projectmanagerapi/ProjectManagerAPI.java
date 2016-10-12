package net.itransformers.projectmanagerapi;

/**
 * Created by niau on 10/11/16.
 */


public interface ProjectManagerAPI  {

    void createProject(String projectTemplate,String projectPath) throws ProjectManagerException;
    void deleteProject(String projectPath) throws ProjectManagerException;

}
