package net.itransformers.graphmlDiffAPI;

import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;

/**
 * Created by niau on 10/19/16.
 */
public interface GraphmlDiffer {
    GraphmlGraph doDiff (GraphmlGraph graphA,GraphmlGraph graphB);
}
