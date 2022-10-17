package org.noear.solon.extend.springboot.web;

import org.noear.solon.core.handle.Context;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Web  响应体通知处理，为 Context 添加 result 便于日志
 *
 * <pre><code>
 * @Component
 * @ControllerAdvice("xxx.xxx.controller")
 * public class ResponseAdvice extends ResponseBodyAdviceSolon{
 *
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.5
 */
public class ResponseBodyAdviceSolon implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o != null) {
            Context c = Context.current();
            if (c != null) {
                c.result = o;
            }
        }

        return o;
    }
}
