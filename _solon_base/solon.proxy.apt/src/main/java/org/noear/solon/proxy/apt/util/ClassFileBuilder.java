package org.noear.solon.proxy.apt.util;

import com.squareup.javapoet.*;
import org.noear.solon.proxy.apt.AptProxy;
import org.noear.solon.proxy.apt.holder.ParamElementHolder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 类文件构建器
 *
 * @author noear
 * @since 2.2
 */
public class ClassFileBuilder {
    public JavaFile build(ProcessingEnvironment env, TypeElement typeElement) {
        //::1.准备
        //获取包路径
        PackageElement packageElement = env.getElementUtils().getPackageOf(typeElement);
        String packageName = packageElement.getQualifiedName().toString();

        //获取类名（支持成员类）
        String className = getClassName(typeElement, packageName);

        ClassName supperClassName = ClassName.get(packageName, className);
        String proxyClassName = className + AptProxy.PROXY_CLASSNAME_SUFFIX;

        //获取所有函数
        Map<String, ExecutableElement> methodAll = MethodUtil.findMethodAll(typeElement);

        //::2.开始
        //生成的类
        TypeSpec.Builder proxyTypeBuilder = TypeSpec
                .classBuilder(proxyClassName)
                .superclass(supperClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);


        //添加构造函数
        addConstructor(proxyTypeBuilder, typeElement, proxyClassName);

        if(methodAll.size() > 0) {
            //添加代理函数
            addMethodAll(proxyTypeBuilder, methodAll);
            //添加静态代码块
            addStaticBlock(proxyTypeBuilder, packageName, className, methodAll);
        }


        //创建javaFile文件对象
        TypeSpec proxyType = proxyTypeBuilder.build();
        return JavaFile.builder(packageName, proxyType).build();
    }

    /**
     * 添加构造函数
     */
    private void addConstructor(TypeSpec.Builder proxyTypeBuilder, TypeElement typeElement, String proxyClassName) {
        //添加字段
        proxyTypeBuilder.addField(InvocationHandler.class, "handler", Modifier.PRIVATE);

        //添加构造函数
        MethodSpec.Builder methodBuilder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC);


        methodBuilder.addParameter(InvocationHandler.class, "handler");
        methodBuilder.addStatement("this.handler = handler");

