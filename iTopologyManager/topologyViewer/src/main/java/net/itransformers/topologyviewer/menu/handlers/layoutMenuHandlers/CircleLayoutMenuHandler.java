package net.itransformers.topologyviewer.menu.handlers.layoutMenuHandlers;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by niau on 11/26/16.
 */
public class CircleLayoutMenuHandler implements ActionListener {
    private TopologyManagerFrame frame;
    String layout;

    public CircleLayoutMenuHandler(TopologyManagerFrame frame) {


        this.frame = frame;
        this.layout = layout;
    }



    @Override
    public void actionPerformed(ActionEvent e) {


        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        CircleLayout circleLayout = new CircleLayout<String, String>(viewerPanel.getCurrentGraph());


        MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();

        vv.setGraphLayout(circleLayout);
        vv.repaint();

    }
}
