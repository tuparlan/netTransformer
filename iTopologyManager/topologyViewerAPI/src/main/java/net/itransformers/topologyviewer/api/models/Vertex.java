package net.itransformers.topologyviewer.api.models;

import java.util.List;

/**
 * Created by pod on 10/15/16.
 */
public class Vertex {
    String id;
    String label;
    List<Icon> icons;

    public Vertex(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Icon> getIcons() {
        return icons;
    }

    public void setIcons(List<Icon> icons) {
        this.icons = icons;
    }
}
