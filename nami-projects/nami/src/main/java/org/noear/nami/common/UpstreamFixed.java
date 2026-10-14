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
package org.noear.nami.common;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * 固定上游
 *
 * @author noear
 * @since 1.2
 */
public class UpstreamFixed implements Supplier<String> {
    protected List<String> servers;
    protected Iterator<String> tmp;

    protected String server1;

    public UpstreamFixed(List<String> servers) {
        this.servers = servers;
        if (servers.size() == 1) {
            server1 = servers.get(0);
        } else {
            tmp = servers.iterator();
        }
    }

    @Override
    public String get() {
        if (server1 == null) {
            if (tmp.hasNext() == false) {
                tmp = servers.iterator();
            }

            return tmp.next();
        } else {
            return server1;
        }
    }
}
