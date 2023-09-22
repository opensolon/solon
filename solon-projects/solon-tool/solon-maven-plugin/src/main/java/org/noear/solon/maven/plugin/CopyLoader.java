package org.noear.solon.maven.plugin;

import java.io.*;
import java.net.URI;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * @author hxm
 */
public class CopyLoader {
    static String path;
    static String name;
    static String tempName;

    public static void start(File file) throws Exception {
        CopyLoader.name = file.getName();
        CopyLoader.tempName = UUID.randomUUID() + ".jar";
        CopyLoader.path = (file.getAbsolutePath().replace(name, ""));
        //提取jar里面的class
        if (setLoader()) {
            organizeFiles();
        }
    }


    private static void organizeFiles() throws IOException{
        FileUtils.delete(new File(path + name));
        FileUtils.moveFile(new File(path + tempName), new File(path + name));
    }

    private static boolean setLoader() throws IOException {
        try {
            //获取当前类所在路径
            ProtectionDomain protectionDomain = CopyLoader.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
            String mavenPluginJarPath = (location == null ? null : location.getSchemeSpecificPart());
            JarFile jarfile = new JarFile(mavenPluginJarPath);
            JarFile targetJarfile = new JarFile(path + name);
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(path + tempName));
            //写loader 文件
            Enumeration<JarEntry> entries = jarfile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory() && jarEntry.getName().contains(Constant.HEAD_PACKAGE_PATH) && jarEntry.getName().endsWith(".class")) {
                    jarOutputStream.putNextEntry(jarEntry);
                    byte[] bytes = IOUtils.toByteArray(jarfile.getInputStream(jarEntry));
                    jarOutputStream.write(bytes);
                }
            }
            //写原文件
            Enumeration<JarEntry> targetEntries = targetJarfile.entries();
            while (targetEntries.hasMoreElements()) {
                JarEntry entry = targetEntries.nextElement();
                InputStream entryInputStream = targetJarfile
                        .getInputStream(entry);
                jarOutputStream.putNextEntry(entry);
                jarOutputStream.write(IOUtils.toByteArray(entryInputStream));
            }
            jarOutputStream.flush();
            jarOutputStream.close();
            targetJarfile.close();
            jarfile.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
