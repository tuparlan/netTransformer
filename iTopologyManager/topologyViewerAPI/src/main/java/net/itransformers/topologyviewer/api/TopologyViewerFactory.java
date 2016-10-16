package net.itransformers.topologyviewer.api;

import java.util.Map;

/**
 * Created by pod on 10/16/16.
 */
public interface TopologyViewerFactory {
    TopologyViewer createTopologyViewer(String name, Map<String, String> properties);
}
