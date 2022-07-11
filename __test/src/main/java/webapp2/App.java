package webapp2;

import org.noear.solon.Solon;

/**
 * @author noear 2022/7/11 created
 */
public class App {
    public static void main(String[] args){
        Solon.start(App.class, args, app->{
           app.get("/",ctx -> {
               ctx.output("hello world");
               System.out.println("get");
           });

           app.filter((ctx, chain) -> {
               System.out.println("a-bef");
               chain.doFilter(ctx);
               System.out.println("a-aft");
           });

            app.filter((ctx, chain) -> {
                System.out.println("b-bef");
                chain.doFilter(ctx);
                System.out.println("b-aft");
            });

            app.filter(1,(ctx, chain) -> {
                System.out.println("c-bef");
                chain.doFilter(ctx);
                System.out.println("c-aft");
            });
        });
    }
}
