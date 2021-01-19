package org.noear.solon.cloud.extend.consul.detector;

import java.util.Map;

public interface Detector {
    String getName();
    Map<String,Object> getInfo();
}
