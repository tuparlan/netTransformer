package net.itransformer.wsclient.connectionDetails;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by vasko on 12/2/2016.
 */
public class ConnectionDetailsManagerStub implements ConnectionDetailsManager {
    protected String url;

    @Override
    public void createConnections(Map<String, ConnectionDetails> connectionDetailsMap) {
    }

    @Override
    public Map<String, ConnectionDetails> getConnections() {
        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
        Map<String, ConnectionDetails> result = restTemplate.getForObject(url+"/connections/",  Map.class);
        return result;
    }

    @Override
    public ConnectionDetails getConnection(String name) {
        return null;
    }

    @Override
    public void createConnection(String name, ConnectionDetails connectionDetails) {

    }

    @Override
    public void updateConnection(String name, String newConnectionDetailName) {

    }

    @Override
    public void deleteConnection(String name) {

    }

    @Override
    public String getConnectionType(String name) {
        return null;
    }

    @Override
    public void updateConnectionType(String name, String type) {

    }

    @Override
    public void createConnectionParam(String name, String paramName, String paramValue) {

    }

    @Override
    public void updateConnectionParam(String name, String paramName, String paramValue) {

    }

    @Override
    public void deleteConnectionParam(String name, String paramName) {

    }

    @Override
    public String getConnectionParam(String name, String paramName) {
        return null;
    }

    @Override
    public Map<String, String> getConnectionParams(String name, String type) {
        return null;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
