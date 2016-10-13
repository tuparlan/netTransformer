package net.itransformers.topologyviewer.gui;

import edu.uci.ics.jung.graph.Graph;
import net.itransformers.topologyviewer.config.models.TopologyViewerConfType;
import net.itransformers.topologyviewer.rightclick.RightClickInvoker;

import java.io.File;

/**
 * Created by vasko on 9/14/2016.
 */
public class GraphViewerPanelFactory {
    private RightClickInvoker rightClickInvoker;

    public GraphViewerPanelFactory(RightClickInvoker rightClickInvoker ) {
        this.rightClickInvoker = rightClickInvoker;
    }

    public <G extends Graph<String, String>> GraphViewerPanel createGraphViewerPanel(TopologyManagerFrame topologyManagerFrame,
                                                                                     TopologyViewerConfType viewerConfig,
                                                                                     GraphmlLoader<G> graphmlLoader,
                                                                                     IconMapLoader iconMapLoader,
                                                                                     EdgeStrokeMapLoader edgeStrokeMapLoader,
                                                                                     EdgeColorMapLoader edgeColorMapLoader,
                                                                                     G entireGraph,
                                                                                     File projectPath,
                                                                                     File deviceXmlPath,
                                                                                     File versionDir,
                                                                                     File graphmlFileName,
                                                                                     String initialNode,
                                                                                     String layout){
        return new GraphViewerPanel<G>(topologyManagerFrame,
                viewerConfig,
                graphmlLoader,
                iconMapLoader,
                edgeStrokeMapLoader,
                edgeColorMapLoader,
                entireGraph,
                projectPath,
                deviceXmlPath,
                versionDir,
                graphmlFileName,
                initialNode,
                layout,
                rightClickInvoker);

    }
}
