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
package webapp.demoa_interceptor;

import org.noear.solon.annotation.Addition;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;


@Addition({BeforeHandlerBean.Before1.class, BeforeHandlerBean.Before2.class, BeforeHandlerBean.Before3.class, AfterHandler.class})
@Mapping("/demoa/trigger")
@Controller
public class DemoaHandler {
    @Mapping
    public void handle(Context context) throws Throwable {
        context.output(context.path());
    }
}
