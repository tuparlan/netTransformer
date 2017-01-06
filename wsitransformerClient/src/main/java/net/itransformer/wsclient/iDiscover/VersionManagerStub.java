package net.itransformer.wsclient.iDiscover;

import net.itransformers.idiscover.api.VersionManager;
import org.springframework.web.client.RestTemplate;

/**
 * Created by vasko on 12/2/2016.
 */
public class VersionManagerStub implements VersionManager {
    protected String url;

    @Override
    public String createVersion() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(url+"/discovery/", null, String.class);
    }

    @Override
    public void createVersion(String versionLabel) {

    }

    @Override
    public void deleteVersion(String version) {

    }

    @Override
    public String[] getVersions() {
        return new String[0];
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
