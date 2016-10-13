package net.itransformers.connectiondetails.csvconnectiondetails;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManagerFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by vasko on 30.09.16.
 */
public class CsvConnectionDetailsManagerFactory implements ConnectionDetailsManagerFactory {
    Logger logger = Logger.getLogger(CsvConnectionDetailsManagerFactory.class);
    String connectionDetailsPath;

    public CsvConnectionDetailsManagerFactory(String connectionDetailsPath) {
        this.connectionDetailsPath = connectionDetailsPath;
    }

    @Override
    public ConnectionDetailsManager createConnectionDetailsManager(String type, Map<String, String> properties) {

        String projectPath = properties.get("projectPath");
        if (projectPath == null) {
            throw new IllegalArgumentException("projectPath is not specified");

        }

        String filePath = projectPath+File.separator+connectionDetailsPath;

        CsvConnectionDetailsFileManager csvConnectionDetailsFileManager = new CsvConnectionDetailsFileManager(filePath);
        try {
            csvConnectionDetailsFileManager.load();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return csvConnectionDetailsFileManager;
    }

    public String getConnectionDetailsPath() {
        return connectionDetailsPath;
    }

    public void setConnectionDetailsPath(String connectionDetailsPath) {
        this.connectionDetailsPath = connectionDetailsPath;
    }
}
