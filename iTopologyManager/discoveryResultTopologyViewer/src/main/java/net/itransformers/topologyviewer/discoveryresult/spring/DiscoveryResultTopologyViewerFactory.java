package net.itransformers.topologyviewer.discoveryresult.spring;

import net.itransformers.topologyviewer.api.TopologyViewer;
import net.itransformers.topologyviewer.api.TopologyViewerFactory;
import net.itransformers.topologyviewer.config.TopologyViewerConfigManagerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.Map;

/**
 * Created by vasko on 9/30/2016.
 */
public class DiscoveryResultTopologyViewerFactory implements TopologyViewerFactory {
    private TopologyViewerConfigManagerFactory topologyViewerConfigManagerFactory;

    @Override
    public TopologyViewer createTopologyViewer(String name, Map<String, String> properties) {
        TopologyViewer topologyViewer = createTopologyViewer(properties);
        return topologyViewer;
    }

    private TopologyViewer createTopologyViewer(Map<String, String> properties) {
        String projectPath = properties.get("projectPath");
        if (projectPath == null) {
            throw new IllegalArgumentException("Missing projectPath parameter");
        }
        return createTopologyViewer(projectPath);
    }

    private TopologyViewer createTopologyViewer(String projectPath) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:discoveryResultTopologyViewer/discoveryResultTopologyViewer.xml");
        ctx.load("classpath:xmlTopologyViewerConfig/xmlTopologyViewerConfig.xml");
        ctx.load("classpath:xmlNodeDataProvider/xmlNodeDataProvider.xml");
        AbstractBeanDefinition projectPathBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(String.class)
                .addConstructorArgValue(projectPath).getBeanDefinition();

        ctx.registerBeanDefinition("projectPath", projectPathBeanDefinition);
        ctx.refresh();

        TopologyViewer topologyViewer = ctx.getBean("discoveryResultTopologyViewer", TopologyViewer.class);

        return  topologyViewer;

    }

}
