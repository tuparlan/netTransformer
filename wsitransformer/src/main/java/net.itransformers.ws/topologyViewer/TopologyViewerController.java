package net.itransformers.ws.topologyViewer;

import net.itransformers.topologyviewer.api.TopologyViewer;
import net.itransformers.topologyviewer.api.TopologyViewerFactory;
import net.itransformers.topologyviewer.api.models.Graph;
import net.itransformers.topologyviewer.config.TopologyViewerConfigManager;
import net.itransformers.topologyviewer.config.TopologyViewerConfigManagerFactory;
import net.itransformers.topologyviewer.config.models.EdgeColorType;
import net.itransformers.topologyviewer.config.models.EdgeStrokeType;
import net.itransformers.topologyviewer.config.models.IconType;
import net.itransformers.ws.resourceManager.ResourceManagerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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
            topologyViewer = topologyViewerFactory.createTopologyViewer("discovery", props);
            context.setAttribute("topologyViewer", topologyViewer);
        }
        return topologyViewer;
    }
    @RequestMapping(value = "/{version}/graph", method = RequestMethod.GET)
    @ResponseBody
    public Graph getGraph(@PathVariable String version) {
        return getTopologyViewer().getGraph(version);
    }

}
