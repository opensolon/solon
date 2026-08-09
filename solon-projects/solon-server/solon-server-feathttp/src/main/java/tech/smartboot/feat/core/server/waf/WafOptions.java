/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server.waf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class WafOptions {
    private boolean enable = false;
    public static final String DESC = "Mysterious Power from the East Is Protecting This Area.";
    private final Set<String> allowMethods = new HashSet<>();
    private Set<String> denyMethods = new HashSet<>();

    private List<String> allowUriPrefixes = new ArrayList<>();

    private List<String> allowUriSuffixes = new ArrayList<>();

    public Set<String> getAllowMethods() {
        return allowMethods;
    }

    public WafOptions addAllowMethod(String method) {
        enable = true;
        allowMethods.add(method);
        return this;
    }

    public WafOptions addAllowMethods(Set<String> allowMethods) {
        enable = true;
        this.allowMethods.addAll(allowMethods);
        return this;
    }

    public Set<String> getDenyMethods() {
        return denyMethods;
    }

    public void setDenyMethods(Set<String> denyMethods) {
        enable = true;
        this.denyMethods = denyMethods;
    }

    public List<String> getAllowUriPrefixes() {
        return allowUriPrefixes;
    }

    public WafOptions addAllowUriPrefix(String prefix) {
        enable = true;
        allowUriPrefixes.add(prefix);
        return this;
    }

    public void setAllowUriPrefixes(List<String> allowUriPrefixes) {
        enable = true;
        this.allowUriPrefixes = allowUriPrefixes;
    }

    public List<String> getAllowUriSuffixes() {
        return allowUriSuffixes;
    }

    public void setAllowUriSuffixes(List<String> allowUriSuffixes) {
        enable = true;
        this.allowUriSuffixes = allowUriSuffixes;
    }

    boolean isEnable() {
        return enable;
    }
}
