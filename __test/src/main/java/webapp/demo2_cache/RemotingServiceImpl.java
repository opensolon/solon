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
package webapp.demo2_cache;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.data.annotation.Cache;

/**
 * @author noear 2022/3/22 created
 */
@Mapping(value = "demo2/rmt/sev", method = MethodType.SOCKET)
@Remoting
public class RemotingServiceImpl implements RemotingService {
    @Cache
    @Override
    public String hello(String name) {
        if (name == null) {
            name = "world";
        }

        return "Hello " + name + ": " + System.currentTimeMillis();
    }
}
