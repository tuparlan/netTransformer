/*
 * DiscoveryWizardDialog.java
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

package net.itransformers.topologyviewer.dialogs.discovery;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManagerFactory;
import net.itransformers.connectiondetails.csvconnectiondetails.CsvConnectionDetailsManagerFactory;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.ResourceManagerFactory;
import net.itransformers.resourcemanager.xmlResourceManager.XmlResourceManagerFactory;
import net.itransformers.utils.ProjectConstants;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiscoveryWizardDialog extends JDialog {

    private int option = JOptionPane.CLOSED_OPTION;
    private JPanel contentPanel = null;
    private JButton prevButton;
    private JButton nextButton;
    private Frame frame;
    private String projectPath;
    private String discoveryBeanName;
    private ResourceManager resourceManager;
    private ConnectionDetailsManager connectionDetailsManager;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));
            ResourceManagerFactory resourceManagerFactory =  new XmlResourceManagerFactory("xmlResourceManager/conf/xml/resource.xml");
            Map<String, String> params = new HashMap<>();
            params.put("projectPath", new File(".").getAbsolutePath());
            ResourceManager resourceManager = resourceManagerFactory.createResourceManager("xml", params);

            ConnectionDetailsManagerFactory connectionDetailsManagerFactory = new CsvConnectionDetailsManagerFactory("csvConnectionDetails/conf/txt/connection-details.txt");
            ConnectionDetailsManager connectionDetailsManager = connectionDetailsManagerFactory.createConnectionDetailsManager("csv",params);

            DiscoveryWizardDialog dialog = new DiscoveryWizardDialog(null,
                    ".",
                    ProjectConstants.snmpProjectType,
                    resourceManager,
                    connectionDetailsManager);
            int option = dialog.showDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public DiscoveryWizardDialog(Frame parentFrame,
                                 String projectPath,
                                 String projectType,
                                 ResourceManager resourceManager,
                                 ConnectionDetailsManager connectionDetailsManager) {

        super(parentFrame, "Discovery Wizard", true);
        this.frame = parentFrame;
        this.projectPath = projectPath;
        //Discoverer Beans are equal to the ProjectTYpes
        this.discoveryBeanName = projectType;
        this.resourceManager = resourceManager;
        this.connectionDetailsManager = connectionDetailsManager;

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        getContentPane().setLayout(new BorderLayout());


        init();

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                prevButton = new JButton("BACK");
                prevButton.setActionCommand("BACK");
                buttonPane.add(prevButton);
                getRootPane().setDefaultButton(prevButton);
                prevButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DiscoveryWizardDialog.this.prev();
                    }
                });
                prevButton.setEnabled(false);
            }
            {
                nextButton = new JButton("NEXT");
                nextButton.setActionCommand("NEXT");
                buttonPane.add(nextButton);
                nextButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DiscoveryWizardDialog.this.next();
                    }
                });


            }
        }

    }

    private void init() {
//<<<<<<< HEAD
        ConnectionDetailsPanel connectionDetailsPanel = new ConnectionDetailsPanel(connectionDetailsManager);
//=======
        File file = new File(projectPath, "csvConnectionDetails/conf/txt/connection-details.txt");
//        ConnectionDetailsPanel connectionDetailsPanel = new ConnectionDetailsPanel();
        try {
            connectionDetailsPanel.load();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error loading connection details file");

        }
        updateCurrentPanel(connectionDetailsPanel);
    }

    private void prev() {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        if (contentPanel instanceof DiscoveryResourcePanel) {
//<<<<<<< HEAD
//=======
//            File resourceFile = new File(projectPath, "xmlResourceManager/conf/xml/resource.xml");
//>>>>>>> add3ffcab75d1513e3f0f2fd15a0396334417256
            try {
                ((DiscoveryResourcePanel) contentPanel).save();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error saving resources file");
            }
//<<<<<<< HEAD
           ConnectionDetailsPanel panel = new ConnectionDetailsPanel(connectionDetailsManager);
////=======
//            File file = new File(projectPath, "csvConnectionDetails/conf/txt/connection-details.txt");
//            ConnectionDetailsPanel panel = new ConnectionDetailsPanel();
//>>>>>>> add3ffcab75d1513e3f0f2fd15a0396334417256
            try {
                panel.load();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error loading connection file");
            }
            updateCurrentPanel(panel);
            prevButton.setEnabled(false);

            nextButton.setText("NEXT");
            nextButton.setEnabled(true);
            nextButton.setActionCommand("NEXT");

        }

    }

    private void next() {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        if (contentPanel instanceof ConnectionDetailsPanel) {
            try {
                ((ConnectionDetailsPanel) contentPanel).save();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error saving connection details file");
            }

        }

//<<<<<<< HEAD
        final DiscoveryResourcePanel panel = new DiscoveryResourcePanel(resourceManager);
//=======
//        final DiscoveryResourcePanel panel = new DiscoveryResourcePanel();
//        File resourceFile = new File(projectPath, "xmlResourceManager/conf/xml/resource.xml");
//>>>>>>> add3ffcab75d1513e3f0f2fd15a0396334417256
        try {
            panel.load(projectPath);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error loading resources details file");
        }

        updateCurrentPanel(panel);
        nextButton.setText("GO!");
        nextButton.setEnabled(true);
        nextButton.setActionCommand("GO");

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nextButton.getActionCommand().equals("GO")) {
                    DiscoveryWizardDialog.this.go();
                }
            }
        });
    }


    private void go() {
//<<<<<<< HEAD
//=======
//        File resourceFile = new File(projectPath, "xmlResourceManager/conf/xml/resource.xml");
//>>>>>>> add3ffcab75d1513e3f0f2fd15a0396334417256
        try {
            ((DiscoveryResourcePanel) contentPanel).save();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error saving resources file");
        }
        try {
            DiscoveryManagerDialogV2 discoveryManagerDialogV2 = new DiscoveryManagerDialogV2(DiscoveryWizardDialog.this.frame, new File(projectPath), discoveryBeanName);
            DiscoveryWizardDialog.this.setVisible(false);
            discoveryManagerDialogV2.setVisible(true);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void updateCurrentPanel(JPanel panel) {
        if (contentPanel != null) {
            getContentPane().remove(contentPanel);
        }
        contentPanel = panel;
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        this.validate();
    }


    public int showDialog() {
        this.setVisible(true);
        this.dispose();
        return option;
    }

}
