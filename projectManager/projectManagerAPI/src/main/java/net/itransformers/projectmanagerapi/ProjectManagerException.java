package net.itransformers.projectmanagerapi;

import java.io.IOException;

/**
 * Created by niau on 10/12/16.
 */
public class ProjectManagerException extends RuntimeException {

    public ProjectManagerException(String message, IOException e1) {
        super(message,e1);
    }

    public ProjectManagerException(String s) {
        super(s);
    }
}
