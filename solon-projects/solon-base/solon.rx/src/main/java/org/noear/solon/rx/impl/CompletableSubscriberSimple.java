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
package org.noear.solon.rx.impl;

import org.noear.solon.rx.CompletableSubscriber;

/**
 * 可完成的订阅者-简单实现
 *
 * @author noear
 * @since 2.9
 */
public class CompletableSubscriberSimple implements CompletableSubscriber {
    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

    }
}
