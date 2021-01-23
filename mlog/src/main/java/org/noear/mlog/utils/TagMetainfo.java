package org.noear.mlog.utils;

import org.noear.mlog.Metainfo;

/**
 * @author noear 2021/1/23 created
 */
public class TagMetainfo extends Metainfo {
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

    public String tag5() {
        return get("tag5");
    }

    public TagMetainfo tag5(String tag5) {
        put("tag5", tag5);
        return this;
    }
}
