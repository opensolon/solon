/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.serialization.hessian;

import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Map;

public class HessianActionExecutor extends ActionExecuteHandlerDefault {
    private HessianBytesSerializer serializer = new HessianBytesSerializer();

    public HessianBytesSerializer getSerializer() {
        return serializer;
    }

    @Override
    public boolean matched(Context ctx, String ct) {
        return serializer.matched(ctx, ct);
    }

    @Override
    protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
       return serializer.deserializeFromBody(ctx);
    }

    /**
     * @since 1.11 增加 requireBody 支持
     * */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if(p.isRequiredPath() || p.isRequiredCookie() || p.isRequiredHeader()){
            //如果是 path、cookie, header
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (p.isRequiredBody() == false && ctx.paramMap().containsKey(p.getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (bodyObj == null) {
            return null;
        } else {
            if(p.isRequiredBody()){
                return bodyObj;
            }

            if (bodyObj instanceof Map) {
                Map<String, Object> tmp = (Map<String, Object>) bodyObj;

                if (tmp.containsKey(p.getName())) {
                    return tmp.get(p.getName());
                }
            }

            return null;
        }
    }
}
