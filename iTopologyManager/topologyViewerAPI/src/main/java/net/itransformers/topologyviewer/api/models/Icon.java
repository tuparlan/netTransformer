package net.itransformers.topologyviewer.api.models;

/**
 * Created by pod on 10/15/16.
 */
public class Icon {
    private String name;

    public Icon(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Icon icon = (Icon) o;

        return name != null ? name.equals(icon.name) : icon.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
