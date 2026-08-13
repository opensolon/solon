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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import static org.noear.solon.maven.plugin.Constant.JAR_CLASS_PATH;

public class ClassesMove {

    /**
     * 检查包是否变化
     *
     * @return
     */
    public static boolean isNotChangeJar(File file) {
        try (JarFile targetJarfile = new JarFile(file)) {
            Enumeration<JarEntry> entries = targetJarfile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory() && jarEntry.getName().contains(Constant.HEAD_PACKAGE_PATH) && jarEntry.getName().endsWith(".class")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    public static void change(File file) throws Exception {
        String target = file.getParent() + File.separator;
        String name = file.getName();
        String desc = name.substring(0, name.lastIndexOf("."));
        File file1 = new File(target + desc);
        if (file1.isDirectory()) {
            deleteDirectory(file1.getAbsolutePath());
        }
        file1.mkdirs();
        // 修复：先在临时文件完成重打包并校验，成功后再删除原 jar 并替换，避免原构件被删后打包失败导致产物永久丢失
        File tempJar = new File(target + desc + ".tmp.jar");
        try {
            //解压
            unzipJar(file1.getPath(), file.getAbsolutePath());
            //打包到临时文件
            jar(tempJar.getAbsolutePath(), file1);

            //校验临时产物有效
            if (!tempJar.isFile() || tempJar.length() == 0) {
                throw new IOException("Repackaged jar is invalid: " + tempJar.getAbsolutePath());
            }

            //删除原来的jar
            deleteFile(file.getAbsolutePath());
            //临时文件替换为正式产物
            if (!tempJar.renameTo(file)) {
                throw new IOException("Rename temp jar failed: " + tempJar.getAbsolutePath());
            }
            //删除历史
            deleteDirectory(file1.getAbsolutePath());
            //打包新的文件
        } catch (Exception e) {
            // 修复：失败时清理残留临时文件，解压目录保留便于恢复，并将异常向上传播而非吞掉
            deleteFile(tempJar.getAbsolutePath());
            throw e;
        }
    }

    private static void jar(String jarFileName, File f) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(jarFileName);
        JarOutputStream out = new JarOutputStream(fileOutputStream);
        jar(out, f, JAR_CLASS_PATH);
        out.flush();
        out.close();
        fileOutputStream.flush();
        fileOutputStream.close();
    }


    private static void jar(JarOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new JarEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                jar(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            try (FileInputStream in = new FileInputStream(f);
                 BufferedInputStream bis = new BufferedInputStream(in)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = bis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        }
    }


    /**
     * 删除目录及目录下的文件
     *
     * @param dir：要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName：要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void unzipJar(String destinationDir, String jarPath) throws IOException {
        File outputDir = new File(destinationDir);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    File outputFile = new File(destinationDir, entry.getName());
                    if (!outputFile.getParentFile().exists()) {
                        outputFile.getParentFile().mkdirs();
                    }

                    try (InputStream inputStream = jarFile.getInputStream(entry);
                         FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        }
    }
}
