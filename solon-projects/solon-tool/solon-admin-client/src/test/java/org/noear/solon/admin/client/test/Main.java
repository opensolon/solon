package org.noear.solon.admin.client.test;

import org.noear.solon.Solon;
import org.noear.solon.admin.client.annotation.EnableAdminClient;
import org.noear.solon.annotation.SolonMain;

@EnableAdminClient
@SolonMain
public class Main {

    public static void main(String[] args) {
        Solon.start(Main.class, args);
    }

}
