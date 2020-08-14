package org.noear.solon.extend.aspect.asm;

public class MethodBean {

    public int access;
    public String methodName;
    public String methodDesc;

    public MethodBean() {
    }

    public MethodBean(int access, String methodName, String methodDesc) {
        this.access = access;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (!(obj instanceof MethodBean)){
            return false;
        }
        MethodBean bean = (MethodBean) obj;
        if (access == bean.access
                && methodName != null
                && bean.methodName != null
                && methodName.equals(bean.methodName)
                && methodDesc != null
                && bean.methodDesc != null
                && methodDesc.equals(bean.methodDesc)){
            return true;
        }
        return false;
    }
}