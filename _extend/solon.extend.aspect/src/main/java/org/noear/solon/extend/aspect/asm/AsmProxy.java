package org.noear.solon.extend.aspect.asm;


import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsmProxy {
    public static final int ASM_VERSION = Opcodes.ASM8;
    public static final int ASM_JDK_VERSION = Opcodes.V1_8;

    // 动态生成代理类的前缀
    public static final String PROXY_CLASSNAME_PREFIX = "$Proxy_";
    // 字段名
    private static final String FIELD_INVOCATIONHANDLER = "invocationHandler";
    // 方法名
    private static final String METHOD_SETTER = "setInvocationHandler";
    private static final String METHOD_INVOKE = "invokeInvocationHandler";
    private static final String METHOD_INVOKE_DESC = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
    private static final String METHOD_FIELD_PREFIX = "method";
    // 缓存容器，防止生成同一个Class文件在同一个ClassLoader加载崩溃的问题
    private static final Map<String, Class<?>> proxyClassCache = new HashMap<>();

    /**
     * 缓存已经生成的代理类的Class，key值根据 classLoader 和 targetClass 共同决定
     */
    private static void saveProxyClassCache(AsmProxyClassLoader classLoader, Class<?> targetClass, Class<?> proxyClass) {
        String key = classLoader.toString() + "_" + targetClass.getName();
        proxyClassCache.put(key, proxyClass);
    }

    /**
     * 从缓存中取得代理类的Class，如果没有则返回 null
     */
    private static Class<?> getProxyClassCache(AsmProxyClassLoader classLoader, Class<?> targetClass) {
        String key = classLoader.toString() + "_" + targetClass.getName();
        return proxyClassCache.get(key);
    }

    /**
     * 返回一个动态创建的代理类，此类继承自 targetClass
     *
     * @param invocationHandler 代理类中每一个方法调用时的回调接口
     * @param targetClass       被代理对象
     * @param targetConstructor 被代理对象的某一个构造器，用于决定代理对象实例化时采用哪一个构造器
     * @param targetParam       被代理对象的某一个构造器的参数，用于实例化构造器
     * @return
     */
    public static Object newProxyInstance(InvocationHandler invocationHandler,
                                          Class<?> targetClass,
                                          Constructor<?> targetConstructor,
                                          Object... targetParam) {
        if (targetClass == null || invocationHandler == null) {
            throw new IllegalArgumentException("argument is null");
        }

        AsmProxyClassLoader classLoader = AsmProxyClassLoader.global();

        try {
            // 查看是否有缓存
            Class<?> proxyClass = getProxyClassCache(classLoader, targetClass);
            if (proxyClass != null) {
                // 实例化代理对象
                return newInstance(proxyClass, invocationHandler, targetConstructor, targetParam);
            }
            // 获取目标类的一些数据
            ClassReader reader = new ClassReader(targetClass.getName());
            TargetClassVisitor targetClassVisitor = new TargetClassVisitor();
            reader.accept(targetClassVisitor, ClassReader.SKIP_DEBUG);
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
            List<MethodBean> constructors = targetClassVisitor.getConstructors();
            addConstructor(writer, constructors, targetClassInnerName);
            // 添加调用 InvocationHandler 的方法
            addInvokeMethod(writer, newClassInnerName);
            // 添加继承的public方法和目标类的protected、default方法
            List<MethodBean> methods = targetClassVisitor.getMethods();
            List<MethodBean> declaredMethods = targetClassVisitor.getDeclaredMethods();
            Map<Integer, Integer> methodsMap = new HashMap<>();
            Map<Integer, Integer> declaredMethodsMap = new HashMap<>();
            int methodNameIndex = 0;
            methodNameIndex = addMethod(writer, newClassInnerName, targetClass.getMethods(),
                    methods, true, methodNameIndex, methodsMap);
            addMethod(writer, newClassInnerName, targetClass.getDeclaredMethods(),
                    declaredMethods, false, methodNameIndex, declaredMethodsMap);
            // 添加静态代码块的初始化
            addStaticInitBlock(writer, targetClassName, newClassInnerName, methodsMap, declaredMethodsMap);
            // 生成二进制数据
            byte[] bytes = writer.toByteArray();
            // 保存到文件，用于debug调试
//            File outputFile = new File("/Users/jm/Downloads/Demo/" + newClassInnerName + ".class");
//            save2File(outputFile, bytes);
            // 从指定ClassLoader加载Class
            proxyClass = classLoader.transfer2Class(bytes);
            // 缓存
            saveProxyClassCache(classLoader, targetClass, proxyClass);
            // 实例化代理对象
            return newInstance(proxyClass, invocationHandler, targetConstructor, targetParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成代理类的类名生成规则
     */
    private static String generateProxyClassName(Class<?> targetClass) {
        return targetClass.getPackage().getName() + "." + PROXY_CLASSNAME_PREFIX + targetClass.getSimpleName();
    }

    /**
     * 根据被代理类的构造器，构造代理类对象。生成代理类的实例时调用其setter方法
     */
    private static Object newInstance(Class<?> proxyClass,
                                      InvocationHandler invocationHandler,
                                      Constructor<?> targetConstructor,
                                      Object... targetParam) throws Exception {
        Class<?>[] parameterTypes = targetConstructor.getParameterTypes();
        Constructor<?> constructor = proxyClass.getConstructor(parameterTypes);
        Object instance = constructor.newInstance(targetParam);
        Method setterMethod = proxyClass.getDeclaredMethod(METHOD_SETTER, InvocationHandler.class);
        setterMethod.setAccessible(true);
        setterMethod.invoke(instance, invocationHandler);
        return instance;
    }

    /**
     * 创建类
     */
    private static void newClass(ClassWriter writer, String newClassName, String targetClassName) throws Exception {
        int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL;
        writer.visit(ASM_JDK_VERSION, access, newClassName, null, targetClassName, null);
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
    private static void addConstructor(ClassWriter writer, List<MethodBean> constructors,
                                       String targetClassInnerName) throws Exception {
        for (MethodBean constructor : constructors) {
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
     * 添加调用 invocationHandler 的 invoke 方法
     */
//    private static void addInvokeMethod(ClassWriter writer, String owner) throws Exception {
//        MethodVisitor methodVisitor = writer.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_VARARGS,
//                METHOD_INVOKE, METHOD_INVOKE_DESC, null, null);
//        methodVisitor.visitCode();
//        // 异常处理
//        Label label0 = new Label();
//        Label label1 = new Label();
//        Label label2 = new Label();
//        methodVisitor.visitTryCatchBlock(label0, label1, label2, Type.getInternalName(Throwable.class));
//        methodVisitor.visitLabel(label0);
//        // 取到 invocationHandler 字段并入栈
//        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
//        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, owner, FIELD_INVOCATIONHANDLER,
//                Type.getDescriptor(InvocationHandler.class));
//        // 将三个参数对应的局部变量表位置入栈
//        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
//        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
//        methodVisitor.visitVarInsn(Opcodes.ALOAD, 3);
//        String handlerName = Type.getInternalName(InvocationHandler.class);
//        String handlerMethodName = "invoke";
//        String handlerDesc = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
//        // 调用 invocationHandler.invoke 方法
//        methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, handlerName, handlerMethodName, handlerDesc, true);
//        // 正常返回
//        methodVisitor.visitLabel(label1);
//        methodVisitor.visitInsn(Opcodes.ARETURN);
//        // 异常处理
//        methodVisitor.visitLabel(label2);
//        methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1,
//                new Object[]{Type.getInternalName(Throwable.class)});
//        methodVisitor.visitVarInsn(Opcodes.ASTORE, 4);
//        methodVisitor.visitVarInsn(Opcodes.ALOAD, 4);
//        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Throwable.class),
//                "printStackTrace", "()V", false);
//        methodVisitor.visitInsn(Opcodes.ACONST_NULL);
//        methodVisitor.visitInsn(Opcodes.ARETURN);
//        methodVisitor.visitMaxs(4, 5);
//        methodVisitor.visitEnd();
//    }

    /**
     * 添加继承的方法或目标类本身的方法
     */
    private static int addMethod(ClassWriter writer, String newClassInnerName,
                                 Method[] methods, List<MethodBean> methodBeans,
                                 boolean isPublic, int methodNameIndex,
                                 Map<Integer, Integer> map) throws Exception {
        for (int i = 0; i < methodBeans.size(); i++) {
            MethodBean methodBean = methodBeans.get(i);
            // 跳过final 和 static 的方法
            if ((methodBean.access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL
                    || (methodBean.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
                continue;
            }
            // 满足指定的修饰符
            int access = -1;
            if (isPublic) {
                // public 方法
                if ((methodBean.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
                    access = Opcodes.ACC_PUBLIC;
                }
            } else {
                // protected 方法
                if ((methodBean.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
                    access = Opcodes.ACC_PROTECTED;
                } else if ((methodBean.access & Opcodes.ACC_PUBLIC) == 0
                        && (methodBean.access & Opcodes.ACC_PROTECTED) == 0
                        && (methodBean.access & Opcodes.ACC_PRIVATE) == 0) {
                    access = 0;
                }
            }
            if (access == -1) {
                continue;
            }
            // 匹配对应的方法
            int methodIndex = findSomeMethod(methods, methodBean);
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
            addMethod(writer, newClassInnerName, methodBean, access, methodNameIndex);
            methodNameIndex++;
        }
        return methodNameIndex;
    }


    /**
     * 实现方法的调用
     */
    private static void addMethod(ClassWriter writer, String newClassInnerName,
                                  MethodBean methodBean, int access, int methodNameIndex) throws Exception {
        MethodVisitor methodVisitor = writer.visitMethod(access, methodBean.methodName,
                methodBean.methodDesc, null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        // 区分静态或者是非静态方法调用
        if ((methodBean.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        }
        // 获取新建的方法字段
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, newClassInnerName,
                METHOD_FIELD_PREFIX + methodNameIndex, Type.getDescriptor(Method.class));
        Type[] argumentTypes = Type.getArgumentTypes(methodBean.methodDesc);
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
        Type returnType = Type.getReturnType(methodBean.methodDesc);
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
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, value);
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
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, value);
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
    private static int findSomeMethod(Method[] methods, MethodBean methodBean) {
        for (int i = 0; i < methods.length; i++) {
            if (equalsMethod(methods[i], methodBean)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 判断 {@link Method} 和 {@link MethodBean} 是否相等
     */
    private static boolean equalsMethod(Method method, MethodBean methodBean) {
        if (method == null && methodBean == null) {
            return true;
        }
        if (method == null || methodBean == null) {
            return false;
        }
        try {
            if (!method.getName().equals(methodBean.methodName)) {
                return false;
            }
            if (!Type.getReturnType(method).equals(Type.getReturnType(methodBean.methodDesc))) {
                return false;
            }
            Type[] argumentTypes1 = Type.getArgumentTypes(method);
            Type[] argumentTypes2 = Type.getArgumentTypes(methodBean.methodDesc);
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
