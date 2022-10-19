package org.noear.solon.view.enjoy;

import com.jfinal.template.source.ISource;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author noear 2022/10/8 created
 */
class ClassPathSource2 implements ISource {
    protected String finalFileName;
    protected String fileName;
    protected String encoding;
    protected boolean isInJar;
    protected long lastModified;
    protected ClassLoader classLoader;
    protected URL url;

    public ClassPathSource2(ClassLoader classLoader,String fileName) {
        this(classLoader, null, fileName, "UTF-8");
    }

    public ClassPathSource2(ClassLoader classLoader,String baseTemplatePath, String fileName) {
        this(classLoader, baseTemplatePath, fileName, "UTF-8");
    }

    public ClassPathSource2(ClassLoader classLoader,String baseTemplatePath, String fileName, String encoding) {
        this.finalFileName = this.buildFinalFileName(baseTemplatePath, fileName);
        this.fileName = fileName;
        this.encoding = encoding;
        this.classLoader = classLoader;
        this.url = this.classLoader.getResource(this.finalFileName);
        if (this.url == null) {
            throw new IllegalArgumentException("File not found in CLASSPATH or JAR : \"" + this.finalFileName + "\"");
        } else {
            this.processIsInJarAndlastModified();
        }
    }

    protected void processIsInJarAndlastModified() {
        if ("file".equalsIgnoreCase(this.url.getProtocol())) {
            this.isInJar = false;
            this.lastModified = (new File(this.url.getFile())).lastModified();
        } else {
            this.isInJar = true;
            this.lastModified = -1L;
        }

    }

    protected String buildFinalFileName(String baseTemplatePath, String fileName) {
        String finalFileName;
        if (baseTemplatePath != null) {
            char firstChar = fileName.charAt(0);
            if (firstChar != '/' && firstChar != '\\') {
                finalFileName = baseTemplatePath + "/" + fileName;
            } else {
                finalFileName = baseTemplatePath + fileName;
            }
        } else {
            finalFileName = fileName;
        }

        if (finalFileName.charAt(0) == '/') {
            finalFileName = finalFileName.substring(1);
        }

        return finalFileName;
    }

    public String getCacheKey() {
        return this.fileName;
    }

    public String getEncoding() {
        return this.encoding;
    }

    protected long getLastModified() {
        return (new File(this.url.getFile())).lastModified();
    }

    public boolean isModified() {
        return this.isInJar ? false : this.lastModified != this.getLastModified();
    }

    public StringBuilder getContent() {
        if (!this.isInJar) {
            this.lastModified = this.getLastModified();
        }

        InputStream inputStream = this.classLoader.getResourceAsStream(this.finalFileName);
        if (inputStream == null) {
            throw new RuntimeException("File not found : \"" + this.finalFileName + "\"");
        } else {
            return loadFile(inputStream, this.encoding);
        }
    }

    public static StringBuilder loadFile(InputStream inputStream, String encoding) {
        try {
            InputStreamReader isr = new InputStreamReader(inputStream, encoding);
            Throwable var3 = null;

            try {
                StringBuilder ret = new StringBuilder();
                char[] buf = new char[1024];

                int num;
                while((num = isr.read(buf, 0, buf.length)) != -1) {
                    ret.append(buf, 0, num);
                }

                StringBuilder var19 = ret;
                return var19;
            } catch (Throwable var16) {
                var3 = var16;
                throw var16;
            } finally {
                if (isr != null) {
                    if (var3 != null) {
                        try {
                            isr.close();
                        } catch (Throwable var15) {
                            var3.addSuppressed(var15);
                        }
                    } else {
                        isr.close();
                    }
                }

            }
        } catch (Exception var18) {
            throw new RuntimeException(var18);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("In Jar File: ").append(this.isInJar).append("\n");
        sb.append("File name: ").append(this.fileName).append("\n");
        sb.append("Final file name: ").append(this.finalFileName).append("\n");
        sb.append("Last modified: ").append(this.lastModified).append("\n");
        return sb.toString();
    }
}

