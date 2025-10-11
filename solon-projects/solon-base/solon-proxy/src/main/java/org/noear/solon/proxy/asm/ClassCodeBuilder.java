/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.proxy.asm;

import org.noear.solon.core.util.JavaUtil;
import org.objectweb.asm.*;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public class ClassCodeBuilder {
    public static final int ASM_JDK_VERSION() {
        if (JavaUtil.JAVA_MAJOR_VERSION < 11) {
            return Opcodes.V1_8;
        } else if (JavaUtil.JAVA_MAJOR_VERSION < 17) {
            return Opcodes.V11;
        } else if (JavaUtil.JAVA_MAJOR_VERSION < 21) {
            return Opcodes.V17;
        } else if (JavaUtil.JAVA_MAJOR_VERSION < 25) {
            return Opcodes.V21;
        } else {
            return Opcodes.V25;
        }
    }

    // 字段名
    private static final String FIELD_INVOCATIONHANDLER = "invocationHandler";
    // 方法名
    private static final String METHOD_SETTER = "setInvocationHandler";
    private static final String METHOD_INVOKE = "invokeInvocationHandler";
    private static final String METHOD_INVOKE_DESC = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String METHOD_FIELD_PREFIX = "method";

    public static Class<?> build(Class<?> targetClass, AsmProxyClassLoader classLoader) throws Exception {
        // 获取目标类的一些数据
        // ClassReader reader = new ClassReader(targetClass.getName());//某些情况下直接通过类名可能会获取不到数据
        ClassReader reader = null;
        String resourceName = targetClass.getName().replace('.', '/') + ".class";
        try (InputStream resourceStream = classLoader.getResourceAsStream(resourceName)) {
            reader = new ClassReader(resourceStream);
        }

        TargetClassVisitor targetClassVisitor = new TargetClassVisitor(classLoader, reader);
        // 判断是否是FINAL的
        if (targetClassVisitor.isFinal()) {
            throw new IllegalArgumentException("class is final");
        }
        // 开始生成代理类
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String newClassName = generateProxyClassName(targetClass);
        String newClassInnerName = newClassName.replace(".", "/");
        String targetClassName = targetClass.getName();
        String targetClassInnerName = Type.getInternalName(targetClass);
        // 创建类
        newClass(writer, newClassInnerName, targetClassInnerName);
        // 添加 InvocationHandler 字段
        addField(writer);
        // 添加 InvocationHandler 的setter
        addSetterMethod(writer, newClassInnerName);
        // 添加构造器，直接调用 super
        List<MethodDigest> constructors = targetClassVisitor.getConstructors();
        addConstructor(writer, constructors, targetClassInnerName);
        // 添加调用 InvocationHandler 的方法
        addInvokeMethod(writer, newClassInnerName);
        // 添加继承的public方法和目标类的protected、default方法
        Map<Integer, Integer> methodsMap = new HashMap<>();
        Map<Integer, Integer> declaredMethodsMap = new HashMap<>();
        int methodNameIndex = 0;

        if (targetClassVisitor.getMethods().size() > 0) {
            methodNameIndex = addMethod(writer, newClassInnerName, targetClass.getMethods(),
                    targetClassVisitor.getMethods(), true, methodNameIndex, methodsMap);
        }

        if (targetClassVisitor.getDeclaredMethods().size() > 0) {
            methodNameIndex = addMethod(writer, newClassInnerName, targetClass.getDeclaredMethods(),
                    targetClassVisitor.getDeclaredMethods(), false, methodNameIndex, declaredMethodsMap);
        }

        // 添加静态代码块的初始化
        if (methodNameIndex > 0) {
            addStaticInitBlock(writer, targetClassName, newClassInnerName, methodsMap, declaredMethodsMap);
        }

        // 生成二进制数据
        byte[] bytes = writer.toByteArray();

        // 从指定ClassLoader加载Class
        Class<?> proxyClass = classLoader.transfer2Class(bytes);

        return proxyClass;
    }



    /**
     * 生成代理类的类名生成规则
     */
    private static String generateProxyClassName(Class<?> targetClass) {
        return targetClass.getPackage().getName() + "." + targetClass.getSimpleName() + AsmProxy.PROXY_CLASSNAME_SUFFIX;
    }

    /**
     * 创建类
     */
    private static void newClass(ClassWriter writer, String newClassName, String targetClassName) throws Exception {
        int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL;

        writer.visit(ASM_JDK_VERSION(), access, newClassName, null, targetClassName, null);
    }

    /**
     * 添加 invocationHandler 字段
     */
    private static void addField(ClassWriter writer) throws Exception {
        FieldVisitor fieldVisitor = writer.visitField(Opcodes.ACC_PRIVATE, FIELD_INVOCATIONHANDLER,
                Type.getDescriptor(InvocationHandler.class), null, null);
        fieldVisitor.visitEnd();
    }

    /**
     * 添加 invocationHandler 的 setter 方法
     */
    private static void addSetterMethod(ClassWriter writer, String owner) throws Exception {
        String methodDesc = "(" + Type.getDescriptor(InvocationHandler.class) + ")V";
        MethodVisitor methodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, METHOD_SETTER, methodDesc, null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, owner, FIELD_INVOCATIONHANDLER,
                Type.getDescriptor(InvocationHandler.class));
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }

    /**
     * 添加构造器
     */
    private static void addConstructor(ClassWriter writer, List<MethodDigest> constructors,
                                       String targetClassInnerName) throws Exception {
        for (MethodDigest constructor : constructors) {
            Type[] argumentTypes = Type.getArgumentTypes(constructor.methodDesc);
            MethodVisitor methodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
                    constructor.methodDesc, null, null);

            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

            // 对每一个参数，都将对应局部变量表的位置入栈
            for (int i = 0; i < argumentTypes.length; i++) {
                Type argumentType = argumentTypes[i];
                if (argumentType.equals(Type.BYTE_TYPE)
                        || argumentType.equals(Type.BOOLEAN_TYPE)
                        || argumentType.equals(Type.CHAR_TYPE)
                        || argumentType.equals(Type.SHORT_TYPE)
                        || argumentType.equals(Type.INT_TYPE)) {
                    methodVisitor.visitVarInsn(Opcodes.ILOAD, i + 1);
                } else if (argumentType.equals(Type.LONG_TYPE)) {
                    methodVisitor.visitVarInsn(Opcodes.LLOAD, i + 1);
                } else if (argumentType.equals(Type.FLOAT_TYPE)) {
                    methodVisitor.visitVarInsn(Opcodes.FLOAD, i + 1);
                } else if (argumentType.equals(Type.DOUBLE_TYPE)) {
                    methodVisitor.visitVarInsn(Opcodes.DLOAD, i + 1);
                } else {
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, i + 1);
                }
            }

            // 调用super() 构造器
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, targetClassInnerName, "<init>", constructor.methodDesc, false);
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(argumentTypes.length + 1, argumentTypes.length + 1);

            methodVisitor.visitEnd();
        }
    }


    /**
     * 添加调用 invocationHandler 的 invoke 方法
     */
    private static void addInvokeMethod(ClassWriter writer, String owner) throws Exception {
        MethodVisitor methodVisitor = writer.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_VARARGS,
                METHOD_INVOKE, METHOD_INVOKE_DESC, null, null);
        methodVisitor.visitCode();
        // 异常处理
        Label start0 = new Label();
        Label end0 = new Label();

        methodVisitor.visitLabel(start0);
        // 取到 invocationHandler 字段并入栈
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, owner, FIELD_INVOCATIONHANDLER,
                Type.getDescriptor(InvocationHandler.class));
        // 将三个参数对应的局部变量表位置入栈
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 3);
        String handlerName = Type.getInternalName(InvocationHandler.class);
        String handlerMethodName = "invoke";
        String handlerDesc = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
        // 调用 invocationHandler.invoke 方法
        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, handlerName, handlerMethodName, handlerDesc, true);
        methodVisitor.visitInsn(Opcodes.ARETURN);
        // 正常返回
        methodVisitor.visitLabel(end0);
        methodVisitor.visitMaxs(4, 5);
        methodVisitor.visitEnd();
    }


    /**
     * 添加继承的方法或目标类本身的方法
     */
    private static int addMethod(ClassWriter writer, String newClassInnerName,
                                 Method[] methods, List<MethodDigest> methodDigests,
                                 boolean isPublic, int methodNameIndex,
                                 Map<Integer, Integer> map) throws Exception {
        for (int i = 0; i < methodDigests.size(); i++) {
            MethodDigest methodDigest = methodDigests.get(i);
            // 跳过final 和 static 的方法
            if ((methodDigest.access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL
                    || (methodDigest.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
                continue;
            }

            // 满足指定的修饰符
            int access = -1;
            if (isPublic) {
                // public 方法
                if ((methodDigest.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
                    access = Opcodes.ACC_PUBLIC;
                }
            } else {
                // protected 方法
                if ((methodDigest.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
                    access = Opcodes.ACC_PROTECTED;
                } else if ((methodDigest.access & Opcodes.ACC_PUBLIC) == 0
                        && (methodDigest.access & Opcodes.ACC_PROTECTED) == 0
                        && (methodDigest.access & Opcodes.ACC_PRIVATE) == 0) {
                    access = 0;
                }
            }

            if (access == -1) {
                continue;
            }

            // 匹配对应的方法
            int methodIndex = findSomeMethod(methods, methodDigest);
            if (methodIndex == -1) {
                continue;
            }

            // 将新建字段的后缀索引和对应方法数组真实的索引连接起来，方便后面初始化静态代码块时使用
            map.put(methodNameIndex, methodIndex);

            // 添加method对应的字段
            String fieldName = METHOD_FIELD_PREFIX + methodNameIndex;
            FieldVisitor fieldVisitor = writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                    fieldName, Type.getDescriptor(Method.class), null, null);
            fieldVisitor.visitEnd();

            // 添加方法的调用
            addMethod(writer, newClassInnerName, methodDigest, access, methodNameIndex);
            methodNameIndex++;
        }

        return methodNameIndex;
    }


    /**
     * 实现方法的调用
     */
    private static void addMethod(ClassWriter writer, String newClassInnerName,
                                  MethodDigest methodDigest, int access, int methodNameIndex) throws Exception {
        MethodVisitor methodVisitor = writer.visitMethod(access, methodDigest.methodName,
                methodDigest.methodDesc, null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        // 区分静态或者是非静态方法调用
        if ((methodDigest.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        }
        // 获取新建的方法字段
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, newClassInnerName,
                METHOD_FIELD_PREFIX + methodNameIndex, Type.getDescriptor(Method.class));
        Type[] argumentTypes = Type.getArgumentTypes(methodDigest.methodDesc);
        // 实例化数组，容量对应方法的参数个数
        methodVisitor.visitIntInsn(Opcodes.BIPUSH, argumentTypes.length);
        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Object.class));
        // 计算局部变量表的位置，其中 double 和 long 占用两个槽，其他占用一个槽
        int start = 1;
        int stop = start;
        // 布局变量表入栈，基本类型需要装箱
        for (int i = 0; i < argumentTypes.length; i++) {
            Type type = argumentTypes[i];
            if (type.equals(Type.BYTE_TYPE)) {
                stop = start + 1;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.ILOAD, start);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Byte.class),
                        "valueOf", "(B)Ljava/lang/Byte;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            } else if (type.equals(Type.SHORT_TYPE)) {
                stop = start + 1;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.ILOAD, start);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Short.class),
                        "valueOf", "(S)Ljava/lang/Short;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            } else if (type.equals(Type.CHAR_TYPE)) {
                stop = start + 1;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.ILOAD, start);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Character.class),
                        "valueOf", "(C)Ljava/lang/Character;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            } else if (type.equals(Type.INT_TYPE)) {
                stop = start + 1;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.ILOAD, start);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Integer.class),
                        "valueOf", "(I)Ljava/lang/Integer;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            } else if (type.equals(Type.FLOAT_TYPE)) {
                stop = start + 1;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.FLOAD, start);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Float.class),
                        "valueOf", "(F)Ljava/lang/Float;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            } else if (type.equals(Type.DOUBLE_TYPE)) {
                stop = start + 2;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.DLOAD, start);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Double.class),
                        "valueOf", "(D)Ljava/lang/Double;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            } else if (type.equals(Type.LONG_TYPE)) {
                stop = start + 2;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.LLOAD, start);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Long.class),
                        "valueOf", "(J)Ljava/lang/Long;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            } else if (type.equals(Type.BOOLEAN_TYPE)) {
                stop = start + 1;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.ILOAD, start);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Boolean.class),
                        "valueOf", "(Z)Ljava/lang/Boolean;", false);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            } else {
                stop = start + 1;
                methodVisitor.visitInsn(Opcodes.DUP);
                // 放入数组的下标位置
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, i);
                // 局部变量表的索引
                methodVisitor.visitVarInsn(Opcodes.ALOAD, start);
                methodVisitor.visitInsn(Opcodes.AASTORE);
            }
            start = stop;
        }
        // 调用 invokeInvocationHandler 方法
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, newClassInnerName,
                METHOD_INVOKE, METHOD_INVOKE_DESC, false);
        // 处理返回情况，基本类型需要拆箱
        Type returnType = Type.getReturnType(methodDigest.methodDesc);
        if (returnType.equals(Type.BYTE_TYPE)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Byte.class));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Byte.class),
                    "byteValue", "()B", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType.equals(Type.BOOLEAN_TYPE)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Boolean.class));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Boolean.class),
                    "booleanValue", "()Z", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType.equals(Type.CHAR_TYPE)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Character.class));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Character.class),
                    "charValue", "()C", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType.equals(Type.SHORT_TYPE)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Short.class));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Short.class),
                    "shortValue", "()S", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType.equals(Type.INT_TYPE)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Integer.class));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Integer.class),
                    "intValue", "()I", false);
            methodVisitor.visitInsn(Opcodes.IRETURN);
        } else if (returnType.equals(Type.LONG_TYPE)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Long.class));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Long.class),
                    "longValue", "()J", false);
            methodVisitor.visitInsn(Opcodes.LRETURN);
        } else if (returnType.equals(Type.FLOAT_TYPE)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Float.class));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Float.class),
                    "floatValue", "()F", false);
            methodVisitor.visitInsn(Opcodes.FRETURN);
        } else if (returnType.equals(Type.DOUBLE_TYPE)) {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Double.class));
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Double.class),
                    "doubleValue", "()D", false);
            methodVisitor.visitInsn(Opcodes.DRETURN);
        } else if (returnType.equals(Type.VOID_TYPE)) {
            methodVisitor.visitInsn(Opcodes.RETURN);
        } else {
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, returnType.getInternalName());
            methodVisitor.visitInsn(Opcodes.ARETURN);
        }
        methodVisitor.visitMaxs(8, 37);
        methodVisitor.visitEnd();
    }

    /**
     * 添加静态初始代码块
     */
    private static void addStaticInitBlock(ClassWriter writer, String targetClassName,
                                           String newClassInnerName, Map<Integer, Integer> methodsMap,
                                           Map<Integer, Integer> declaredMethodsMap) throws Exception {
        String exceptionClassName = Type.getInternalName(ClassNotFoundException.class);
        MethodVisitor methodVisitor = writer.visitMethod(Opcodes.ACC_STATIC, "<clinit>",
                "()V", null, null);
        methodVisitor.visitCode();
        // 开始异常处理
        Label label0 = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, exceptionClassName);
        methodVisitor.visitLabel(label0);

        // 给继承的方法添加对应的字段初始化
        for (Map.Entry<Integer, Integer> entry : methodsMap.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            methodVisitor.visitLdcInsn(targetClassName);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Class.class),
                    "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Class.class),
                    "getMethods", "()[Ljava/lang/reflect/Method;", false);
            methodVisitor.visitIntInsn(Opcodes.SIPUSH, value);
            methodVisitor.visitInsn(Opcodes.AALOAD);
            methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, newClassInnerName,
                    METHOD_FIELD_PREFIX + key, Type.getDescriptor(Method.class));
        }

        // 给目标类本身的方法添加对应的字段初始化
        for (Map.Entry<Integer, Integer> entry : declaredMethodsMap.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            methodVisitor.visitLdcInsn(targetClassName);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Class.class),
                    "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Class.class),
                    "getDeclaredMethods", "()[Ljava/lang/reflect/Method;", false);
            methodVisitor.visitIntInsn(Opcodes.SIPUSH, value);
            methodVisitor.visitInsn(Opcodes.AALOAD);
            methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, newClassInnerName,
                    METHOD_FIELD_PREFIX + key, Type.getDescriptor(Method.class));
        }

        methodVisitor.visitLabel(label1);
        Label label3 = new Label();
        methodVisitor.visitJumpInsn(Opcodes.GOTO, label3);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{exceptionClassName});
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 0);
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, exceptionClassName,
                "printStackTrace", "()V", false);
        methodVisitor.visitLabel(label3);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(2, 1);
        methodVisitor.visitEnd();
    }

    /**
     * 找到相等方法的索引
     */
    private static int findSomeMethod(Method[] methods, MethodDigest methodDigest) {
        for (int i = 0; i < methods.length; i++) {
            if (equalsMethod(methods[i], methodDigest)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断 {@link Method} 和 {@link MethodDigest} 是否相等
     */
    private static boolean equalsMethod(Method method, MethodDigest methodDigest) {
        if (method == null && methodDigest == null) {
            return true;
        }
        if (method == null || methodDigest == null) {
            return false;
        }
        try {
            if (!method.getName().equals(methodDigest.methodName)) {
                return false;
            }
            if (!Type.getReturnType(method).equals(Type.getReturnType(methodDigest.methodDesc))) {
                return false;
            }
            Type[] argumentTypes1 = Type.getArgumentTypes(method);
            Type[] argumentTypes2 = Type.getArgumentTypes(methodDigest.methodDesc);
            if (argumentTypes1.length != argumentTypes2.length) {
                return false;
            }
            for (int i = 0; i < argumentTypes1.length; i++) {
                if (!argumentTypes1[i].equals(argumentTypes2[i])) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
