package net.itransformers.graphmlDiffAPI;

import java.util.Map;

/**
 * Created by niau on 10/19/16.
 */
public interface GraphmlDifferFactory {

  GraphmlDiffer createGraphmlDiffer (Map<String,Object> properties);
}
