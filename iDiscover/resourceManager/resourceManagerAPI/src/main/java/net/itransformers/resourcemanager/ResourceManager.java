/*
 * ResourceManager.java
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

package net.itransformers.resourcemanager;


import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;

import java.util.*;

public interface ResourceManager {

    ResourceType findFirstResourceBy(Map<String, String> deviceParams);
    List<ResourceType> findResourceBy(Map<String, String> deviceParams);
    List<ResourceType> findResourcesByConnectionType(String connectionParametersType);

    void createResources(ResourcesType resourcesType);
    ResourcesType getResources();

    List<String> getResourceNames();
    void createResourceName(String resourceName);
    void updateResourceName(String resourceName, String newResourceName);
    void deleteResourceName(String resourceName);

    void createSelectionParam(String resourceName, String paramName, String paramValue);
    void updateSelectionParam(String resourceName, String paramName, String paramValue);
    void deleteSelectionParam(String resourceName, String paramName);
    List<ParamType> getSelectionParams(String resourceName);

    List<String> getConnectionTypes(String resourceName);
    void createConnectionType(String resourceName, String connType);
    void deleteConnectionType(String resourceName, String connType);
    void createConnectionParam(String resourceName, String connectionType, String paramName, String paramValue);
    void updateConnectionParams(String resourceName, String connectionType, String paramName, String paramValue);
    void deleteConnectionParams(String resourceName, String connectionType, String paramName);
    List<ParamType> getConnectionParams(String resourceName, String connectionType);
}
