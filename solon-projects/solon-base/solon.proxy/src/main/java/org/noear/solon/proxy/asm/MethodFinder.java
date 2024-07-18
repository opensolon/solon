package org.noear.solon.proxy.asm;

import org.objectweb.asm.Opcodes;

/**
 * @author noear
 * @since 2.8
 */
public class MethodFinder {
    /**
     * 是否允许函数处理
     */
    public static boolean allowMethod(int access) {
        if ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC ||
                (access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL ||
                (access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT ||
                (access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE ||
                (access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
            //静态 或 只读 或 私有 或 虚拟；不需要重写
            return false;
        } else {
            return true;
        }
    }
}
