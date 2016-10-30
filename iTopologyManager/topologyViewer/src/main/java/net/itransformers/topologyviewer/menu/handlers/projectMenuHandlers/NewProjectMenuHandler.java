/*
 * NewProjectMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.projectMenuHandlers;

import net.itransformers.filebasedprojectmanager.FileBasedProjectManager;
import net.itransformers.projectmanagerapi.ProjectManagerException;
import net.itransformers.topologyviewer.dialogs.NewProjectDialog;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.ProjectConstants;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class NewProjectMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    static Logger logger = Logger.getLogger(NewProjectMenuHandler.class);

    public NewProjectMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NewProjectDialog dialog = new NewProjectDialog(frame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        File file = null;
        String projectType = dialog.getProjectType();
        logger.info("Selected project file is from type: " + projectType);

        if (!dialog.isOkPressed()) {
            return;
        }
        frame.setTitle(ProjectConstants.getProjectName(projectType));

        FileBasedProjectManager fileBasedProjectManager = new FileBasedProjectManager(dialog.getProjectDir().getParentFile());


        try{
            fileBasedProjectManager.createProject("projectTemplates/netTransformer.pfl", dialog.getProjectDir().getAbsolutePath());
        } catch (ProjectManagerException e1)
        {
            JOptionPane.showMessageDialog(frame, "Unable to create project in "+dialog.getProjectDir());

        }
        frame.setPath(dialog.getProjectDir());

        switch (projectType) {
            case ProjectConstants.mrtBgpDiscovererProjectType:
                file = new File("bgpPeeringMap.pfl");
                frame.setProjectType(ProjectConstants.mrtBgpDiscovererProjectType);
                frame.setViewerConfig("bgpPeeringMap");
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(false);
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
                frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(4).setEnabled(true);
                break;
            case ProjectConstants.freeGraphProjectType:
                file = new File("freeGraph.pfl");
                frame.setProjectType(ProjectConstants.freeGraphProjectType);
                frame.setViewerConfig("freeGraph");
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(false);
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(false);
                frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(5).setEnabled(true);
                break;
            case ProjectConstants.snmpProjectType:
                file = new File("netTransformer.pfl");
                frame.setProjectType(ProjectConstants.snmpProjectType);
                frame.setViewerConfig("discovery");
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
                frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(3).setEnabled(true);
                break;

            case ProjectConstants.snmpBgpDiscovererProjectType:
                file = new File("bgpSnmpPeeringMap.pfl");
                frame.setProjectType(ProjectConstants.snmpBgpDiscovererProjectType);
                frame.setViewerConfig("xmlTopologyViewerConfig/conf/xml/bgpPeeringMap/viewer-config.xml");
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
                frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(3).setEnabled(true);
                break;

            default:
                // JOptionPane.showMessageDialog(,"Unknown projectType", JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(frame, "Unknown ProjectType");


        }

//        else if (projectType.equals(ProjectConstants.mrtBgpDiscovererProjectType)) {
//
//
//        } else if (projectType.equals(ProjectConstants.freeGraphProjectType)) {
//
//        } else if (projectType.equals(ProjectConstants.snmpProjectType)){
//            file = new File("netTransformer.pfl");
//            frame.setProjectType(ProjectConstants.snmpProjectType);
//            frame.setViewerConfig(new File(dialog.getProjectDir() + File.separator + "iTopologyManager/topologyViewer/conf/xml/viewer-config.xml"));
//            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
//            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
//            frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(3).setEnabled(true);
//
//        } else if (projectType.equals(ProjectConstants.snmpBgpDiscovererProjectType)) {
//            file = new File("netTransformer.pfl");
//            frame.setProjectType(ProjectConstants.snmpProjectType);
//            frame.setViewerConfig(new File(dialog.getProjectDir() + File.separator + "iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/viewer-config.xml"));
//            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
//            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
//            frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(3).setEnabled(true);
//        } else {
//
//            JOptionPane.showMessageDialog(frame,"Unknown projectType", JOptionPane.ERROR_MESSAGE);
//
//        }



    frame.setPath(dialog.getProjectDir());
    frame.getRootPane().getJMenuBar().getMenu(1).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(2).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(3).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(4).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(5).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(6).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(7).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(4).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(5).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(6).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(7).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(8).setEnabled(true);
    frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(9).setEnabled(true);



    }


}
