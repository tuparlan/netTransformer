package net.itransformers.topologyviewer.api.models;

/**
 * Created by pod on 10/15/16.
 */
public class Stroke {
    float width;
    int cap;
    int join;
    float miterlimit;
    float[] dash;
    float dashPhase;

    public Stroke(float width, int cap, int join, float miterlimit, float[] dash, float dashPhase) {
        this.width = width;
        this.cap = cap;
        this.join = join;
        this.miterlimit = miterlimit;
        this.dash = dash;
        this.dashPhase = dashPhase;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public int getJoin() {
        return join;
    }

    public void setJoin(int join) {
        this.join = join;
    }

    public float getMiterlimit() {
        return miterlimit;
    }

    public void setMiterlimit(float miterlimit) {
        this.miterlimit = miterlimit;
    }

    public float[] getDash() {
        return dash;
    }

    public void setDash(float[] dash) {
        this.dash = dash;
    }

    public float getDashPhase() {
        return dashPhase;
    }

    public void setDashPhase(float dashPhase) {
        this.dashPhase = dashPhase;
    }
}
