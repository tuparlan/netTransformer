package net.itransformers.topologyviewer.discoveryresult;

import net.itransformers.topologyviewer.config.models.DataMatcherType;
import net.itransformers.topologyviewer.config.models.TopologyViewerConfType;
import net.itransformers.topologyviewer.config.models.datamatcher.DataMatcher;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pod on 10/16/16.
 */
public class DataMatcherMap {
    private TopologyViewerConfType viewerConfig;
    private Map<String, DataMatcher> matcherMap = new HashMap<String, DataMatcher>();

    static Logger logger = Logger.getLogger(IconMapLoader.class);

    public DataMatcherMap(TopologyViewerConfType viewerConfig) {
        this.viewerConfig = viewerConfig;
    }

    public void init(){
        List<DataMatcherType> matcherList = viewerConfig.getDataMatcher();
        for (DataMatcherType dataMatcherType : matcherList) {
            String className = dataMatcherType.getClazz();
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("Can not find class: "+className, e);
            }
            try {
                DataMatcher dataMatcher = (DataMatcher) clazz.newInstance();
                matcherMap.put(dataMatcherType.getName(), dataMatcher);
            } catch (InstantiationException e) {
                logger.error("Can not instantiate class: " + className, e);
            } catch (IllegalAccessException e) {
                logger.error("Can not access constructor class: " + className, e);
            }
        }
    }

    public DataMatcher getMatcher(String matcherName) {
        return matcherMap.get(matcherName);
    }
}
