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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
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

            //由于是在类上注解，那么获取TypeElement
            TypeElement typeElement = (TypeElement) element;

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
                    .addModifiers(Modifier.PUBLIC);

            addConstructor(proxyTypeBuilder,typeElement, proxyClassName);

            for (Element e : typeElement.getEnclosedElements()) {
                if (e.getKind() == ElementKind.METHOD) {
                    //Symbol.MethodSymbol
                    addMethod(proxyTypeBuilder, (ExecutableElement) e);
                }
            }

            TypeSpec proxyType = proxyTypeBuilder.build();

            //创建javaFile文件对象
            JavaFile javaFile = JavaFile.builder(packageName, proxyType).build();
            //写入源文件
            javaFile.writeTo(mFiler);

        }
    }

    private void addConstructor(TypeSpec.Builder proxyTypeBuilder, TypeElement typeElement, String proxyClassName) {
        //添加字段
        proxyTypeBuilder.addField(InvocationHandler.class, "handler", Modifier.PRIVATE);

        StringBuilder methodCodeBuilder = new StringBuilder();

        //添加构造函数
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(proxyClassName)
                .addModifiers(Modifier.PUBLIC);


        methodCodeBuilder.append("this.target = target");

        {
            TypeName paramType = TypeName.get(typeElement.asType());
            String paramName = "target";

            methodBuilder.addParameter(paramType, paramName);
        }

        //$L表示字面量 $T表示类型 //addStatement("$T bundle = new $T()",bundle)

        methodBuilder.addStatement(methodCodeBuilder.toString());

        proxyTypeBuilder.addMethod(methodBuilder.build());
    }

    private void addMethod(TypeSpec.Builder proxyTypeBuilder, ExecutableElement methodElement) {
        TypeName returnTypeName = TypeName.get(methodElement.getReturnType());

        StringBuilder methodCodeBuilder = new StringBuilder();


        //生成方法
        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder(methodElement.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(returnTypeName)
                .addAnnotation(Override.class);


        methodCodeBuilder.append("target.")
                .append(methodElement.getSimpleName())
                .append("(");

        for (VariableElement paramElement : methodElement.getParameters()) {
            TypeName paramType = TypeName.get(paramElement.asType());
            String paramName = paramElement.getSimpleName().toString();

            methodBuilder.addParameter(paramType, paramName);
            methodCodeBuilder.append(paramName).append(",");
        }
        if(methodCodeBuilder.charAt(methodCodeBuilder.length()-1) == ','){
            methodCodeBuilder.setLength(methodCodeBuilder.length()-1);
        }
        methodCodeBuilder.append(")");


        //$L表示字面量 $T表示类型 //addStatement("$T bundle = new $T()",bundle)


        if(methodElement.getReturnType().getKind() == TypeKind.VOID){
            methodBuilder.addStatement(methodCodeBuilder.toString());
        }else {
            methodCodeBuilder.insert(0,"return ");
            methodBuilder.addStatement(methodCodeBuilder.toString());
        }

        proxyTypeBuilder.addMethod(methodBuilder.build());
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
