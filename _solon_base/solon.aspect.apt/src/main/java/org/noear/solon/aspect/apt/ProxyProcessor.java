package org.noear.solon.aspect.apt;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import org.noear.solon.aspect.annotation.Dao;
import org.noear.solon.aspect.annotation.Repository;
import org.noear.solon.aspect.annotation.Service;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 *
 * https://blog.csdn.net/Goals1989/article/details/125446609
 *
 * https://blog.csdn.net/web15085181368/article/details/124874063
 *
 * https://blog.csdn.net/dirksmaller/article/details/103930756
 *
 * @author noear 2023/2/23 created
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"*"})
public class ProxyProcessor extends AbstractProcessor {
    /**
     * java源文件操作相关类，主要用于生成java源文件
     */
    private Filer mFiler;
    /**
     * 注解类型工具类，主要用于后续生成java源文件使用
     * 类为TypeElement，变量为VariableElement，方法为ExecuteableElement
     */
    private Elements mElementsUtils;
    /**
     * 日志打印，类似于log,可用于输出错误信息
     */
    private Messager mMessager;

    /**
     * 初始化，主要用于初始化各个变量
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();
        mElementsUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    /**
     * 支持的注解类型
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {

        Set<String> typeSet = new LinkedHashSet<>();

        typeSet.add(Dao.class.getCanonicalName());
        typeSet.add(Repository.class.getCanonicalName());
        typeSet.add(Service.class.getCanonicalName());

        return typeSet;
    }

    /**
     * 支持的版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 1.搜集信息
     * 2.生成java源文件
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!annotations.isEmpty()) {
            //获取Bind注解类型的元素，这里是类类型TypeElement
            Set<? extends Element> bindElement = roundEnv.getElementsAnnotatedWith(Service.class);

            try {
                generateCode(bindElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    /**
     * @param elements
     */
    private void generateCode(Set<? extends Element> elements) throws IOException {
        for (Element element : elements) {
            if (element instanceof TypeElement) {
                //由于是在类上注解，那么获取TypeElement
                TypeElement typeElement = (TypeElement) element;

                if(typeElement.getModifiers().contains(Modifier.ABSTRACT)){
                    continue;
                }

                if(typeElement.getModifiers().contains(Modifier.PUBLIC) == false){
                    continue;
                }

                addClass(typeElement);
            }
        }
    }

    private void addClass(TypeElement typeElement) throws IOException{
        //获取全限定类名
        String className = typeElement.getQualifiedName().toString();

        mMessager.printMessage(Diagnostic.Kind.WARNING, "className:" + className);

        //获取包路径
        PackageElement packageElement = mElementsUtils.getPackageOf(typeElement);
        String packageName = packageElement.getQualifiedName().toString();

        mMessager.printMessage(Diagnostic.Kind.WARNING, "packageName:" + packageName);

        //获取用于生成的类名
        className = getClassName(typeElement, packageName);


        ClassName supperClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
        String proxyClassName = className + "$$SolonProxy";

        //生成的类
        TypeSpec.Builder proxyTypeBuilder = TypeSpec
                .classBuilder(proxyClassName)
                .superclass(supperClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        //添加构造函数
        addConstructor(proxyTypeBuilder, typeElement, proxyClassName);
        //添加代理函数
        addMethodAll(proxyTypeBuilder, typeElement);
        //添加静态代码块
        addStaticBlock(proxyTypeBuilder, typeElement, className);

        TypeSpec proxyType = proxyTypeBuilder.build();

        //创建javaFile文件对象
        JavaFile javaFile = JavaFile.builder(packageName, proxyType).build();
        //写入源文件
        javaFile.writeTo(mFiler);
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

        //$L表示字面量 $T表示类型 //addStatement("$T bundle = new $T()",bundle)

        methodBuilder.addStatement("this.handler = handler");

        proxyTypeBuilder.addMethod(methodBuilder.build());
    }

    private void addStaticBlock(TypeSpec.Builder proxyTypeBuilder, TypeElement typeElement, String className){
        int methodIndex = 0;

        StringBuilder codeBuilder  = new StringBuilder();

        codeBuilder.append("try {\n");

        for (Element e : typeElement.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                ExecutableElement methodElement = (ExecutableElement) e;
                //添加函数
                if (methodElement.getModifiers().contains(Modifier.STATIC) ||
                        methodElement.getModifiers().contains(Modifier.PRIVATE) ||
                        methodElement.getModifiers().contains(Modifier.FINAL)) {
                    //静态 或 只读 或 私有；不需要重写
                    continue;
                }
                String methodFieldName = "  method" + methodIndex;

                codeBuilder.append(methodFieldName)
                        .append("=")
                        .append(className)
                        .append(".class.getMethod(\"")
                        .append(methodElement.getSimpleName())
                        .append("\"");

                for(VariableElement pe : methodElement.getParameters()){
                    codeBuilder.append(",")
                            .append(pe.asType().toString())
                            .append(".class");
                }

                codeBuilder.append(");\n");

                ++methodIndex;
            }
        }

        codeBuilder.append("} catch (Throwable e) {\n" +
                "  throw new IllegalStateException(e);\n" +
                "}\n");


        CodeBlock codeBlock = CodeBlock.of(codeBuilder.toString());

        proxyTypeBuilder.addStaticBlock(codeBlock);
    }

