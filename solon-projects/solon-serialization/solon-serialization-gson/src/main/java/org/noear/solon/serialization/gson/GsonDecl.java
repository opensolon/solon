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
package org.noear.solon.serialization.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.noear.solon.Utils;
import org.noear.solon.core.util.Assert;

/**
 * Gson 声明
 *
 * @author noear
 * @since 3.6
 */
public class GsonDecl {
    private GsonBuilder builder;
    private Gson gson;

    public GsonBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(GsonBuilder builder) {
        Assert.notNull(builder, "builder can't be null");
        this.builder = builder;
    }

    public GsonDecl() {
        builder = new GsonBuilder();
    }


    /// //////

    protected Gson getGson() {
        if (gson == null) {
            Utils.locker().lock();
            try {
                if (gson == null) {
                    gson = builder.create();
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return gson;
    }

    protected void refresh(boolean force) {
        if (force) {
            gson = builder.create();
        } else {
            if (gson == null) {
                gson = builder.create();
            }
        }
    }
}