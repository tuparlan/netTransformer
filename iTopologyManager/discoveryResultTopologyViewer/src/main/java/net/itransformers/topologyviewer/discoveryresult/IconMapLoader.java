package net.itransformers.topologyviewer.discoveryresult;/*
 * net.itransformers.topologyviewer.discoveryresult.IconMapLoader.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.topologyviewer.config.models.DataMatcherType;
import net.itransformers.topologyviewer.config.models.IconType;
import net.itransformers.topologyviewer.config.models.TopologyViewerConfType;
import net.itransformers.topologyviewer.config.models.datamatcher.DataMatcher;
import org.apache.log4j.Logger;
import net.itransformers.topologyviewer.api.models.Icon;
import java.util.*;


public class IconMapLoader {
    static Logger logger = Logger.getLogger(IconMapLoader.class);

    private TopologyViewerConfType viewerConfig;
    private DataMatcherMap dataMatcherMap;

    public IconMapLoader(TopologyViewerConfType viewerConfig, DataMatcherMap dataMatcherMap) {
        this.viewerConfig = viewerConfig;
        this.dataMatcherMap = dataMatcherMap;
    }

    public Map<String, List<Icon>> getIconsMap(Map<String, GraphmlNode> vertexMap) {
        Map<String, List<Icon>> iconMap = new HashMap<>();
        List<IconType> iconTypeList = viewerConfig.getIcon();
        List<IconType.Data> datas;
        for (GraphmlNode vertice : vertexMap.values()) {
            System.out.println(vertice);
            for (IconType iconType : iconTypeList) {
                boolean match = true;
                datas = iconType.getData();
                boolean isDefaultIcon = datas.isEmpty();

                for (IconType.Data data : datas) {
                    final String value = vertice.getGraphmlNodeData().get(data.getKey());
                    String matcher = data.getMatcher();
                    if (matcher == null) {
                        matcher = "default";
                    }
                    if (value == null){
                        match = false;
                        break;

                    }
                    DataMatcher matcherInstance = dataMatcherMap.getMatcher(matcher);
                    boolean matchResult = matcherInstance.compareData(value, data.getValue());
                    if (!matchResult) {
                        match = false;
                        break;
                    }
                }
                boolean iconExists = iconMap.containsKey(vertice.getId());
                if ((!isDefaultIcon && match) || (isDefaultIcon && !iconExists)) {
                    final String name = iconType.getName();
                    String[] iconNames = name.split(",");
                    iconMap.putIfAbsent(vertice.getId(), new ArrayList<>());
                    for (int i=0;i<iconNames.length;i++) {
                        List<Icon> iconList = iconMap.get(vertice.getId());
                        iconList.add(new Icon(iconNames[i].trim()));
                    }
                    break;
                }
            }
        }
        return iconMap;
    }

}
