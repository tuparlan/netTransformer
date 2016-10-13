package net.itransformers.topologyviewer.config;

import net.itransformers.topologyviewer.config.models.EdgeColorType;
import net.itransformers.topologyviewer.config.models.EdgeStrokeType;
import net.itransformers.topologyviewer.config.models.IconType;
import net.itransformers.topologyviewer.config.models.TopologyViewerConfType;

import java.util.List;

/**
 * Created by vasko on 10/13/2016.
 */
public interface TopologyViewerConfigManager {
    TopologyViewerConfType getTopologyViewerConfType();
    List<IconType> getIcons();
    List<EdgeStrokeType> getEdgeStrokes();
    List<EdgeColorType> getEdgeColors();
}
