package org.noear.solon.aot.hint;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 反射的提示
 *
 * @author songyinyin
 * @since 2.2
 */
public class ReflectionHints {

    private String name;

    private String reachableType;

    private Set<String> fields = new LinkedHashSet<>();

    private Set<ExecutableHint> methods = new LinkedHashSet<>();

    private Set<ExecutableHint> constructors = new LinkedHashSet<>();

    private Set<MemberCategory> memberCategories = new LinkedHashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReachableType() {
        return reachableType;
    }

    public void setReachableType(String reachableType) {
        this.reachableType = reachableType;
    }

    public Set<String> getFields() {
        return fields;
    }

    public Set<ExecutableHint> getMethods() {
        return methods;
    }

    public Set<ExecutableHint> getConstructors() {
        return constructors;
    }

    public Set<MemberCategory> getMemberCategories() {
        return memberCategories;
    }
}
