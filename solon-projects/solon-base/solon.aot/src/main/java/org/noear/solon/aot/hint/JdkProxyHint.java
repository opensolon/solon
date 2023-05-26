package org.noear.solon.aot.hint;

import java.util.List;

/**
 * Jdk代理提示
 *
 * @author songyinyin
 * @since 2.2
 */
public class JdkProxyHint {

    private String reachableType;

    private List<String> interfaces;

    public String getReachableType() {
        return reachableType;
    }

    public void setReachableType(String reachableType) {
        this.reachableType = reachableType;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }
}
