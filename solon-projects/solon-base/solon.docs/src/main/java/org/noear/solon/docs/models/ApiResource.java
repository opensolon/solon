package org.noear.solon.docs.models;


import org.noear.solon.Utils;
import org.noear.solon.core.handle.Action;

import java.util.function.Predicate;

/**
 * Swagger 资源信息
 * */
public class ApiResource implements Predicate<Action> {

    private transient Predicate<Action> condition;
    private String basePackage;

    public ApiResource() {

    }

    public ApiResource(String basePackage) {
        this.basePackage = basePackage;
    }

    public ApiResource(Predicate<Action> condition) {
        this.condition = condition;
    }

    public String basePackage() {
        return basePackage;
    }

    public ApiResource basePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    @Override
    public boolean test(Action action) {
        boolean isOk = true;
        if (Utils.isNotEmpty(basePackage)) {
            isOk &= action.controller().clz().getName().startsWith(basePackage);
        }

        if (condition != null) {
            isOk &= condition.test(action);
        }

        return isOk;
    }
}
