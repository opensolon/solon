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
package org.noear.solon.cloud.telemetry.integration;


import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.StatusCode;

import java.util.concurrent.TimeUnit;

/**
 * Span 伪装类
 *
 * @author noear
 * @since 3.0
 */
public class SpanSimulate implements Span {
    private static final Span instance = new SpanSimulate();

    public static Span getInstance() {
        return instance;
    }

    @Override
    public <T> Span setAttribute(AttributeKey<T> attributeKey, T t) {
        return null;
    }

    @Override
    public Span addEvent(String s, Attributes attributes) {
        return null;
    }

    @Override
    public Span addEvent(String s, Attributes attributes, long l, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Span setStatus(StatusCode statusCode, String s) {
        return null;
    }

    @Override
    public Span recordException(Throwable throwable, Attributes attributes) {
        return null;
    }

    @Override
    public Span updateName(String s) {
        return null;
    }

    @Override
    public void end() {

    }

    @Override
    public void end(long l, TimeUnit timeUnit) {

    }

    @Override
    public SpanContext getSpanContext() {
        return null;
    }

    @Override
    public boolean isRecording() {
        return false;
    }
}
