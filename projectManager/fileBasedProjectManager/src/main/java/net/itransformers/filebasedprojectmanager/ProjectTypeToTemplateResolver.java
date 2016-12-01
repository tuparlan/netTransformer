package net.itransformers.filebasedprojectmanager;

import net.itransformers.utils.ProjectConstants;

/**
 * Created by niau on 11/13/16.
 */
public class ProjectTypeToTemplateResolver {

   public static String getProjectTemplate(String projectType) {
       if (ProjectConstants.snmpProjectType.equals(projectType)) {
           return "projectTemplates/netTransformer.pfl";
       } else if (ProjectConstants.freeGraphProjectType.equals(projectType)) {
           return "projectTemplates/freeGraph.pfl";

       } else if (ProjectConstants.bgpDiscovererProjectType.equals(projectType)) {
           return "projectTemplates/bgpPeeringMap.pfl";
       }
       else
           return null;
   }
}
