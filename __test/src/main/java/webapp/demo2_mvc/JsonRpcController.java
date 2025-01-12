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
package webapp.demo2_mvc;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.Context;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Mapping("/demo2/rpc/")
@Remoting
public class JsonRpcController {

    public Object json(Context ctx) {
//        ctx.headerMap().put("serialization","@avro");

        Map<String, Object> model = new HashMap<>();
        model.put("title", "dock");
        model.put("msg", "你好 world! in XController");

        model.put("bool", true);
        model.put("int", 12);
        model.put("long", 12L);
        model.put("double", 12.12D);
        model.put("date", new Date());
        model.put("local_date", LocalDate.now());

        return model;
    }

    public String header(Context ctx) {
        return ctx.header("name");
    }
}
