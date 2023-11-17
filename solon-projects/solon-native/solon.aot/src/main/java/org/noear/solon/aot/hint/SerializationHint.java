package org.noear.solon.aot.hint;

/**
 * 序列化
 *
 * @author songyinyin
 * @since 2.2
 */
public class SerializationHint {

    private String name;

    private String reachableType;

    private String customTargetConstructorClass;

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

    public String getCustomTargetConstructorClass() {
        return customTargetConstructorClass;
    }

    public void setCustomTargetConstructorClass(String customTargetConstructorClass) {
        this.customTargetConstructorClass = customTargetConstructorClass;
    }

}
