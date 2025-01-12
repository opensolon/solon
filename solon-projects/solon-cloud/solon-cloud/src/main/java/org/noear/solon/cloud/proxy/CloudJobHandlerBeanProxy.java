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
package org.noear.solon.cloud.proxy;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;

/**
 * 云任务处理类原型代理
 *
 * @author noear
 * @since 2.2
 */
public class CloudJobHandlerBeanProxy implements CloudJobHandler {
    private BeanWrap target;

    public CloudJobHandlerBeanProxy(BeanWrap target) {
        this.target = target;
    }

    public BeanWrap getTarget() {
        return target;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        try {
            ((CloudJobHandler) target.get()).handle(ctx);
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);

            if (e instanceof CloudJobException) {
                throw e;
            } else {
                throw new CloudJobException(e);
            }
        }
    }
}
