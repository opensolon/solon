package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.util.HostUtil;

import java.net.URI;

/**
 * @author noear
 * @since 4.0
 */
public class HostUtilTest {
    @Test
    public void fromXForwardedHost() {
        ContextEmpty ctx = new ContextEmpty() {
            @Override
            public URI uri() {
                return URI.create("http://10.0.0.8:8080/demo/");
            }
        };
        ctx.headerMap().put("X-Forwarded-Host", "www.example.com");
        ctx.headerMap().put("Host", "10.0.0.8:8080");

        assert "www.example.com".equals(HostUtil.global().getRealHost(ctx));
        assert "www.example.com".equals(ctx.realHost());
    }

    @Test
    public void fromXForwardedHostMulti() {
        ContextEmpty ctx = new ContextEmpty() {
            @Override
            public URI uri() {
                return URI.create("http://10.0.0.8:8080/demo/");
            }
        };
        ctx.headerMap().put("X-Forwarded-Host", "www.example.com, proxy.internal");
        ctx.headerMap().put("Host", "10.0.0.8:8080");

        assert "www.example.com".equals(ctx.realHost());
    }

    @Test
    public void fromHost() {
        ContextEmpty ctx = new ContextEmpty() {
            @Override
            public URI uri() {
                return URI.create("http://10.0.0.8:8080/demo/");
            }
        };
        ctx.headerMap().put("Host", "www.example.com");

        assert "www.example.com".equals(ctx.realHost());
    }

    @Test
    public void fromUri() {
        ContextEmpty ctx = new ContextEmpty() {
            @Override
            public URI uri() {
                return URI.create("http://localhost:8080/demo/");
            }
        };

        assert "localhost".equals(ctx.realHost());
    }

    @Test
    public void unknownFallback() {
        ContextEmpty ctx = new ContextEmpty() {
            @Override
            public URI uri() {
                return URI.create("http://localhost:8080/demo/");
            }
        };
        ctx.headerMap().put("X-Forwarded-Host", "unknown");
        ctx.headerMap().put("Host", "www.example.com");

        assert "www.example.com".equals(ctx.realHost());
    }
}
