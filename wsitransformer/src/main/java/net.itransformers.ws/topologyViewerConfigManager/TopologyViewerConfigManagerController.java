package net.itransformers.ws.topologyViewerConfigManager;

import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.ResourceManagerFactory;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.topologyviewer.config.TopologyViewerConfigManager;
import net.itransformers.topologyviewer.config.TopologyViewerConfigManagerFactory;
import net.itransformers.topologyviewer.config.models.EdgeColorType;
import net.itransformers.topologyviewer.config.models.EdgeStrokeType;
import net.itransformers.topologyviewer.config.models.IconType;
import net.itransformers.ws.resourceManager.ResourceManagerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vasko on 10/13/2016.
 */

@Controller
@RequestMapping(value="/topology_viewer/config")
public class TopologyViewerConfigManagerController implements ServletContextAware {
    final Logger logger = LoggerFactory.getLogger(ResourceManagerController.class);

    @Resource(name="topologyViewerConfigManagerFactory")
    private TopologyViewerConfigManagerFactory topologyViewerConfigManagerFactory;


    @Resource(name="projectPath")
    private String projectPath;

    private ServletContext context;

    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    private TopologyViewerConfigManager getTopologyViewerConfigManager(){
        TopologyViewerConfigManager topologyViewerConfigManager =
                (TopologyViewerConfigManager) context.getAttribute("topologyViewerConfigManager");
        if (topologyViewerConfigManager == null) {
            Map<String, String> props = new HashMap<>();
            props.put("projectPath", projectPath);
            props.put("name", "discovery");
            topologyViewerConfigManager = topologyViewerConfigManagerFactory.createTopologyViewerConfigManager("xml", props);
            context.setAttribute("topologyViewerConfigManager", topologyViewerConfigManager);
        }
        return topologyViewerConfigManager;
    }
    @RequestMapping(value = "/icons", method = RequestMethod.GET)
    @ResponseBody
    public List<IconType> getIcons() {
        return getTopologyViewerConfigManager().getIcons();
    }
    @RequestMapping(value = "/edge/strokes", method = RequestMethod.GET)
    @ResponseBody
    public List<EdgeStrokeType> getEdgeStrokes() {
        return getTopologyViewerConfigManager().getEdgeStrokes();
    }
    @RequestMapping(value = "/edge/colors", method = RequestMethod.GET)
    @ResponseBody
    public List<EdgeColorType> getEdgeColors() {
        return getTopologyViewerConfigManager().getEdgeColors();
    }
}
