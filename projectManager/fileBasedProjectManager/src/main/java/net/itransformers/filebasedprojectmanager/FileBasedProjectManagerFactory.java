package net.itransformers.filebasedprojectmanager;
import net.itransformers.projectmanagerapi.ProjectManagerFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by niau on 10/11/16.
 */
public class FileBasedProjectManagerFactory implements ProjectManagerFactory {

    @Override
    public FileBasedProjectManager createProjectManager (Map<String, String> parameters) {
        if (!parameters.containsKey("baseDir")) {
            throw new IllegalArgumentException("Parameter 'baseDir' is no provided for the ProjectManagerFactory");
        }
        String baseDirStr = parameters.get("baseDir");
        File baseDir = new File(baseDirStr);
        FileBasedProjectManager fileBasedProjectManager = new FileBasedProjectManager(baseDir);

        return fileBasedProjectManager;
    }
}
