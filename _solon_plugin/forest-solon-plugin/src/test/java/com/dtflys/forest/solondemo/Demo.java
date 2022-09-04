package com.dtflys.forest.solondemo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.Bridge;

import java.io.IOException;

@Component
public class Demo {
    @Inject
    TestGiteeApi api;
    @Init
    public void init(){
        String ret=api.search("solon");
        System.out.println(ret);
    }
    public static void main(String[] args) throws IOException {
        //模拟Upstream
        Bridge.upstreamFactorySet(((group, service) -> new GiteeUpstream()));

        Solon.start(Demo.class,args);
    }
}
