/*
 * VertexFilterFactory.java
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

package net.itransformers.topologyviewer.discoveryresult;

import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.topologyviewer.config.models.FilterType;
import net.itransformers.topologyviewer.config.models.ForType;
import net.itransformers.topologyviewer.config.models.IncludeType;
import net.itransformers.topologyviewer.config.models.datamatcher.DataMatcher;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 12:c9
 * To change this template use File | Settings | File Templates.
 */
public class VertexFilter {
    static Logger logger = Logger.getLogger(VertexFilter.class);
    protected DataMatcherMap dataMatcherMap;
    protected FilterTypeMap filterTypeMap;

    public VertexFilter(DataMatcherMap dataMatcherMap, FilterTypeMap filterTypeMap) {
        this.dataMatcherMap = dataMatcherMap;
        this.filterTypeMap = filterTypeMap;
    }

    public Map<String, GraphmlNode> filter(final String filterName, Map<String, GraphmlNode> vertexMap) {
        Map<String, GraphmlNode> result = new HashMap<>();
        for (String vertexId : vertexMap.keySet()) {
            if (hasToInclude(vertexId, filterName, vertexMap)) {
                result.put(vertexId, vertexMap.get(vertexId));
            }
        }
        return result;
    }

    private boolean hasToInclude(String v, final String filterName, Map<String, GraphmlNode> vertexMap) {
        FilterType filter = filterTypeMap.getFilter(filterName);
        if (filter == null) return true;
        List<IncludeType> includes = filter.getInclude();
        String filterType = filter.getType();

        if (filterType == null) {
            filterType = "or";
        }
        boolean hasNodeInlcude = false;

        for (IncludeType include : includes) {
            if (ForType.NODE.equals(include.getFor())) {

                String matcher = include.getMatcher();
                if (matcher == null) {
                    matcher = "default";
                }
                if (include.getClassType() == null) {

                    final String dataKey = include.getDataKey();
                    //include all nodes
                    if (dataKey == null) {
                        hasNodeInlcude = true;
                        continue;
                    }
                    //the evaluated node has that dataKey
                    String value = vertexMap.get(v).getGraphmlNodeData().get(dataKey);
                    //Evaluate only if the value is not null
                    if (value != null) {
                        //Evaluate multiplicity e.g multiple values for single data key split by commas
                        String[] dataValues = value.split(",");

                        //get the value from the include filter
                        String includeDataValue = include.getDataValue();

                        //Circle around the actual values and perform the datamatch
                        DataMatcher matcherInstance = dataMatcherMap.getMatcher(matcher);
                        //If we have an "and" filter
                        if ("and".equals(filterType)) {
                            for (String dataValue : dataValues) {
                                // boolean matchResult = ;
                                hasNodeInlcude = false;
                                if (matcherInstance.compareData(dataValue, includeDataValue)) {
                                    logger.debug("Node selected: " + v + " by filter " + filter.getName() + " with include " + include.getDataKey() + " with value " + dataValue);
                                    hasNodeInlcude = true;
                                }
                            }

                            if (!hasNodeInlcude) {
                                return false;
                            }
                            //If we have an "or" filter

                        } else {
                            for (String dataValue : dataValues) {
                                if (matcherInstance.compareData(dataValue, includeDataValue)) {
                                    logger.debug("Node " + v + " has been selected from filter " + filter.getName() + " by property " + include.getDataKey() + " with value " + dataValue);
                                    hasNodeInlcude = true;
                                } else {
                                    logger.debug("Node" + v + "  has not been selected from filter " + filter.getName() + " by property " + include.getDataKey() + " with value " + dataValue);

                                }

                            }
                        }
                    }
                } else {
                    throw new RuntimeException("Not implemented");
                }
            }
        }
        //Finally if the has to include flag is set include
        if (!hasNodeInlcude) {
            System.out.println("Node " + v + " has not been selected");
            return false;

        } else {
            System.out.println("Node " + v + " has been selected");

            return true;
        }
    }
}

