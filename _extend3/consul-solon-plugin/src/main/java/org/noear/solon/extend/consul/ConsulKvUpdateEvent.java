package org.noear.solon.extend.consul;

import java.util.Map;

public class ConsulKvUpdateEvent {
    //变更的Kv
   private Map<String,String> values;

    public ConsulKvUpdateEvent(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
