package net.itransformers.ws.topologyViewer;

import net.itransformers.topologyviewer.api.TopologyViewer;
import net.itransformers.topologyviewer.api.TopologyViewerFactory;
import net.itransformers.topologyviewer.api.models.Graph;
import net.itransformers.utils.ProjectConstants;
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
 * Created by vasko on 10/13/2016.
 */

@Controller
@RequestMapping(value="/topology_viewer")
public class TopologyViewerController implements ServletContextAware {
    final Logger logger = LoggerFactory.getLogger(TopologyViewerController.class);

    @Resource(name="discoveryResultTopologyViewerFactory")
    private TopologyViewerFactory topologyViewerFactory;

    @Resource(name="projectPath")
    private String projectPath;

    private ServletContext context;

    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    private TopologyViewer getTopologyViewer(){
        TopologyViewer topologyViewer =
                (TopologyViewer) context.getAttribute("topologyViewer");
        if (topologyViewer == null) {
            Map<String, String> props = new HashMap<>();
            props.put("projectPath", projectPath);
            props.put("projectType", ProjectConstants.snmpProjectType);
            topologyViewer = topologyViewerFactory.createTopologyViewer("discovery", props);
            context.setAttribute("topologyViewer", topologyViewer);
        }
        return topologyViewer;
    }
    @RequestMapping(value = "/{version}/graph", method = RequestMethod.GET)
    @ResponseBody
    public Graph getGraph(@PathVariable String version, @RequestParam(required = false) String vertexFilterName, @RequestParam(required = false) String edgeFilterName) {
        return getTopologyViewer().getGraph(version, vertexFilterName, edgeFilterName);
    }

}
