package net.itransformers.projectmanagerapi;

import java.util.Map;

/**
 * Created by niau on 10/11/16.
 */
public interface ProjectManagerFactory {

   ProjectManagerAPI createProjectManager(Map<String, String> parameters);
}
