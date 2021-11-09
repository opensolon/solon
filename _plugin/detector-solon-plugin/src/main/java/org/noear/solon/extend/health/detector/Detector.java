package org.noear.solon.extend.health.detector;

import org.noear.solon.core.Lifecycle;

import java.util.Map;

public interface Detector extends Lifecycle {
    String getName();
    Map<String,Object> getInfo();
}
