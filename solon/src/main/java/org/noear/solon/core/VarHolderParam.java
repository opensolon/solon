package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VarHolderParam implements VarHolder{
    protected Parameter p;
    protected CompletableFuture<Object> future;
    public VarHolderParam(Parameter p){
        this.p = p;
        this.future = new CompletableFuture<>();
    }

    @Override
    public ParameterizedType getGenericType() {
        return null;
    }

    @Override
    public Class<?> getType() {
        return p.getType();
    }

    @Override
    public Annotation[] getAnnoS() {
        return p.getAnnotations();
    }

    @Override
    public void setValue(Object val) {
        future.complete(val);
    }

    public Object getValue() throws InterruptedException, ExecutionException {
        return future.get();
    }
}
