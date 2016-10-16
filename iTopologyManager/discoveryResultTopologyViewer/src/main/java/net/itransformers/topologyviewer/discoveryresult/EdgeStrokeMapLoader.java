package net.itransformers.topologyviewer.discoveryresult;/*
 * net.itransformers.topologyviewer.discoveryresult.EdgeStrokeMapLoader.java
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

import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;
import net.itransformers.topologyviewer.api.models.Stroke;
import net.itransformers.topologyviewer.config.models.EdgeStrokeType;
import net.itransformers.topologyviewer.config.models.TopologyViewerConfType;
import org.apache.log4j.Logger;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class EdgeStrokeMapLoader {
    static Logger logger = Logger.getLogger(EdgeStrokeMapLoader.class);

    private TopologyViewerConfType viewerConfig;

    public EdgeStrokeMapLoader(TopologyViewerConfType viewerConfig) {
        this.viewerConfig = viewerConfig;
    }

    public Map<String,Stroke> getEdgeStrokeMap(Map<String, GraphmlEdge> edgeMap) {
        Map<String,Stroke> edgesStrokeMap = new HashMap<String, Stroke>();
        List<EdgeStrokeType> edgeStrokeTypeList = viewerConfig.getEdgeStroke();
        List<EdgeStrokeType.Data> datas;
        for (GraphmlEdge edge : edgeMap.values()) {
            for (EdgeStrokeType edgeStrokeType : edgeStrokeTypeList) {
                boolean match = true;
                datas = edgeStrokeType.getData();
                boolean isDefaultIcon = datas.isEmpty();
                for (EdgeStrokeType.Data data : datas) {
                    final String value = edge.getGraphmlEdgeData().get(data.getKey());
                    if (value == null || !value.equals(data.getValue())) {
                        match = false;
                        break;
                    }
                }
                boolean iconExists = edgesStrokeMap.containsKey(edge.getId());
                if ((!isDefaultIcon && match) || (isDefaultIcon && !iconExists)) {
                    try {
                        String dashStr = edgeStrokeType.getDash();
                        if(dashStr!=null){
                            String[] dashArr = dashStr.split(",");
                            float[] dash = new float[dashArr.length];
                            for (int i=0;i < dashArr.length; i++) {
                                dash[i] = Float.parseFloat(dashArr[i]);
                            }
                            float width = edgeStrokeType.getWidth();
                            float dashPhase = edgeStrokeType.getDashPhase();
                            int join = edgeStrokeType.getJoin();
                            int cap = edgeStrokeType.getCap();
                            float miterlimit = edgeStrokeType.getMiterlimit();
                            final Stroke edgeStroke = new Stroke(width, cap,
                                    join, miterlimit, dash, dashPhase);
                            edgesStrokeMap.put(edge.getId(), edgeStroke);

                        }
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        break;
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                        break;
                    }
                    break;
                }
            }
        }
        return edgesStrokeMap;
    }

}
