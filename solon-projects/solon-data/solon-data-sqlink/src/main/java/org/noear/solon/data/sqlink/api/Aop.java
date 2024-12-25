package org.noear.solon.data.sqlink.api;


import io.github.kiryu1223.expressionTree.delegate.Action1;
import org.noear.solon.data.sqlink.api.crud.read.LQuery;

import java.util.HashMap;
import java.util.Map;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.cast;

public class Aop {

    private final Map<Class<?>, Action1<LQuery<?>>> onSelect = new HashMap<>();

    public <T> void onSelect(Class<T> c, Action1<LQuery<T>> query) {
        onSelect.put(c, cast(query));
    }

    public <T> Action1<LQuery<T>> getOnSelectByType(Class<T> c) {
        return cast(onSelect.get(c));
    }
}
