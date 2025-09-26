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

/**
 * 用于替代 ActionExecuteHandlerDefault
 *
 * @author noear
 * @since 3.6
 */
public class EntityConverterDefault extends AbstractEntityConverter {
    static final EntityConverter _instance = new EntityConverterDefault();

    public static EntityConverter instance() {
        return _instance;
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
    public boolean canRead(Context ctx, String mime) {
        return true;
    }
}
