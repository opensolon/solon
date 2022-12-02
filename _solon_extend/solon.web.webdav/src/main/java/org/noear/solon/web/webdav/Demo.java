package org.noear.solon.web.webdav;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;

/**
 * @author noear 2022/12/2 created
 */
public class Demo {
    public static void main(String[] args) {
        FileSystem fileSystem = new LocalFileSystem("D:\\webos");
        Handler handler = new AbstractHandler() {
            @Override
            public String getLoginUser(Context ctx) {
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
        Solon.start(Demo.class,args,app -> {
            app.filter((ctx, chain) -> {
                if(ctx.uri().getPath().startsWith("/webdav")){
                    handler.handle(ctx);
                    ctx.setHandled(true);
                }else{
                    try{
                        MethodType.valueOf(ctx.method());
                        chain.doFilter(ctx);
                    }catch (Exception e){
                    }
                }
            });
        });
    }
}
