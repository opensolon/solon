package org.noear.nami.springboot;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Nami负载均衡提供工厂
 * 集成服务发现时，可以通过该工厂把找到的服务注册进来
 */
public class NamiUpstreamFactory {
    private static Map<String,Supplier<String>> upstreamMap=new ConcurrentHashMap<>();

    public static void regUpstream(String name,Supplier<String> upstream){
        upstreamMap.put(name,upstream);
    }
    public static void regUpstream(String name, List<String> urls){
        regUpstream(name, new SimpleLoadBalancer(urls));
    }
    public static void unRegUpstream(String name){
        upstreamMap.remove(name);
    }

    public static Supplier<String> getUpstream(String name) {
        return upstreamMap.get(name);
    }
}