        proxyTypeBuilder.addMethod(methodBuilder.build());
    }


    /**
     * 添加静态代码块
     */
    private void addStaticBlock(TypeSpec.Builder proxyTypeBuilder, String packageName, String className, Map<String, ExecutableElement> methodAll) {
        int methodIndex = 0;

        StringBuilder codeBuilder = new StringBuilder(150);

        codeBuilder.append("try {\n");
        codeBuilder.append("  Class<?> clazz = $T.class;\n\n");

        for (ExecutableElement methodElement : methodAll.values()) {
            //添加函数
            if (MethodUtil.allowMethod(methodElement) == false) {
                //静态 或 只读 或 私有；不需要重写
                continue;
            }
            String methodFieldName = "  method" + methodIndex;

            codeBuilder.append(methodFieldName)
                    .append("=clazz.getMethod(\"")
                    .append(methodElement.getSimpleName())
                    .append("\"");

            for (VariableElement p0 : methodElement.getParameters()) {
                if (p0 instanceof ParamElementHolder) {
                    ParamElementHolder p0x = (ParamElementHolder) p0;

                    TypeMirror p1 = p0x.getReal().asType();
                    String p1Name = p1.toString();
                    int p1NameIdx = p1Name.indexOf("<");
                    if (p1NameIdx > 0) {
                        p1Name = p1Name.substring(0, p1NameIdx);
                    }

                    if (p1 instanceof TypeVariable) {
                        codeBuilder.append(",Object.class");
                    } else {
                        codeBuilder.append(",")
                                .append(p1Name)
                                .append(".class");
                    }
                } else {
                    TypeMirror p1 = p0.asType();
                    String p1Name = p1.toString();
                    int p1NameIdx = p1Name.indexOf("<");
                    if (p1NameIdx > 0) {
                        p1Name = p1Name.substring(0, p1NameIdx);
                    }

                    codeBuilder.append(",")
                            .append(p1Name)
                            .append(".class");
                }
            }

            codeBuilder.append(");\n");

            ++methodIndex;
        }

        codeBuilder.append("} catch (Throwable e) {\n" +
                "  throw new IllegalStateException(e);\n" +
                "}\n");


        CodeBlock codeBlock = CodeBlock.of(codeBuilder.toString(), ClassName.get(packageName, className));

        proxyTypeBuilder.addStaticBlock(codeBlock);
    }


    /**
     * 添加所有函数
     */
    private void addMethodAll(TypeSpec.Builder proxyTypeBuilder, Map<String, ExecutableElement> methodAll) {
        int methodIndex = 0;

        for (ExecutableElement e : methodAll.values()) {
            //添加函数
            methodIndex = addMethod(proxyTypeBuilder, e, methodIndex);
        }
    }

    /**
     * 添加具本函数
     */
    private int addMethod(TypeSpec.Builder proxyTypeBuilder, ExecutableElement methodElement, int methodIndex) {
        if (MethodUtil.allowMethod(methodElement) == false) {
            //静态 或 只读 或 私有；不需要重写
            return methodIndex;
        }

        String methodFieldName = "method" + methodIndex;

        proxyTypeBuilder.addField(Method.class, methodFieldName, Modifier.PRIVATE, Modifier.STATIC);

        TypeMirror returnType = methodElement.getReturnType();
        TypeName returnTypeName = TypeNameUtil.getTypeName(returnType);

        StringBuilder codeBuilder = new StringBuilder(150);

        boolean isPublic = methodElement.getModifiers().contains(Modifier.PUBLIC);

        //生成方法
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(methodElement.getSimpleName().toString())
                .addModifiers(isPublic ? Modifier.PUBLIC : Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(returnTypeName);

        //添加可抛类型
        for (TypeMirror tt : methodElement.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(tt));
        }

        //添加函数泛型
        for (TypeParameterElement te : methodElement.getTypeParameters()) {
            if (te.asType() instanceof TypeVariable) {
                TypeVariable tv = (TypeVariable) te.asType();
                methodBuilder.addTypeVariable(TypeVariableName.get(tv));
            } else {
                methodBuilder.addTypeVariable(TypeVariableName.get(te.getSimpleName().toString()));
            }

        }

        //构建代码块和参数
        codeBuilder.append("handler.invoke(this, ")
                .append(methodFieldName).append(", ")
                .append("new Object[]{");

        //添加函数参数
        for (VariableElement pe : methodElement.getParameters()) {
            TypeMirror pet = pe.asType();
            TypeName paramType = TypeNameUtil.getTypeName(pet);

            String paramName = pe.getSimpleName().toString();

            methodBuilder.addParameter(paramType, paramName);
            codeBuilder.append(paramName).append(",");
        }

        if (codeBuilder.charAt(codeBuilder.length() - 1) == ',') {
            codeBuilder.setLength(codeBuilder.length() - 1);
        }
        codeBuilder.append("});");

        //添加函数代码
        if (methodElement.getReturnType().getKind() == TypeKind.VOID) {
            codeBuilder.insert(0, "try { \n  ");
            codeBuilder.append("\n} catch (RuntimeException _ex) {\n" +
                    "  throw _ex;\n" +
                    "} catch (Throwable _ex) {\n" +
                    "  throw new RuntimeException(_ex);\n" +
                    "}\n");

            methodBuilder.addCode(codeBuilder.toString());
        } else {
            codeBuilder.insert(0, "try { \n  return (" + returnType + ")");
            codeBuilder.append("\n} catch (RuntimeException _ex) {\n" +
                    "  throw _ex;\n" +
                    "} catch (Throwable _ex) {\n" +
                    "  throw new RuntimeException(_ex);\n" +
                    "}\n");

            methodBuilder.addCode(codeBuilder.toString());
        }

        proxyTypeBuilder.addMethod(methodBuilder.build());

        return ++methodIndex;
    }

    /**
     * 根据类型和包名获取类名
     *
     * @param type        类型
     * @param packageName 包名
     */
    public static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}