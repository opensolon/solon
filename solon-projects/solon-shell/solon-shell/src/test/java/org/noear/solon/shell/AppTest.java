package org.noear.solon.shell;


import org.noear.solon.Solon;

public class AppTest {
    public static void main(String[] args) throws Exception{
        Solon.start(AppTest.class, args).block();
    }
}
