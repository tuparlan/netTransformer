package net.itransformers.topologyviewer.menu.handlers.layoutMenuHandlers;

import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by niau on 11/26/16.
 */
public class DAGLayoutMenuHandler implements ActionListener {
    private TopologyManagerFrame frame;

    public DAGLayoutMenuHandler(TopologyManagerFrame frame ) {


        this.frame = frame;
    }



    @Override
    public void actionPerformed(ActionEvent e) {


        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();

        DAGLayout dagLayout = new DAGLayout(viewerPanel.getCurrentGraph());


        MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();

        vv.setGraphLayout(dagLayout);
        vv.repaint();

    }
}
