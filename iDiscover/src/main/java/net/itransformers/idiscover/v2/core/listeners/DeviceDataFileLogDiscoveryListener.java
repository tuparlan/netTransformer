/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.v2.core.listeners;/*
 * iTransformer is an open source tool able to discover IP networks
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

import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;

public class DeviceDataFileLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(DeviceDataFileLogDiscoveryListener.class);
    String baseDirName;

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        File baseDir = new File(baseDirName);
        File deviceDataDir = new File(baseDir, "device-data");
        if (!deviceDataDir.exists()) deviceDataDir.mkdir();

        String deviceName = discoveryResult.getNodeId();
        File file = new File(deviceDataDir, deviceName+".xml");
        DiscoveredDeviceData discoveredDeviceData = (DiscoveredDeviceData) discoveryResult.getDiscoveredData("deviceData");
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            JaxbMarshalar.marshal(discoveredDeviceData, os, "DiscoveredDevice");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        } catch (JAXBException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (os != null) try {os.close();} catch (IOException e) {}
        }

    }

    public String getBaseDirName() {
        return baseDirName;
    }

    public void setBaseDirName(String baseDirName) {
        this.baseDirName = baseDirName;
    }
}