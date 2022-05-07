package org.noear.solon.cloud.opentracing.integration;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.tag.Tag;

import java.util.Map;

/**
 * @author noear
 * @since 1.7
 */
public class SpanEmpty implements Span {
    public static final Span instance = new SpanEmpty();

    @Override
    public SpanContext context() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Span setTag(String key, String value) {
        return instance;
    }

    @Override
    public Span setTag(String key, boolean value) {
        return instance;
    }

    @Override
    public Span setTag(String key, Number value) {
        return instance;
    }

    @Override
    public <T> Span setTag(Tag<T> tag, T value) {
        return instance;
    }

    @Override
    public Span log(Map<String, ?> fields) {
        return instance;
    }

    @Override
    public Span log(long timestampMicroseconds, Map<String, ?> fields) {
        return instance;
    }

    @Override
    public Span log(String event) {
        return instance;
    }

    @Override
    public Span log(long timestampMicroseconds, String event) {
        return instance;
    }

    @Override
    public Span setBaggageItem(String key, String value) {
        return instance;
    }

    @Override
    public String getBaggageItem(String key) {
        return null;
    }

    @Override
    public Span setOperationName(String operationName) {
        return instance;
    }

    @Override
    public void finish() {

    }

    @Override
    public void finish(long finishMicros) {

    }
}
