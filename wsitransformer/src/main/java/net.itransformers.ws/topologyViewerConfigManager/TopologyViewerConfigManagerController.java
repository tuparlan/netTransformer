package net.itransformers.ws.topologyViewerConfigManager;

import net.itransformers.topologyviewer.config.TopologyViewerConfigManager;
import net.itransformers.topologyviewer.config.TopologyViewerConfigManagerFactory;
import net.itransformers.topologyviewer.config.models.EdgeColorType;
import net.itransformers.topologyviewer.config.models.EdgeStrokeType;
import net.itransformers.topologyviewer.config.models.IconType;
import net.itransformers.utils.ProjectConstants;
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
@RequestMapping(value="/topology_viewer/config")
public class TopologyViewerConfigManagerController implements ServletContextAware {
    final Logger logger = LoggerFactory.getLogger(TopologyViewerConfigManagerController.class);

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
            props.put("projectType", ProjectConstants.snmpProjectType);
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


    @RequestMapping(value = "/images/{name}", method = RequestMethod.GET, produces = "image/png")
    public @ResponseBody byte[] getIcon(@PathVariable String name)  {
        try {
            InputStream is = this.getClass().getResourceAsStream("/images/"+name+".png");
            BufferedImage img = ImageIO.read(is);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bao);
            return bao.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
