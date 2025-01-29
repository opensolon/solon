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
package libs.gateway1;

import org.noear.solon.cloud.gateway.CloudGatewayFilter;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.rx.Completable;

//@Component
public class CloudGatewayFilterImpl implements CloudGatewayFilter {
    @Override
    public Completable doFilter(ExContext ctx, ExFilterChain chain) {
        String token = ctx.rawHeader("TOKEN");
        if (token == null) {
            ctx.newResponse().status(401);
            return Completable.complete();
        }

        return Completable.create(emitter -> {
            chain.doFilter(ctx)
                    .doOnError(err -> {
                        if (err instanceof StatusException) {
                            StatusException se = (StatusException) err;

                            ctx.newResponse().status(se.getCode());
                            emitter.onComplete();
                        } else {
                            ctx.newResponse().status(500);
                            emitter.onComplete();
                        }
                    })
                    .doOnComplete(() -> {
                        emitter.onComplete();
                    })
                    .subscribe();
        });
    }
}
