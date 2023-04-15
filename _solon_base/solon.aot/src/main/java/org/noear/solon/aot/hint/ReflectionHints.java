package org.noear.solon.aot.hint;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 反射的提示
 *
 * @author songyinyin
 * @since 2023/4/6 15:18
 */
@Data
public class ReflectionHints {

    private String name;

    private String reachableType;

    private Set<String> fields = new LinkedHashSet<>();

    private Set<ExecutableHint> methods = new LinkedHashSet<>();

    private Set<ExecutableHint> constructors = new LinkedHashSet<>();

    private Set<MemberCategory> memberCategories = new LinkedHashSet<>();

}
