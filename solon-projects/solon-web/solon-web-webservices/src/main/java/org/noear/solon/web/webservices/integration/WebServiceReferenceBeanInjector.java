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
package org.noear.solon.web.webservices.integration;

import org.noear.solon.core.BeanInjector;
import org.noear.solon.core.VarHolder;
import org.noear.solon.web.webservices.WebServiceReference;
import org.noear.solon.web.webservices.WebServiceHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 1.0
 */
public class WebServiceReferenceBeanInjector implements BeanInjector<WebServiceReference> {
    private Map<String, Object> cached = new ConcurrentHashMap<>();

    @Override
    public void doInject(VarHolder varH, WebServiceReference anno) {
        if (varH.getType().isInterface()) {

            String wsKey = anno.value() + "#" + varH.getType().getTypeName();
            Object wsProxy = cached.computeIfAbsent(wsKey, k -> WebServiceHelper.createWebClient(anno.value(), varH.getType()));

            varH.setValue(wsProxy);
        }
    }
}
