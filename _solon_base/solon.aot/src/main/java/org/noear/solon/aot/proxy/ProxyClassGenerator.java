package org.noear.solon.aot.proxy;

import com.squareup.javapoet.JavaFile;
import org.noear.solon.aot.Settings;

import java.io.File;
import java.io.IOException;

/**
 * aot 代理注解处理器基类
 *
 * @author noear
 * @since 2.2
 */
public class ProxyClassGenerator {
    /**
     * 类文件构建器
     */
    private ProxyClassFileBuilder classFileBuilder = new ProxyClassFileBuilder();

    /**
     * 生成代码
     *
     * @param typeElement
     */
    public void generateCode(Settings settings, Class<?> typeElement) {
        assertProxyType(typeElement);

        //构建 java 文件
        JavaFile javaFile = classFileBuilder.build(typeElement);
        //写入源文件
        try {
            javaFile.writeTo(getProxyFileDir(settings, typeElement));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getProxyFileDir(Settings settings, Class<?> typeElement) {
        try {
            String dir = typeElement.getPackage().getName().replace("\\.","/");
            String proxyFileDir = settings.getGeneratedSources() + "/" + dir;

            File file = new File(proxyFileDir);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            return new File(settings.getGeneratedSources() + "/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 断言（对不支持的情况异常提示）
     */
    private void assertProxyType(Class<?> typeElement) throws IllegalStateException {
        int modifiers = typeElement.getModifiers();

        //虚拟类不支持
        if (java.lang.reflect.Modifier.isAbstract(modifiers)) {
            throw new IllegalStateException("Abstract classes are not supported as proxy components");
        }

        //只读类不支持
        if (java.lang.reflect.Modifier.isFinal(modifiers)) {
            throw new IllegalStateException("Final classes are not supported as proxy components");
        }

        //非公有类不支持
        if (java.lang.reflect.Modifier.isPublic(modifiers) == false) {
            throw new IllegalStateException("Not public classes are not supported as proxy components");
        }

        //泛型类不支持
        if (typeElement.getTypeParameters() != null && typeElement.getTypeParameters().length > 0) {
            throw new IllegalStateException("Generic type classes are not supported as proxy components");
        }
    }
}