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
        setLoader();
        organizeFiles();
    }


    private static void organizeFiles() {
        new File(path + name).delete();
        new File(path + tempName).renameTo(new File(path + name));

    }

    private static void setLoader() throws IOException {
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
                    byte[] bytes = toByteArray(jarfile.getInputStream(jarEntry));
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
                jarOutputStream.write(toByteArray(entryInputStream));
            }
            jarOutputStream.flush();
            jarOutputStream.close();
            targetJarfile.close();
            jarfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
