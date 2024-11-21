/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.nami.coder.fastjson.integration;

import org.noear.nami.NamiManager;
import org.noear.nami.coder.fastjson.FastjsonDecoder;
import org.noear.nami.coder.fastjson.FastjsonEncoder;
import org.noear.nami.coder.fastjson.FastjsonTypeEncoder;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.2
 */
public class NamiFastjsonPlugin implements Plugin {
    @Override
    public void start(AppContext context) {
        NamiManager.reg(FastjsonDecoder.instance);
        NamiManager.reg(FastjsonEncoder.instance);
        NamiManager.reg(FastjsonTypeEncoder.instance);
    }
}
