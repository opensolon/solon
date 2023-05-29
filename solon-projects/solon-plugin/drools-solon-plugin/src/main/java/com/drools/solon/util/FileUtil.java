package com.drools.solon.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:hongwen0928@outlook.com">Karas</a>
 * @date 2019/9/27
 * @since 1.0.0
 */
public class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 临时存放规则文件的文件夹
     */
    private static final String TEMP_DIR;

    static {
        boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
        if (isLinux) {
            TEMP_DIR = "/tmp";
        } else {
            Map<String, String> map = System.getenv();
            String win = map.get("TEMP");
            if (win != null && win.length() > 0) {
                TEMP_DIR = map.get("TEMP");
            } else {
                TEMP_DIR = "temp";
            }
        }
    }

    /**
     * 整合所有文件，到所传的List中
     *
     * @param path     可能含有classpath形式，因此需要进行判断
     * @param fileList {@code List<File>} 所传入的FileList
     */
    public static void fileList(String path, List<File> fileList) {
        if (path.startsWith(Utils.TAG_classpath)) {
            try {
                createTempDir();
                Enumeration<URL> resources = ResourceUtil.getResources(path);
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    fileList.add(copyResourceToTempFile(resource));
                }
            } catch (IOException e) {
                LOG.error("资源读取异常！", e);
            }
        } else {
            File file = new File(path);
            try {
                if(!file.exists()){
                    return;
                }
                if(!file.getAbsolutePath().equals(file.getCanonicalPath())){
                    LOG.info("资源文件,path:{},absolutePath{},[可能存在文件被遍历的风险]！", path,file.getAbsolutePath());
                    return;
                }
                if (!file.isDirectory()) {
                    // base case
                    fileList.add(file);
                } else {
                    File[] f = file.listFiles();
                    if (f != null) {
                        for (File nextFile : f) {
                            fileList(nextFile.getPath(), fileList);
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("资源读取异常[可能存在文件被遍历的风险]！", e);
            }

        }
    }

    /**
     * 将资源文件转为临时文件
     *
     * @param resource 资源目录下的文件，需要copy出来，因为打包后可能导致资源路径不正确。
     * @return {@code File} 资源目录对应的文件，但不是实际文件，是拷贝的。
     */
    private static File copyResourceToTempFile(URL resource) {
        OutputStream os = null;
        File targetFile = null;
        int EOF = -1;

        try (InputStream is = resource.openStream();){
            int length = is.available();
            byte[] buffer = new byte[length];

            int remaining = length;
            while (remaining > 0) {
                int location = length - remaining;
                int count = is.read(buffer, location, remaining);
                if (EOF == count) { // EOF
                    break;
                }
                remaining -= count;
            }
            targetFile = new File(TEMP_DIR + File.separator + Paths.get(resource.toURI()).getFileName());
            os = new FileOutputStream(targetFile);
            os.write(buffer);
        } catch (Exception e) {
            LOG.error("资源整合失败", e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                LOG.error("无法关闭文件", e);
            }
        }
        return targetFile;
    }

    /**
     * 创建临时目录文件夹
     */
    private static void createTempDir() {
        File file = new File(TEMP_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }


}
