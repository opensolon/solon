package org.noear.solon.cloud.utils;

import org.noear.mlog.Metainfo;

/**
 * @author noear
 * @since 1.2
 */
public class TagMetainfo extends Metainfo {
    public String tag0() {
        return get("tag0");
    }

    public TagMetainfo tag0(String tag0) {
        put("tag0", tag0);
        return this;
    }

    public String tag1() {
        return get("tag1");
    }

    public TagMetainfo tag1(String tag1) {
        put("tag1", tag1);
        return this;
    }

    public String tag2() {
        return get("tag2");
    }

    public TagMetainfo tag2(String tag2) {
        put("tag2", tag2);
        return this;
    }


    public String tag3() {
        return get("tag3");
    }

    public TagMetainfo tag3(String tag3) {
        put("tag3", tag3);
        return this;
    }


    public String tag4() {
        return get("tag4");
    }

    public TagMetainfo tag4(String tag4) {
        put("tag4", tag4);
        return this;
    }
}
