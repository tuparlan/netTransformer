package net.itransformer.wsclient.iDiscover;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.NetworkDiscoveryListener;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

/**
 * Created by vasko on 12/2/2016.
 */
public class NetworkDiscovererStub implements NetworkDiscoverer {
    protected String url;
    protected String version;

    @Override
    public void startDiscovery() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(url+"/discovery/"+version+"/discoverer", null, Void.class);
    }

    @Override
    public void stopDiscovery() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(url+"/discovery/"+version+"/discoverer");
    }

    @Override
    public void pauseDiscovery() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(url+"/discovery/"+version+"/discoverer", "PAUSE");
    }

    @Override
    public void resumeDiscovery() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(url+"/discovery/"+version+"/discoverer", "RESUME");
    }

    @Override
    public Status getDiscoveryStatus() {
        RestTemplate restTemplate = new RestTemplate();
        NetworkDiscoverer.Status status = restTemplate.getForObject(url+"/discovery/"+version+"/discoverer", NetworkDiscoverer.Status.class);
        return status;
    }

    @Override
    public void addNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListeners) {

    }

    @Override
    public void removeNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListeners) {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
