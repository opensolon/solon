package org.noear.solon.extend.consul.detector;

import java.util.Map;

public interface Detector {
    String getName();
    Map<String,Object> getInfo();
}
