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
package org.noear.solon.core.handle;

import org.noear.solon.Solon;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.util.DataThrowable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 返回值处理器默认处理
 *
 * @author noear
 * @since 3.1
 */
public class ReturnValueHandlerDefault implements ReturnValueHandler {
    static final Logger log = LoggerFactory.getLogger(ReturnValueHandlerDefault.class);

    public static final ReturnValueHandler INSTANCE = new ReturnValueHandlerDefault();

    @Override
    public boolean matched(Context ctx, Class<?> returnType) {
        return false;
    }

    @Override
    public void returnHandle(Context c, Object obj) throws Throwable {
        //可以通过before关掉render
        //
        obj = Solon.app().chains().postResult(c, obj);

        if (c.getRendered() == false) {
            c.result = obj;
        }


        if (obj instanceof DataThrowable) {
            //没有代理时，跳过 DataThrowable
            return;
        }

        if (obj == null) {
            //如果返回为空，且二次加载的结果为 null
            return;
        }

        if (obj instanceof Throwable) {
            if (c.remoting()) {
                //尝试推送异常，不然没机会记录；也可对后继做控制
                Throwable objE = (Throwable) obj;
                log.warn("Remoting handle failed: " + c.pathNew(), objE);

                if (c.getRendered() == false) {
                    if (objE instanceof StatusException) {
                        StatusException se = (StatusException) objE;
                        c.status(se.getCode(), se.getMessage());
                    } else {
                        c.status(500, objE.getMessage());
                    }
                    c.setRendered(true);
                    //c.render(obj);
                }
            } else {
                c.setHandled(false); //传递给 filter, 可以统一处理未知异常
                throw (Throwable) obj;
            }
        } else {
            if (c.getRendered() == false) {
                c.render(obj);
            }
        }
    }
}