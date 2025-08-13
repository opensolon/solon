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
package webapp.demo1_handler;

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author noear 2023/4/19 created
 */
@Mapping("/sse")
@Managed
public class SseHandler implements Handler , EventListener<SseHandler.SseEvent> {
    private List<SseEvent> sseEvents = new ArrayList<>();

    @Override
    public void handle(Context ctx) throws Throwable {
        ctx.contentType("text/event-stream");
        ctx.charset("utf-8");

        while (true) {
            if (sseEvents.size() > 0) {
                Iterator<SseEvent> iterator = sseEvents.iterator();
                while (iterator.hasNext()) {
                    SseEvent event = iterator.next();
                    ctx.output(event.toString());
                    ctx.flush();
                }
            } else {
                Thread.sleep(1000);
            }
        }
    }

    @Override
    public void onEvent(SseEvent sseEvent) throws Throwable {
        sseEvents.add(sseEvent);
    }


    public static class SseEvent {

    }
}
