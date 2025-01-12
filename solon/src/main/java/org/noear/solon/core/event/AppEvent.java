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
import org.noear.solon.core.AppContext;

/**
 * 应用事件
 *
 * @author noear
 * @since 1.11
 */
public abstract class AppEvent {
    private final SolonApp app;

    /**
     * 应用实例
     * */
    public SolonApp app() {
        return app;
    }

    /**
     * 应用上下文
     * */
    public AppContext context(){
        return app.context();
    }

    public AppEvent(SolonApp app){
        this.app = app;
    }
}
