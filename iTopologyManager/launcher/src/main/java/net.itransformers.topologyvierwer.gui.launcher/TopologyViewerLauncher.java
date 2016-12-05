/*
 * TopologyViewerLauncher.java
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

package net.itransformers.topologyvierwer.gui.launcher;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.security.AccessControlException;
import java.util.Properties;

public class TopologyViewerLauncher {
    public static final String VIEWER_PREFERENCES_PROPERTIES = "viewer-preferences.properties";
    private static Properties preferences = new Properties();

    public static void main(String[] args) throws Exception {


        File prefsFile = new File(VIEWER_PREFERENCES_PROPERTIES);

        try {
            if (!prefsFile.exists()) {
                if (!prefsFile.createNewFile()){
                    System.out.println("Can not create preferences file");
                }
            }
            preferences.load(new FileInputStream(prefsFile));

        } catch (AccessControlException e){
            e.printStackTrace();
        }
        String baseDir = preferences.getProperty("PATH");
        if (baseDir==null || !new File(baseDir).exists()){
             baseDir = ".";
        }
//
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        System.setProperty("base.dir", new File(baseDir).getAbsolutePath());
        ctx.load("classpath:rightClick/rightClick.xml");
        ctx.load("classpath:rightClickAPI/rightClickAPI.xml");
        ctx.load("classpath:xmlResourceManager/xmlResourceManagerFactory.xml");
        ctx.load("classpath:csvConnectionDetails/csvConnectionDetailsFactory.xml");
        ctx.load("classpath:topologyViewer/topologyViewer.xml");
        ctx.load("classpath:xmlTopologyViewerConfig/xmlTopologyViewerConfig.xml");
        ctx.refresh();

        TopologyManagerFrame frame = (TopologyManagerFrame) ctx.getBean("topologyManagerFrame");
        //frame.setPath();
        frame.init(new File(baseDir));

    }

    private static void printUsage(String msg) {
        System.out.println("Error: "+msg);
        System.out.println("Usage:   topoManager.bat [-t <directed|undirected>] -d <local_dir> -u <remote_url> -g <graphml_dir> -f <viewer_config]");
        System.out.println(
                        "-d <local_dir>              # relative or absolute path to local dir with 'graphml-dir' and 'device-data' dirs.\n"
        );
    }
}
