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
import net.itransformers.topologyviewer.dialogs.NewProjectDialog;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

        String projectName = dialog.getProjectName();
        frame.setTitle(projectName);

        FileBasedProjectManager fileBasedProjectManager = new FileBasedProjectManager(dialog.getProjectDir().getParentFile());

        fileBasedProjectManager.createProject(projectName,projectType);

//        try{
//            fileBasedProjectManager.createProject("projectTemplates/netTransformer.pfl", dialog.getProjectDir().getAbsolutePath());
//        } catch (ProjectManagerException e1)
//        {
//            JOptionPane.showMessageDialog(frame, "Unable to create project in "+dialog.getProjectDir());
//
//        }
        frame.setPath(dialog.getProjectDir());
//
//        switch (projectType) {
//
//            case ProjectConstants.freeGraphProjectType:
//                frame.setProjectType(ProjectConstants.freeGraphProjectType);
//                frame.setViewerConfig("freeGraph");
//                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(false);
//                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(false);
//                frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(5).setEnabled(true);
//                break;
//            case ProjectConstants.snmpProjectType:
//                frame.setProjectType(ProjectConstants.snmpProjectType);
//                frame.setViewerConfig("discovery");
//                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
//                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
//                frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(3).setEnabled(true);
//                break;
//
//            case ProjectConstants.bgpDiscovererProjectType:
//                frame.setProjectType(ProjectConstants.bgpDiscovererProjectType);
//                frame.setViewerConfig("bgpPeeringMap");
//                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
//                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
//                frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(3).setEnabled(true);
//                break;
//
//            default:
//                // JOptionPane.showMessageDialog(,"Unknown projectType", JOptionPane.ERROR_MESSAGE);
//                JOptionPane.showMessageDialog(frame, "Unknown ProjectType");
//
//
//        }


     frame.doOpenProject(dialog.getProjectDir());


    }


}
