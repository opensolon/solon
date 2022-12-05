package org.noear.solon.web.webdav.test;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.web.webdav.AbstractHandler;
import org.noear.solon.web.webdav.FileSystem;
import org.noear.solon.web.webdav.impl.LocalFileSystem;

/**
 * @author noear 2022/12/2 created
 */
public class Demo {
    public static void main(String[] args) {
        FileSystem fileSystem = new LocalFileSystem("D:\\webos");
        Handler handler = new AbstractHandler(true) {
            @Override
            public String user(Context ctx) {
                return "admin";
            }

            @Override
            public FileSystem fileSystem() {
                return fileSystem;
            }

            @Override
            public String prefix() {
                return "/webdav";
            }
        };

        Solon.start(Demo.class, args, app -> {
            app.webdav("/webdav", handler);
        });
    }
}
