package org.noear.solon.data.sqlink.api;


import io.github.kiryu1223.expressionTree.delegate.Action1;
import org.noear.solon.data.sqlink.api.crud.read.LQuery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.cast;

public class Filter {

    private final Map<Class<?>, Map<String, Action1<LQuery<?>>>> onSelect = new LinkedHashMap<>();

    public <T> void onSelect(String filterId, Class<T> targetType, Action1<LQuery<T>> action) {
        Map<String, Action1<LQuery<?>>> actionMap = onSelect.computeIfAbsent(targetType, k -> new LinkedHashMap<>());
        actionMap.put(filterId, cast(action));
    }

    public <T> void invokeOnSelect(Class<T> targetType, LQuery<T> query, List<String> ignoreFilterIds, boolean isIgnoreFilterAll) {
        if (isIgnoreFilterAll) return;
        for (Map.Entry<Class<?>, Map<String, Action1<LQuery<?>>>> entry : onSelect.entrySet()) {
            Class<?> key = entry.getKey();
            if (key.isAssignableFrom(targetType)) {
                Map<String, Action1<LQuery<?>>> value = entry.getValue();
                value.forEach((k, v) -> {
                    if (ignoreFilterIds.isEmpty() || !ignoreFilterIds.contains(k)) {
                        v.invoke(query);
                    }
                });
            }
        }
    }
}
