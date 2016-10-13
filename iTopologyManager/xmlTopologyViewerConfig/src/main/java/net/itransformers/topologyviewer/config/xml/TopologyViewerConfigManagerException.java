package net.itransformers.topologyviewer.config.xml;

import java.io.FileNotFoundException;

/**
 * Created by vasko on 10/13/2016.
 */
public class TopologyViewerConfigManagerException extends RuntimeException {
    public TopologyViewerConfigManagerException(String message, FileNotFoundException e) {
        super(message,e);
    }
}
