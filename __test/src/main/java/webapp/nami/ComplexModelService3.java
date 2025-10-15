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
package webapp.nami;


import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.nami.annotation.NamiBody;
import org.noear.nami.annotation.NamiMapping;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.coder.snack4.Snack4Decoder;
import org.noear.nami.coder.snack4.Snack4Encoder;
import org.noear.solon.Utils;

/**
 * @author noear 2022/12/6 created
 */
@NamiClient(name="local", path="/nami/ComplexModelService3/", headers="TOKEN=a")
public interface ComplexModelService3 extends Filter {
    @NamiMapping("PUT")
    void save(@NamiBody ComplexModel model);

    @NamiMapping("GET api/1.0.1")
    ComplexModel read(Integer modelId);

    //自带个过滤器，过滤自己：）
    default Result doFilter(Invocation inv) throws Throwable{
        inv.headers.put("Token", "Xxx");
        inv.headers.put("TraceId", Utils.guid());
        inv.config.setDecoder(Snack4Decoder.instance);
        inv.config.setEncoder(Snack4Encoder.instance);

        return inv.invoke();
    }
}
