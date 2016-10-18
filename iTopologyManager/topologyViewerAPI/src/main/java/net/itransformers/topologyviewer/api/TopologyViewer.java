package net.itransformers.topologyviewer.api;

import net.itransformers.topologyviewer.api.models.Graph;

/**
 * Created by pod on 10/14/16.
 */
public interface TopologyViewer {
    Graph getGraph(String version);
    Graph getGraph(String version, String vertexFilterName, String edgeFilterName);
}
