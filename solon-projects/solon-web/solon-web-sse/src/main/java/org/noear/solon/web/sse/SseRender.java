/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.web.sse;

import org.noear.solon.Solon;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

/**
 * sse 渲染器
 *
 * @author noear
 * @since 3.1
 */
public class SseRender implements Render {
    private static final SseRender instance = new SseRender();

    public static SseRender getInstance() {
        return instance;
    }

    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.startsWith(MimeType.TEXT_EVENT_STREAM_VALUE);
        }
    }

    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        SseEvent event;
        if (data instanceof SseEvent) {
            event = (SseEvent) data;
        } else {
            if (data instanceof String) {
                event = new SseEvent().data(data);
            } else {
                String json = Solon.app().renderOfJson().renderAndReturn(data, ctx);
                event = new SseEvent().data(json);
            }
        }

        return event.toString();
    }

    public static void pushSseHeaders(Context ctx) {
        ctx.contentType(MimeType.TEXT_EVENT_STREAM_UTF8_VALUE);
        ctx.keepAlive(60);
        ctx.cacheControl("no-cache");
    }

    @Override
    public void render(Object data, Context ctx) throws Throwable {
        if (ctx.isHeadersSent() == false) {
            pushSseHeaders(ctx);
        }

        if (data != null) {
            ctx.output(renderAndReturn(data, ctx));
        }
    }
}