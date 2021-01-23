package org.noear.mlog.utils;

import org.noear.mlog.Marker;

/**
 * @author noear
 * @since 1.2
 */
public class TagMarker implements Marker {
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;

    public String tag1() {
        return tag1;
    }

    public TagMarker tag1(String tag1) {
        this.tag1 = tag1;
        return this;
    }

    public String tag2() {
        return tag2;
    }

    public TagMarker tag2(String tag2) {
        this.tag2 = tag2;
        return this;
    }

    public String tag3() {
        return tag3;
    }

    public TagMarker tag3(String tag3) {
        this.tag3 = tag3;
        return this;
    }

    public String tag4() {
        return tag4;
    }

    public TagMarker tag4(String tag4) {
        this.tag4 = tag4;
        return this;
    }

    public String tag5() {
        return tag5;
    }

    public TagMarker tag5(String tag5) {
        this.tag5 = tag5;
        return this;
    }

    @Override
    public String formatAsString() {
        return "['" + tag1 + "']" +
                "['" + tag2 + "']" +
                "['" + tag3 + "']" +
                "['" + tag4 + "']" +
                "['" + tag5 + "']";
    }
}
