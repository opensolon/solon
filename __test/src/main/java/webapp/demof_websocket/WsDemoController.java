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
package webapp.demof_websocket;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Http;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Socket;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;

@Controller
public class WsDemoController {
    @Socket
    @Mapping("/demof/*/{id}")
    public void test(Context ctx, String id) throws Exception {
        if (ctx == null) {
            return;
        }

        System.out.println("WebSocket-PathVar-Mvc:Id: " + id);

        String msg = ctx.body();

        if (msg.equals("close")) {
            ctx.output("它叫我关了：" + msg);
            System.out.println("它叫我关了：" + msg + "!!!");
            ctx.close();//关掉
        } else {
            System.out.println(">>>>>>>>我收到了：" + msg + ": " + ctx.paramMap().toString());
            ctx.output("我收到了：" + msg + ": " + ctx.paramMap().toString());
        }
    }

    @Http
    @Mapping("/demof/websocket")
    public Object test_client(Context ctx, String id){
        ModelAndView mv = new ModelAndView("demof/websocket.ftl");
        mv.put("app_port", Solon.cfg().serverPort() + 10000);

        return mv;
    }
}
