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
package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * App load end 事件
 *
 * @author noear
 * @since 1.1
 * */
public class AppLoadEndEvent  extends AppEvent {
    public AppLoadEndEvent(SolonApp app) {
        super(app);
    }
}
