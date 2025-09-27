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
 * EntityConverter 转为 Renderer 接口
 *
 * @author noear
 * @since 3.6
 */
public class EntityConverterToRenderer implements Render {
    private final EntityConverter entityConverter;

    public EntityConverter getEntityConverter() {
        return entityConverter;
    }

    public EntityConverterToRenderer(EntityConverter entityConverter) {
        this.entityConverter = entityConverter;
    }

    @Override
    public String name() {
        return entityConverter.name();
    }

    @Override
    public boolean matched(Context ctx, String mime) {
        if (entityConverter.allowWrite()) {
            return entityConverter.canWrite(mime, ctx);
        } else {
            return false;
        }
    }

    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        return entityConverter.writeAndReturn(data, ctx);
    }

    @Override
    public void render(Object data, Context ctx) throws Throwable {
        entityConverter.write(data, ctx);
    }
}