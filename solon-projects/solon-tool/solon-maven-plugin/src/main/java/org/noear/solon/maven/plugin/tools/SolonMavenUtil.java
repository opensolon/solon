package org.noear.solon.maven.plugin.tools;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.noear.solon.maven.plugin.Constant;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author songyinyin
 * @since 2023/4/9 20:10
 */
public class SolonMavenUtil {

    private static final String DOT_CLASS = ".class";

    private static final FileFilter CLASS_FILE_FILTER = file -> file.isFile() && file.getName().endsWith(DOT_CLASS);

    private static final FileFilter PACKAGE_DIRECTORY_FILTER = file -> file.isDirectory() && !file.getName().startsWith(".");

    /**
     * 获取指定目录或jar包的solon启动类
     *
     * @param file      可以是一个class目录，也可以是一个jar包
     * @param mainClass 如果指定启动类，则直接返回
     */
    public static String getStartClass(File file, String mainClass, Log logger) throws IOException {
        if (StringUtils.isNotEmpty(mainClass)) {
            return mainClass;
        }

        List<String> mains = new ArrayList<>();
        ClassPool classPool = ClassPool.getDefault();
        if (file.isDirectory()) {

            Deque<File> stack = new ArrayDeque<>();
            stack.push(file);
            while (!stack.isEmpty()) {
                File itemFile = stack.pop();
                if (itemFile.isFile()) {
                    try {
                        InputStream inputStream = Files.newInputStream(itemFile.toPath());
                        CtClass ctClass = classPool.makeClass(inputStream);
                        inputStream.close();
                        String solonMain = getSolonMainClass(ctClass, mains);
                        //有注解的为主类
                        if (solonMain != null) {
                            logger.info("检查到的启动类：" + ctClass.getName());
                            return solonMain;
                        }
                    } catch (IOException | NotFoundException ignored) {

                    }
                }
                if (itemFile.isDirectory()) {
                    pushAllSorted(stack, itemFile.listFiles(PACKAGE_DIRECTORY_FILTER));
                    pushAllSorted(stack, itemFile.listFiles(CLASS_FILE_FILTER));
                }
            }
        } else {

            try (JarFile jar = new JarFile(file)) {

                Enumeration<JarEntry> enumFiles = jar.entries();

                while (enumFiles.hasMoreElements()) {
                    JarEntry entry = enumFiles.nextElement();
                    String classFullName = entry.getName();

                    if (classFullName.endsWith(".class")) {
                        try {
                            InputStream inputStream = jar.getInputStream(entry);
                            CtClass ctClass = classPool.makeClass(inputStream);
                            inputStream.close();
                            String solonMain = getSolonMainClass(ctClass, mains);
                            //有注解的为主类
                            if (solonMain != null) {
                                logger.info("检查到的启动类：" + ctClass.getName());
                                return solonMain;
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }
        }


        if (mains.size() > 0) {
            if (mains.size() == 1) {
                logger.info("检查到的启动类：" + mains.get(0));
                return mains.get(0);
            }

            //提供选择启动类：
            logger.info("检查到的启动类：");

            for (int i = 0; i < mains.size(); i++) {
                logger.info(i + "、" + mains.get(i));
            }

            logger.info("请选择一个主函数作为启动函数(如：输入0回车)：");
            String tempMain;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                if (scanner.hasNextLine()) {
                    try {
                        String s = scanner.nextLine();
                        int i = Integer.parseInt(s);
                        tempMain = mains.get(i);
                        break;
                    } catch (Exception e) {
                        logger.error("选择失败，请重新选择");
                    }
                }
            }
            scanner.close();
            return tempMain;
        }
        throw new IllegalStateException("找不到启动类,请配置你的启动类");
    }

    /**
     * 优先使用maven上下文中的jdk
     */
    public static String getJavaExecutable(ToolchainManager toolchainManager, MavenSession mavenSession) {
        Toolchain toolchain = toolchainManager.getToolchainFromBuildContext("jdk", mavenSession);
        String javaExecutable = (toolchain != null) ? toolchain.findTool("java") : null;
        return (javaExecutable != null) ? javaExecutable : findJavaExecutable();
    }

    private static String findJavaExecutable() {
        String javaHome = System.getProperty("java.home");
        if (StringUtils.isEmpty(javaHome)) {
            throw new IllegalStateException("Unable to find java executable due to missing 'java.home'");
        }
        File bin = new File(new File(javaHome), "bin");
        File command = new File(bin, "java.exe");
        command = command.exists() ? command : new File(bin, "java");
        if (!command.exists()) {
            throw new IllegalStateException("Unable to find java in " + javaHome);
        }
        try {
            return command.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getSolonMainClass(CtClass ctClass, List<String> mains) throws NotFoundException {
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for (CtMethod method : methods) {
            if (isMainMethod(method)) {
                mains.add(ctClass.getName());

                //有注解的为主类
                if (ctClass.hasAnnotation(Constant.START_CLASS_ANNOTATION)) {
                    return ctClass.getName();
                }
            }
        }
        return null;
    }

    private static void pushAllSorted(Deque<File> stack, File[] files) {
        Arrays.sort(files, Comparator.comparing(File::getName));
        for (File file : files) {
            stack.push(file);
        }
    }

    private static boolean isMainMethod(CtMethod method) throws NotFoundException {
        if (method.getName().equals("main") && method.getParameterTypes().length == 1) {
            if (method.getParameterTypes()[0].getName().equals("java.lang.String[]") && Modifier.isStatic(method.getModifiers())) {
                if (method.getReturnType().getName().equals("void")) {
                    return true;
                }
            }
        }
        return false;
    }

}
