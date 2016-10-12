package net.itransformers.connectiondetails.connectiondetailsapi;

import java.util.Map;

/**
 * Created by niau on 9/18/16.
 */
public interface ConnectionDetailsManager {
    void createConnections(Map<String,ConnectionDetails> connectionDetailsMap);
    Map<String,ConnectionDetails> getConnections();

    ConnectionDetails getConnection(String name);
    void createConnection(String name, ConnectionDetails connectionDetails);
    void updateConnection(String name, String newConnectionDetailName);
    void deleteConnection(String name);

    String getConnectionType(String name);
    void updateConnectionType(String name, String type);

    void createConnectionParam(String name, String paramName, String paramValue);
    void updateConnectionParam(String name, String paramName, String paramValue);
    void deleteConnectionParam(String name, String paramName);
    String getConnectionParam(String name, String paramName);
    Map<String, String> getConnectionParams(String name, String type);
}
