package features.serialization.jackson.action;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.*;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.test.SolonTest;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author noear 2024/9/17 created
 */
@SolonTest
public class TestExecutor {
    @Inject
    AppContext context;

    @Test
    public void test() throws Throwable {
        System.out.println(DateTimeFormatter.ISO_OFFSET_DATE_TIME.toString());

        ContextEmpty ctx = new ContextEmpty();
        ctx.headerMap().add("Content-Type", "text/json");
        ctx.pathNew("/a1");
        ctx.bodyNew("{\"name\":\"noear\",\"label\":\"A\"}");

        context.app().routerHandler().handle(ctx);
        ctx.result = ctx.attr("output");
        System.out.println(ctx.result);
        assert "Hello noear A".equals(ctx.result);
    }

    @Test
    public void test1() throws Throwable {
        ContextEmpty ctx = new ContextEmpty();
        ctx.headerMap().add("Content-Type", "text/json");
        ctx.pathNew("/a2");
        ctx.bodyNew("{\"name\":\"noear\",\"label\":\"A\"}");

        context.app().routerHandler().handle(ctx);

        ctx.result = ctx.attr("output");
        System.out.println(ctx.result);
        assert "\"A\"".equals(ctx.result);
    }

    @Test
    public void test2() throws Throwable {
        String time1 = "2024-10-22T08:23:17.315";

        ContextEmpty ctx = new ContextEmpty();
        ctx.headerMap().add("Content-Type", "text/json");
        ctx.pathNew("/a3");
        ctx.bodyNew("{\"name\":\"noear\",\"time\":\"" + time1 + "\"}");

        context.app().routerHandler().handle(ctx);

        ctx.result = ctx.attr("output");
        System.out.println(ctx.result);
        assert time1.equals(ctx.result);
    }

    @Test
    public void test2_a() throws Throwable {
        String time1 = "2024-10-22T08:48:21";
        String time2 = "2024-10-22 08:48:21";
        String time3 = "2024-10-22";

        {
            ContextEmpty ctx = new ContextEmpty();
            ctx.headerMap().add("Content-Type", "text/json");
            ctx.pathNew("/a3");
            ctx.bodyNew("{\"name\":\"noear\",\"time\":\"" + time1 + "\"}");

            context.app().routerHandler().handle(ctx);

            ctx.result = ctx.attr("output");
            System.out.println(ctx.result);
            assert time1.equals(ctx.result);
        }

        {
            ContextEmpty ctx = new ContextEmpty();
            ctx.headerMap().add("Content-Type", "text/json");
            ctx.pathNew("/a3");
            ctx.bodyNew("{\"name\":\"noear\",\"time\":\"" + time2 + "\"}");

            context.app().routerHandler().handle(ctx);

            ctx.result = ctx.attr("output");
            System.out.println(ctx.result);
            assert time1.equals(ctx.result);
        }

        {
            ContextEmpty ctx = new ContextEmpty();
            ctx.headerMap().add("Content-Type", "text/json");
            ctx.pathNew("/a3");
            ctx.bodyNew("{\"name\":\"noear\",\"time\":\"" + time3 + "\"}");

            context.app().routerHandler().handle(ctx);

            ctx.result = ctx.attr("output");
            System.out.println(ctx.result);
            assert "2024-10-22T00:00".equals(ctx.result);
        }
    }

    @Test
    public void test2_b() throws Throwable {
        {
            ContextEmpty ctx = new ContextEmpty();
            ctx.headerMap().add("Content-Type", "text/json");
            ctx.pathNew("/a3");
            ctx.bodyNew("{\"name\":\"noear\",\"time\":\"" + LocalDate.now() + "\"}");

            context.app().routerHandler().handle(ctx);
        }

        {
            ContextEmpty ctx = new ContextEmpty();
            ctx.headerMap().add("Content-Type", "text/json");
            ctx.pathNew("/a3");
            ctx.bodyNew("{\"name\":\"noear\",\"time\":\"" + LocalTime.now() + "\"}");

            context.app().routerHandler().handle(ctx);
        }

        {
            ContextEmpty ctx = new ContextEmpty();
            ctx.headerMap().add("Content-Type", "text/json");
            ctx.pathNew("/a3");
            ctx.bodyNew("{\"name\":\"noear\",\"time\":\"" + OffsetDateTime.now() + "\"}");

            context.app().routerHandler().handle(ctx);
        }

        {
            ContextEmpty ctx = new ContextEmpty();
            ctx.headerMap().add("Content-Type", "text/json");
            ctx.pathNew("/a3");
            ctx.bodyNew("{\"name\":\"noear\",\"time\":\"" + OffsetTime.now() + "\"}");

            context.app().routerHandler().handle(ctx);
        }
    }

    @Controller
    public static class Demo {
        @Mapping("/a1")
        public String a1(String name, Label label) {
            return "Hello " + name + " " + label;
        }

        @Mapping("/a2")
        public Label a2(String name, Label label) {
            return label;
        }

        @Mapping("/a3")
        public String a3(String name, LocalDateTime time) {
            return time.toString();
        }
    }

    public enum Label {
        A,
        B
    }
}