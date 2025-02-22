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
import org.noear.solon.loader.JarLauncher;

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
            //获取 JarLauncher 类所在路径
            ProtectionDomain protectionDomain = JarLauncher.class.getProtectionDomain();
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
