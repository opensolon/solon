package com.layjava.docs.javadoc.solon.wrap;

import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Example;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.4
 */
public class ApiParamImpl implements ApiParamAnno {
    ApiParam real;
    public ApiParamImpl(ApiParam real){
        this.real = real;
    }

    @Override
    public String name() {
        return real.name();
    }

    @Override
    public String value() {
        return real.value();
    }

    @Override
    public String defaultValue() {
        return real.defaultValue();
    }

    @Override
    public String allowableValues() {
        return real.allowableValues();
    }

    @Override
    public boolean required() {
        return real.required();
    }

    @Override
    public String access() {
        return real.access();
    }

    @Override
    public boolean allowMultiple() {
        return real.allowMultiple();
    }

    @Override
    public boolean hidden() {
        return real.hidden();
    }

    @Override
    public String dataType() {
        return "";
    }

    @Override
    public Class<?> dataTypeClass() {
        return Void.class;
    }

    @Override
    public String paramType() {
        return "";
    }

    @Override
    public String example() {
        return real.example();
    }

    @Override
    public Example examples() {
        return real.examples();
    }

    @Override
    public String type() {
        return real.type();
    }

    @Override
    public String format() {
        return real.format();
    }

    @Override
    public boolean allowEmptyValue() {
        return real.allowEmptyValue();
    }

    @Override
    public boolean readOnly() {
        return real.readOnly();
    }

    @Override
    public String collectionFormat() {
        return real.collectionFormat();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return real.annotationType();
    }
}
