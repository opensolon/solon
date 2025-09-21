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
package org.noear.solon.serialization.hessian;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

//不要要入参，方便后面多视图混用
//
 /**
  *
  * @deprecated 3.6
  * */
 @Deprecated
public class HessianRender implements Render {
     private final HessianEntityConverter entityConverter;

     public HessianRender(HessianEntityConverter entityConverter) {
         this.entityConverter = entityConverter;
     }

     public HessianBytesSerializer getSerializer() {
         return entityConverter.getSerializer();
     }

     @Override
     public boolean matched(Context ctx, String mime) {
         return entityConverter.canWrite(mime, ctx);
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