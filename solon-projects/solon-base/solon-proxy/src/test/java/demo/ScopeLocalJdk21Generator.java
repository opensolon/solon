package demo;

import org.objectweb.asm.*;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

public class ScopeLocalJdk21Generator {

    public static byte[] generateClass() throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String className = "org/noear/solon/core/util/ScopeLocalJdk21";
        String superName = "java/lang/Object";
        String interfaceName = "org/noear/solon/core/util/ScopeLocal";

        // 开始定义类
        cw.visit(Opcodes.V21,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
                className,
                "Lorg/noear/solon/core/util/ScopeLocal<TT;>;",
                superName,
                new String[]{interfaceName});

        // 添加源文件信息
        cw.visitSource("ScopeLocalJdk21.java", null);

        // 生成静态字段：log
        FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                "log",
                "Lorg/slf4j/Logger;",
                null,
                null);
        fv.visitEnd();

        // 生成实例字段：ref
        fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "ref",
                "Ljava/lang/ScopedValue;",
                "Ljava/lang/ScopedValue<TT;>;",
                null);
        fv.visitEnd();

        // 生成静态初始化块（类构造器）
        generateStaticInitializer(cw);

        // 生成构造方法
        generateConstructor(cw);

        // 生成 get() 方法
        generateGetMethod(cw);

        // 生成 with(Runnable) 方法
        generateWithRunnableMethod(cw);

        // 生成 with(CallableTx) 方法
        generateWithCallableMethod(cw);

        // 生成 set(T) 方法
        generateSetMethod(cw);

        // 生成 remove() 方法
        generateRemoveMethod(cw);

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static void generateStaticInitializer(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_STATIC,
                "<clinit>",
                "()V",
                null,
                null);

        mv.visitCode();

        // log = LoggerFactory.getLogger(ScopeLocalJdk21.class);
        mv.visitLdcInsn(Type.getObjectType("org/noear/solon/core/util/ScopeLocalJdk21"));
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                "org/slf4j/LoggerFactory",
                "getLogger",
                "(Ljava/lang/Class;)Lorg/slf4j/Logger;",
                false);
        mv.visitFieldInsn(Opcodes.PUTSTATIC,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "log",
                "Lorg/slf4j/Logger;");

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 0);
        mv.visitEnd();
    }

    private static void generateConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null);

        mv.visitCode();

        // 调用父类构造方法
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false);

        // this.ref = ScopedValue.newInstance();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                "java/lang/ScopedValue",
                "newInstance",
                "()Ljava/lang/ScopedValue;",
                false);
        mv.visitFieldInsn(Opcodes.PUTFIELD,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "ref",
                "Ljava/lang/ScopedValue;");

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private static void generateGetMethod(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "get",
                "()Ljava/lang/Object;",
                "()TT;",
                null);

        mv.visitCode();

        // return ref.get();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "ref",
                "Ljava/lang/ScopedValue;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/ScopedValue",
                "get",
                "()Ljava/lang/Object;",
                false);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private static void generateWithRunnableMethod(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "with",
                "(Ljava/lang/Object;Ljava/lang/Runnable;)V",
                "(TT;Ljava/lang/Runnable;)V",
                null);

        mv.visitCode();

        // ref.where(ref, value).run(runnable);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "ref",
                "Ljava/lang/ScopedValue;");

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "ref",
                "Ljava/lang/ScopedValue;");

        mv.visitVarInsn(Opcodes.ALOAD, 1); // value
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/ScopedValue",
                "where",
                "(Ljava/lang/ScopedValue;Ljava/lang/Object;)Ljava/lang/ScopedValue$Carrier;",
                false);

        mv.visitVarInsn(Opcodes.ALOAD, 2); // runnable
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/ScopedValue$Carrier",
                "run",
                "(Ljava/lang/Runnable;)V",
                false);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(4, 3);
        mv.visitEnd();
    }

    private static void generateWithCallableMethod(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "with",
                "(Ljava/lang/Object;Lorg/noear/solon/core/util/CallableTx;)Ljava/lang/Object;",
                "<R:Ljava/lang/Object;X:Ljava/lang/Throwable;>(TT;Lorg/noear/solon/core/util/CallableTx<+TR;TX;>;)TR;^TX;",
                null);

        mv.visitCode();

        // return ref.where(ref, value).call(callable::call);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "ref",
                "Ljava/lang/ScopedValue;");

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "ref",
                "Ljava/lang/ScopedValue;");

        mv.visitVarInsn(Opcodes.ALOAD, 1); // value

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/ScopedValue",
                "where",
                "(Ljava/lang/ScopedValue;Ljava/lang/Object;)Ljava/lang/ScopedValue$Carrier;",
                false);

        // 创建 lambda 表达式：callable::call
        mv.visitVarInsn(Opcodes.ALOAD, 2); // callable

        // 使用 invokedynamic 调用 lambda
        Handle handle = new Handle(Opcodes.H_INVOKEINTERFACE,
                "org/noear/solon/core/util/CallableTx",
                "call",
                "()Ljava/lang/Object;^Ljava/lang/Throwable;",
                true);

        mv.visitInvokeDynamicInsn("call",
                "()Ljava/util/concurrent/Callable;",
                new Handle(Opcodes.H_INVOKESTATIC,
                        "java/lang/invoke/LambdaMetafactory",
                        "metafactory",
                        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                        false),
                Type.getType("()Ljava/lang/Object;"),
                handle,
                Type.getType("()Ljava/lang/Object;"));

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/ScopedValue$Carrier",
                "call",
                "(Ljava/util/concurrent/Callable;)Ljava/lang/Object;",
                false);

        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(5, 3);
        mv.visitEnd();
    }

    private static void generateSetMethod(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "set",
                "(Ljava/lang/Object;)Lorg/noear/solon/core/util/ScopeLocal;",
                "(TT;)Lorg/noear/solon/core/util/ScopeLocal<TT;>;",
                null);

        mv.visitCode();

        // log.error("ScopeLocal.set is invalid, please use ScopeLocal.with");
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "log",
                "Lorg/slf4j/Logger;");
        mv.visitLdcInsn("ScopeLocal.set is invalid, please use ScopeLocal.with");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                "org/slf4j/Logger",
                "error",
                "(Ljava/lang/String;)V",
                true);

        // return null;
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private static void generateRemoveMethod(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "remove",
                "()V",
                null,
                null);

        mv.visitCode();

        // log.error("ScopeLocal.remove is invalid, please use ScopeLocal.with");
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                "org/noear/solon/core/util/ScopeLocalJdk21",
                "log",
                "Lorg/slf4j/Logger;");
        mv.visitLdcInsn("ScopeLocal.remove is invalid, please use ScopeLocal.with");
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                "org/slf4j/Logger",
                "error",
                "(Ljava/lang/String;)V",
                true);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    public static void main(String[] args) throws Exception {
        // 生成字节码
        byte[] bytecode = generateClass();

        // 保存为 .class 文件
        try (FileOutputStream fos = new FileOutputStream("ScopeLocalJdk21.class")) {
            fos.write(bytecode);
        }

        System.out.println("Class file generated successfully!");

        // 可选：加载并测试生成的类
        testGeneratedClass(bytecode);
    }

    private static void testGeneratedClass(byte[] bytecode) throws Exception {
        // 使用自定义类加载器加载生成的类
        ClassLoader classLoader = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if (name.equals("org.noear.solon.core.util.ScopeLocalJdk21")) {
                    return defineClass(name, bytecode, 0, bytecode.length);
                }
                return super.findClass(name);
            }
        };

        // 加载类
        Class<?> clazz = classLoader.loadClass("org.noear.solon.core.util.ScopeLocalJdk21");

        // 创建实例
        Object instance = clazz.newInstance();

        // 测试方法
        Method getMethod = clazz.getMethod("get");
        Method withRunnableMethod = clazz.getMethod("with", Object.class, Runnable.class);
        Method setMethod = clazz.getMethod("set", Object.class);
        Method removeMethod = clazz.getMethod("remove");

        System.out.println("Class loaded successfully!");
        System.out.println("Methods available:");
        System.out.println("  - " + getMethod);
        System.out.println("  - " + withRunnableMethod);
        System.out.println("  - " + setMethod);
        System.out.println("  - " + removeMethod);
    }
}