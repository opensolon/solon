package org.noear.nami.springboot;

import java.util.List;
import java.util.function.Supplier;

/**
 * 一个简单的负载均衡器
 */
public class SimpleLoadBalancer implements Supplier<String> {
    private final List<String> urls;
    private int index=0;
    public SimpleLoadBalancer(List<String> urls){
        this.urls=urls;
    };

    @Override
    public String get() {
        int size= urls.size();
        if(size==0){
            return null;
        }
        index=index++%size;
        return urls.get(index);
    }
}
