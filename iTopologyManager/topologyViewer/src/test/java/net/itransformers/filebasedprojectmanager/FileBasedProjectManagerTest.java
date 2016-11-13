package net.itransformers.filebasedprojectmanager;

import de.svenjacobs.loremipsum.LoremIpsum;
import junit.framework.Assert;
import junit.framework.TestCase;
import net.itransformers.projectmanagerapi.ProjectManagerException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 11/13/16.
 */
public class FileBasedProjectManagerTest extends TestCase {

    FileBasedProjectManager fileBasedProjectManager;
    String projectName;

    @Before
    public void setup(){

        LoremIpsum loremIpsum = new LoremIpsum();
        projectName = loremIpsum.getWords(2);
        Map<String,String> properties = new HashMap<>();
        properties.put("baseDir", ".");
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();

        ctx.load("classpath:fileBasedProjectManager/fileBasedProjectManager.xml");
        FileBasedProjectManagerFactory fileBasedProjectManagerFactory = ctx.getBean("projectManagerFactory", FileBasedProjectManagerFactory.class);
        this.fileBasedProjectManager = fileBasedProjectManagerFactory.createProjectManager(properties);

    }
    @Test (expected=ProjectManagerException.class)
    public void testCreateProject() throws Exception {
        setup();
        try {
            fileBasedProjectManager.createProject(projectName, "projectTemplates/netTransformer.pfl");
        }catch (ProjectManagerException ex){

        }

    }
    @Test
    public void testGetProjectNames() throws Exception {
       String [] projectNames = fileBasedProjectManager.getProjectNames();
        boolean match = false;
        for (String name : projectNames) {
           if (projectName.equals("name")){
               match = true;
           }
        }
        Assert.assertTrue(match);


    }
    @Test
    public void testDeleteProject() throws Exception {
        fileBasedProjectManager.deleteProject(projectName);

    }
}