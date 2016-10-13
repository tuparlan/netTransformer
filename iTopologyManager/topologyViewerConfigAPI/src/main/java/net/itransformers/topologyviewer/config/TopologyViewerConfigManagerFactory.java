package net.itransformers.topologyviewer.config;

import java.util.Map;

/**
 * Created by vasko on 10/13/2016.
 */
public interface TopologyViewerConfigManagerFactory {
    TopologyViewerConfigManager createTopologyViewerConfigManager(String type, Map<String, String> properties);
}
