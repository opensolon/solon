package labs.code404;

import org.noear.solon.Solon;

/**
 * @author noear 2021/10/27 created
 */
public class TestApp {
    public static void main(String[] args) {
        Solon.start(TestApp.class, args, app -> {
//            app.after(c -> {
//                if (c.status() == 404 || c.getHandled() == false) {
//                    c.setHandled(true);
//                    c.output("没有：（");
//                }
//            });

            app.filter((ctx, chain) -> {
                long start = System.currentTimeMillis();
                try {
                    chain.doFilter(ctx);

                    if (ctx.status() == 404 || ctx.getHandled() == false) {
                        ctx.setHandled(true);
                        ctx.output("没有：（");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();

                    ctx.output("出错了：（");
                }

                //获得接口时长
                long times = System.currentTimeMillis() - start;
                System.out.println("用时："+ times);
            });
        });
    }
}
