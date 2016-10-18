package net.itransformers.topologyviewer.discoveryresult;

import net.itransformers.topologyviewer.config.models.DataMatcherType;
import net.itransformers.topologyviewer.config.models.FilterType;
import net.itransformers.topologyviewer.config.models.FiltersType;
import net.itransformers.topologyviewer.config.models.TopologyViewerConfType;
import net.itransformers.topologyviewer.config.models.datamatcher.DataMatcher;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pod on 10/16/16.
 */
public class FilterTypeMap {
    private TopologyViewerConfType viewerConfig;
    private Map<String, FilterType> filterTypeMap;

    static Logger logger = Logger.getLogger(IconMapLoader.class);

    public FilterTypeMap(TopologyViewerConfType viewerConfig) {
        this.viewerConfig = viewerConfig;
    }

    public void init(){
        filterTypeMap = new HashMap<>();
        FiltersType filtersType = viewerConfig.getFilters();
        if (filtersType == null) return;
        List<FilterType> filters = filtersType.getFilter();
        for (FilterType filter : filters) {
            filterTypeMap.put(filter.getName(), filter);
        }
    }

    public FilterType getFilter(String filterName) {
        return filterTypeMap.get(filterName);
    }
}
