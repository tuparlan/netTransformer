package net.itransformers.ws.projectManager;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.projectmanagerapi.ProjectManagerAPI;
import net.itransformers.projectmanagerapi.ProjectManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 9/23/16.
 */
@Controller
@RequestMapping(value="/project")
public class ProjectManagerController implements ServletContextAware {
    
    final Logger logger = LoggerFactory.getLogger(ProjectManagerController.class);

    @Resource(name="projectManagerFactory")
    private ProjectManagerFactory projectManagerFactory;
    
    @Resource(name="baseDir")
    private String baseDir;

    
    private ServletContext context;

    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    private ProjectManagerAPI getProjectManager(){
        ProjectManagerAPI projectManager =
                (ProjectManagerAPI) context.getAttribute("projectManager");
        if (projectManager == null) {
            Map<String, String> props = new HashMap<>();
            props.put("baseDir", baseDir);
            projectManager = projectManagerFactory.createProjectManager(props);
            context.setAttribute("projectManager", projectManager);
        }
        return projectManager;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String[] getProjectNames() {
        return getProjectManager().getProjectNames();
    }

    @RequestMapping(value = "/", method=RequestMethod.POST)
    @ResponseBody
    public void createProject(@RequestBody String projectName){
        // TODO hardcoded project template
        String projectTemplate = "";
        getProjectManager().createProject(projectName, projectTemplate);
    }

    @RequestMapping(value = "/{projectName}", method=RequestMethod.DELETE)
    @ResponseBody
    public void get(@PathVariable String projectName) {
        getProjectManager().deleteProject(projectName);
    }
}
