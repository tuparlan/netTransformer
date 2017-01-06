package net.itransformers.filebasedprojectmanager;

import net.itransformers.projectmanagerapi.ProjectManagerException;
import net.itransformers.utils.ProjectConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 11/13/16.
 */

public class FileBasedProjectManagerTest {

    private FileBasedProjectManager fileBasedProjectManager;
    private   String projectName;

    @Before
    public void setup() {
        String baseDir = System.getProperty("base.dir");
        if (baseDir==null){
            baseDir=".";
        }
        System.out.println("BaseDir:"+new File(baseDir).getAbsolutePath());

        Map<String,String> properties = new HashMap<>();
        String projectDir = new File(baseDir,"projectManager/fileBasedProjectManager/src/test/resources").getAbsolutePath();

        properties.put("baseDir", baseDir);
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.refresh();

        ctx.load("classpath:fileBasedProjectManager/fileBasedProjectManager.xml");
        FileBasedProjectManagerFactory fileBasedProjectManagerFactory = ctx.getBean("projectManagerFactory", FileBasedProjectManagerFactory.class);
        fileBasedProjectManager = fileBasedProjectManagerFactory.createProjectManager(properties);
        projectName = fileBasedProjectManager.randomProjectNameGenerator(projectDir);
    }
    @Test
    public void testCreateNetworkDiscoveryProject() throws Exception {
        fileBasedProjectManager.createProject(projectName, ProjectConstants.snmpProjectType);

        Assert.assertTrue(testGetProjectNames());

       // Assert.assertTrue(testDeleteProject());

    }

    @Test
    public void testCreateBgpMapProject() throws Exception {
        fileBasedProjectManager.createProject(projectName, ProjectConstants.bgpDiscovererProjectType);

        Assert.assertTrue(testGetProjectNames());
        Assert.assertTrue(testDeleteProject());

    }

    @Test
    public void testCreateFreeGraphProject() throws Exception {
        fileBasedProjectManager.createProject(projectName, ProjectConstants.freeGraphProjectType);

        Assert.assertTrue(testGetProjectNames());
        Assert.assertTrue(testDeleteProject());

    }
    public boolean testGetProjectNames() throws Exception {
        String [] projectNames = fileBasedProjectManager.getProjectNames();
        for (String name : projectNames) {
            if (projectName.equals(name)){
                return true;
            }
        }
        return false;
    }
    public boolean testDeleteProject() throws Exception {

        try {
            fileBasedProjectManager.deleteProject(projectName);
        }catch (ProjectManagerException ex){
            return false;
        }
        return true;

    }
}