package org.noear.solon.cloud.model;

import java.util.Map;

/**
 * 数据包
 *
 * @author noear
 * @since 1.6
 */
public class Pack {
    Map<String, String> data;

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }
}
