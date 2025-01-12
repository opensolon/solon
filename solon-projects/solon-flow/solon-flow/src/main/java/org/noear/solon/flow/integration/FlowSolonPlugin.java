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
package org.noear.solon.flow.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.core.Chain;
import org.noear.solon.flow.core.FlowEngine;

import java.util.List;

/**
 * @author noear
 * @since 3.0
 */
public class FlowSolonPlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        FlowEngine flowEngine = FlowEngine.newInstance();

        List<String> chainList = context.cfg().getList("solon.flow");
        for (String chainUri : chainList) {
            if (chainUri.contains("*")) {
                for (String uri : ResourceUtil.scanResources(chainUri)) {
                    flowEngine.load(Chain.parseByUri(uri));
                }
            } else {
                flowEngine.load(Chain.parseByUri(chainUri));
            }
        }

        context.wrapAndPut(FlowEngine.class, flowEngine);
    }
}
