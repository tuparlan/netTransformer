/*
 * netTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.discoveryresult;

import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;
import net.itransformers.topologyviewer.config.models.FilterType;
import net.itransformers.topologyviewer.config.models.ForType;
import net.itransformers.topologyviewer.config.models.IncludeType;
import net.itransformers.topologyviewer.config.models.datamatcher.DataMatcher;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EdgeFilter {
    static Logger logger = Logger.getLogger(EdgeFilter.class);
    protected DataMatcherMap dataMatcherMap;
    protected FilterTypeMap filterTypeMap;

    public EdgeFilter(DataMatcherMap dataMatcherMap, FilterTypeMap filterTypeMap) {
        this.dataMatcherMap = dataMatcherMap;
        this.filterTypeMap = filterTypeMap;
    }

    public Map<String, GraphmlEdge> filter(String filterName, Map<String, GraphmlEdge> edgeMap) {
        Map<String, GraphmlEdge> result = new HashMap<>();
        for (String edgeId : edgeMap.keySet()) {
            if (hasToInclude(edgeId, filterName, edgeMap)) {
                result.put(edgeId, edgeMap.get(edgeId));
            }
        }
        return result;
    }

    protected boolean hasToInclude(String edge, String filterName, Map<String, GraphmlEdge> edgeMap) {
        try {
            FilterType filter = filterTypeMap.getFilter(filterName);
            if (filter == null) return true;

            List<IncludeType> includes = filter.getInclude();
            String filterType = filter.getType();

            if (filterType == null) {
                filterType = "or";
            }
            boolean hasEdgeInlcude = false;

            for (IncludeType include : includes) {

                if (ForType.EDGE.equals(include.getFor())) {
                    String matcher = include.getMatcher();
                    if (matcher == null) {
                        matcher = "default";
                    }
                    final String dataKey = include.getDataKey();
                    if (dataKey == null) { // lets include all edges
                        hasEdgeInlcude = true;

                        continue;
                    }

                    String value = edgeMap.get(edge).getGraphmlEdgeData().get(dataKey);
                    if (value != null) {
                        String[] dataValues = value.split(",");
                        String includeDataValue = include.getDataValue();

                        //Circle around the actual values and perform the datamatch
                        DataMatcher matcherInstance = dataMatcherMap.getMatcher(matcher);

                        boolean hasToInclude = false;


                        if ("and".equals(filterType)) {
                            for (String dataValue : dataValues) {
                                // boolean matchResult = ;
                                hasEdgeInlcude = false;
                                if (matcherInstance.compareData(dataValue, includeDataValue)) {
                                    logger.debug("Edge selected: " + edge + " by filter " + filter.getName() + " with include " + include.getDataKey() + " with value " + dataValue);
                                    hasEdgeInlcude = true;
                                }
                            }

                            if (!hasEdgeInlcude) {
                                return false;
                            }
                            //If we have an "or" filter

                        } else {
                            for (String dataValue : dataValues) {
                                if (matcherInstance.compareData(dataValue, includeDataValue)) {
                                    logger.debug("Edge " + edge + " has been selected from filter " + filter.getName() + " by property " + include.getDataKey() + " with value " + dataValue);
                                    return true;

                                }

                            }
                        }
                    }

                }

            }
            //Finally if the has to include flag is set include
            if (!hasEdgeInlcude) {
                System.out.println("Edge " + edge + " has not been selected");
                return false;

            } else {
                System.out.println("Edge " + edge + " has been selected");

                return true;
            }
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            return false;
        }
    }
}