    private void addMethodAll(TypeSpec.Builder proxyTypeBuilder, TypeElement typeElement){
        int methodIndex = 0;

        for (Element e : typeElement.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                //添加函数
                methodIndex = addMethod(proxyTypeBuilder, (ExecutableElement) e, methodIndex);
            }
        }
    }

    /**
     * 添加函数
     */
    private int addMethod(TypeSpec.Builder proxyTypeBuilder, ExecutableElement methodElement, int methodIndex) {
        if (methodElement.getModifiers().contains(Modifier.STATIC) ||
                methodElement.getModifiers().contains(Modifier.PRIVATE) ||
                methodElement.getModifiers().contains(Modifier.FINAL)) {
            //静态 或 只读 或 私有；不需要重写
            return methodIndex;
        }

        String methodFieldName = "method" + methodIndex;

        proxyTypeBuilder.addField(Method.class, methodFieldName, Modifier.PRIVATE, Modifier.STATIC);

        TypeName returnTypeName = TypeName.get(methodElement.getReturnType());

        StringBuilder methodCodeBuilder = new StringBuilder();

        boolean isPublic = methodElement.getModifiers().contains(Modifier.PUBLIC);

        //生成方法
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(methodElement.getSimpleName().toString())
                .addModifiers(isPublic ? Modifier.PUBLIC : Modifier.PROTECTED)
                .returns(returnTypeName)
                .addAnnotation(Override.class);

        //添加可抛类型
        for(TypeMirror tt : methodElement.getThrownTypes()){
            methodBuilder.addException(TypeName.get(tt));
        }


        //构建代码块和参数
        methodCodeBuilder.append("handler.invoke(this, ")
                .append(methodFieldName).append(", ")
                .append("new Object[]{");

        for (VariableElement pe : methodElement.getParameters()) {
            TypeName paramType = TypeName.get(pe.asType());
            String paramName = pe.getSimpleName().toString();

            methodBuilder.addParameter(paramType, paramName);
            methodCodeBuilder.append(paramName).append(",");
        }

        if (methodCodeBuilder.charAt(methodCodeBuilder.length() - 1) == ',') {
            methodCodeBuilder.setLength(methodCodeBuilder.length() - 1);
        }
        methodCodeBuilder.append("});");


        //$L表示字面量 $T表示类型 //addStatement("$T bundle = new $T()",bundle)


        //添加函数代码
        if (methodElement.getReturnType().getKind() == TypeKind.VOID) {
            methodCodeBuilder.insert(0, "try { \n  ");
            methodCodeBuilder.append("\n} catch (RuntimeException e) {\n" +
                    "  throw e;\n" +
                    "} catch (Throwable e) {\n" +
                    "  throw new RuntimeException(e);\n" +
                    "}\n");

            methodBuilder.addCode(methodCodeBuilder.toString());
        } else {
            methodCodeBuilder.insert(0, "try { \n  return ("+methodElement.getReturnType().toString()+")");
            methodCodeBuilder.append("\n} catch (RuntimeException e) {\n" +
                    "  throw e;\n" +
                    "} catch (Throwable e) {\n" +
                    "  throw new RuntimeException(e);\n" +
                    "}\n");

            methodBuilder.addCode(methodCodeBuilder.toString());
        }

        proxyTypeBuilder.addMethod(methodBuilder.build());

        return ++methodIndex;
    }

    /**
     * 根据type和package获取类名
     *
     * @param type        类型
     * @param packageName 包名
     */
    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}
