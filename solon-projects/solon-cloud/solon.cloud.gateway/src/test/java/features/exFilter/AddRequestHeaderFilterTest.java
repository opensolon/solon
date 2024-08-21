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
package features.exFilter;

import features.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExNewRequest;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.rx.Completable;

import java.util.List;

/**
 * @author noear 2024/8/21 created
 */
public class AddRequestHeaderFilterTest {
    @Test
    public void testValidConfig() {
        ExFilter filter = RouteFactoryManager.buildFilter(
                "AddRequestHeader=app.ver,1");

        assert filter != null;

        ExNewRequest newRequest = new ExNewRequest();
        filter.doFilter(new ExContextEmpty() {
            @Override
            public ExNewRequest newRequest() {
                return newRequest;
            }
        }, ctx -> Completable.complete());

        assert newRequest.getHeaders().size() == 1;
        assert "1".equals(newRequest.getHeaders().get("app.ver").getFirstValue());
    }
}
