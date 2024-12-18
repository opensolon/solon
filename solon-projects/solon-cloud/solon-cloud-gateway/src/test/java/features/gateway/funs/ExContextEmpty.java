package features.gateway.funs;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.net.SocketAddress;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExNewRequest;
import org.noear.solon.cloud.gateway.exchange.ExNewResponse;
import org.noear.solon.cloud.gateway.properties.TimeoutProperties;
import org.noear.solon.core.handle.Context;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * 交换上下文空实现（用于单测）
 *
 * @author noear
 * @since 2.9
 */
public class ExContextEmpty implements ExContext {
    @Override
    public <T> T attr(String key) {
        return null;
    }

    @Override
    public void attrSet(String key, Object value) {

    }

    @Override
    public URI target() {
        return null;
    }

    @Override
    public void targetNew(URI target) {

    }

    @Override
    public URI targetNew() {
        return null;
    }

    @Override
    public TimeoutProperties timeout() {
        return null;
    }

    @Override
    public SocketAddress remoteAddress() {
        return null;
    }

    @Override
    public SocketAddress localAddress() {
        return null;
    }

    @Override
    public String realIp() {
        return "";
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String rawMethod() {
        return "";
    }

    @Override
    public URI rawURI() {
        return null;
    }

    @Override
    public String rawPath() {
        return "";
    }

    @Override
    public String rawQueryString() {
        return "";
    }

    @Override
    public String rawQueryParam(String key) {
        return "";
    }

    @Override
    public MultiMap rawQueryParams() {
        return null;
    }

    @Override
    public String rawHeader(String key) {
        return "";
    }

    @Override
    public MultiMap rawHeaders() {
        return null;
    }

    @Override
    public String rawCookie(String key) {
        return "";
    }

    @Override
    public Set<Cookie> rawCookies() {
        return Collections.emptySet();
    }

    @Override
    public Future<Buffer> rawBody() {
        return null;
    }

    @Override
    public ExNewRequest newRequest() {
        return null;
    }

    @Override
    public ExNewResponse newResponse() {
        return null;
    }

    @Override
    public Context toContext() {
        return null;
    }
}
