package org.noear.solon.extend.consul.test;


import org.noear.solon.Solon;
import org.noear.solon.annotation.Remoting;

@Remoting
public class TestApplication implements HelloInterface{
    public static void main(String[] args) {
        Solon.start(TestApplication.class, args);
    }

    @Override
    public String hello0() {
        return "你好：（";
    }
}
