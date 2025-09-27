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

import org.noear.solon.core.wrap.MethodWrap;

/**
 * ActionExecuteHandler 接口转为 EntityConverter
 *
 * @author noear
 * @since 3.6
 */
public class EntityConverterFromExecutor implements EntityConverter {
    private final ActionExecuteHandler executeHandler;


    public EntityConverterFromExecutor(ActionExecuteHandler executeHandler) {
        this.executeHandler = executeHandler;
    }

    @Override
    public boolean isInstance(Class<?> clz) {
        return clz.isInstance(this) || clz.isInstance(executeHandler);
    }

    @Override
    public boolean allowWrite() {
        return false;
    }

    @Override
    public boolean canWrite(String mime, Context ctx) {
        return false;
    }

    @Override
    public String writeAndReturn(Object data, Context ctx) throws Throwable {
        return null;
    }

    @Override
    public void write(Object data, Context ctx) throws Throwable {

    }

    @Override
    public boolean allowRead() {
        return true;
    }

    @Override
    public boolean canRead(Context ctx, String mime) {
        return executeHandler.matched(ctx, mime);
    }

    @Override
    public Object[] read(Context ctx, Object target, MethodWrap mWrap) throws Throwable {
        return executeHandler.resolveArguments(ctx, target, mWrap);
    }
}