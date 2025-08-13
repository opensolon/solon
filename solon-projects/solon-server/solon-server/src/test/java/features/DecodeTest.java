package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.server.util.DecodeUtils;
import org.noear.solon.core.handle.ContextEmpty;

/**
 * @author noear 2024/9/25 created
 */
public class DecodeTest {
    @Test
    public void decode_cookie_case1() {
        ContextEmpty ctx = new ContextEmpty();

        DecodeUtils.decodeCookies(ctx, "a=1");
        System.out.println(ctx.cookieMap());

        assert ctx.cookieMap().size() == 1;
        assert ctx.cookieMap().get("a").equals("1");
    }

    @Test
    public void decode_cookie_case1_2() {
        ContextEmpty ctx = new ContextEmpty();

        DecodeUtils.decodeCookies(ctx, " a=1 ");
        System.out.println(ctx.cookieMap());

        assert ctx.cookieMap().size() == 1;
        assert ctx.cookieMap().get("a").equals("1");
    }

    @Test
    public void decode_cookie_case2() {
        ContextEmpty ctx = new ContextEmpty();

        DecodeUtils.decodeCookies(ctx, "a=1; a=2");
        System.out.println(ctx.cookieMap());

        assert ctx.cookieMap().size() == 1;
        assert ctx.cookieMap().get("a").equals("1");
        assert ctx.cookieMap().holder("a").getLastValue().equals("2");
    }
    @Test
    public void decode_cookie_case2_2() {
        ContextEmpty ctx = new ContextEmpty();

        DecodeUtils.decodeCookies(ctx, "a=1; a=2 ");
        System.out.println(ctx.cookieMap());

        assert ctx.cookieMap().size() == 1;
        assert ctx.cookieMap().get("a").equals("1");
        assert ctx.cookieMap().holder("a").getLastValue().equals("2");
    }

    @Test
    public void decode_cookie_case3() {
        ContextEmpty ctx = new ContextEmpty();

        DecodeUtils.decodeCookies(ctx, "a=1; a=2; b=3; ");
        System.out.println(ctx.cookieMap());

        assert ctx.cookieMap().size() == 2;
        assert ctx.cookieMap().get("a").equals("1");
        assert ctx.cookieMap().get("b").equals("3");
    }
}
