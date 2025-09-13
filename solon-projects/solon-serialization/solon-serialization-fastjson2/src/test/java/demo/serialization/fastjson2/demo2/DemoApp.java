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
package demo.serialization.fastjson2.demo2;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import org.noear.solon.Solon;
import org.noear.solon.serialization.fastjson2.Fastjson2RenderFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author noear 2021/10/12 created
 */
public class DemoApp {
    @JSONField(format = "yyyy-MM-dd")
    public Date date;
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args, app -> {
            app.onEvent(Fastjson2RenderFactory.class, e -> {
                initMvcJsonCustom(e);
            });
        });
    }

    //初始化json定制（需要在插件运行前定制）
    private static void initMvcJsonCustom(Fastjson2RenderFactory factory) {
        //示例1：通过转换器，做简单类型的定制
        factory.addConvertor(Date.class, s -> s.getTime());

        factory.addConvertor(LocalDate.class, s -> s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        factory.addConvertor(Long.class, s -> String.valueOf(s));

        //示例2：通过编码器，做复杂类型的原生定制（基于框架原生接口）
        factory.addEncoder(Date.class, (out, obj, o1, type, i) -> {
            out.writeInt64(((Date) obj).getTime());
        });

        //示例3：重置序列化特性（例，添加序列化null的特性）
        factory.addFeatures(JSONWriter.Feature.WriteMapNullValue);
    }
}
