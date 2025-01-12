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
package features.gateway.funs.exFilter;

import features.gateway.funs.ExContextEmpty;
import org.junit.jupiter.api.Test;
import org.noear.solon.cloud.gateway.exchange.ExFilter;
import org.noear.solon.cloud.gateway.exchange.ExNewResponse;
import org.noear.solon.cloud.gateway.route.RouteFactoryManager;
import org.noear.solon.rx.Completable;
import org.noear.solon.rx.impl.CompletableSubscriberSimple;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/8/21 created
 */
@SolonTest
public class RemoveResponseHeaderFilterTest {
    @Test
    public void testValidConfig() {
        ExFilter filter = RouteFactoryManager.buildFilter(
                "RemoveResponseHeader=app.ver,1");

        assert filter != null;

        ExNewResponse newResponse = new ExNewResponse();
        newResponse.headerAdd("a", "1");
        newResponse.headerAdd("app.ver", "1");

        assert newResponse.getHeaders().size() == 2;

        filter.doFilter(new ExContextEmpty() {
            @Override
            public ExNewResponse newResponse() {
                return newResponse;
            }
        }, ctx -> Completable.complete()).subscribe(new CompletableSubscriberSimple());

        assert newResponse.getHeaders().size() == 1;
    }
}
