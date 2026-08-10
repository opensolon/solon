/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.multipart;

import tech.smartboot.feat.core.common.FeatUtils;
import tech.smartboot.feat.core.common.HeaderName;
import tech.smartboot.feat.core.common.HeaderValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class PartImpl implements Part {
    /**
     * 表单域名称
     */
    private String name;
    /**
     * 文件名称
     */
    private String fileName;


    private InputStream inputStream;
    private OutputStream diskOutputStream;

    /**
     * 表单域大小
     */
    private int formSize;

    /**
     * 磁盘文件
     */
    private File diskFile;
    private final List<HeaderValue> headers = new ArrayList<>(8);
    private int headerSize = 0;
    private String headerTemp;
    private String contentType;
    private final MultipartConfig multipartConfig;

    public PartImpl(MultipartConfig multipartConfig) {
        this.multipartConfig = multipartConfig;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (fileName == null && inputStream == null) {
            throw new IllegalStateException();
        }
        if (inputStream != null) {
            return inputStream;
        }
        return inputStream = Files.newInputStream(diskFile.toPath());
    }

    @Override
    public String getContentType() {
        if (contentType != null) {
            return contentType;
        }
        contentType = getHeader(HeaderName.CONTENT_TYPE);
        return contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSubmittedFileName() {
        return fileName;
    }

    @Override
    public long getSize() {
        if (diskFile != null) {
            return diskFile.length();
        } else {
            return formSize;
        }
    }

    @Override
    public void write(String fileName) throws IOException {
        if (this.diskFile == null) {
            return;
        }
        Path target = Paths.get(fileName);
        if (!target.isAbsolute()) {
            if (FeatUtils.isNotBlank(multipartConfig.getLocation())) {
                target = Paths.get(multipartConfig.getLocation(), fileName);
            } else {
                target = File.createTempFile("feat_" + this.hashCode(), fileName).toPath();
            }
        }
        Files.move(diskFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void delete() throws IOException {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception ignore) {

            }

            inputStream = null;
        }
        if (diskFile != null) {
            try {
                diskFile.delete();
            } catch (Exception ignore) {

            }

            diskFile = null;
        }
    }

    @Override
    public String getHeader(String name) {
        for (int i = 0; i < headerSize; i++) {
            HeaderValue headerValue = headers.get(i);
            if (headerValue.getName().equalsIgnoreCase(name)) {
                return headerValue.getValue();
            }
        }
        return null;
    }

    public void setHeadValue(String value) {
        setHeader(headerTemp, value);
    }

    public void setHeaderTemp(String headerTemp) {
        this.headerTemp = headerTemp;
    }

    public void setHeader(String headerName, String value) {
        if (headerSize < headers.size()) {
            HeaderValue headerValue = headers.get(headerSize);
            headerValue.setName(headerName);
            headerValue.setValue(value);
        } else {
            headers.add(new HeaderValue(headerName, value));
        }
        headerSize++;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        List<String> value = new ArrayList<>(4);
        for (int i = 0; i < headerSize; i++) {
            HeaderValue headerValue = headers.get(i);
            if (headerValue.getName().equalsIgnoreCase(name)) {
                value.add(headerValue.getValue());
            }
        }
        return value;
    }

    @Override
    public Collection<String> getHeaderNames() {
        Set<String> nameSet = new HashSet<>();
        for (int i = 0; i < headerSize; i++) {
            nameSet.add(headers.get(i).getName());
        }
        return nameSet;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setFormSize(int formSize) {
        this.formSize = formSize;
    }

    public OutputStream getDiskOutputStream() throws IOException {
        if (fileName == null) {
            throw new IllegalStateException();
        }
        if (diskOutputStream == null) {
            diskFile = getFile();
            //System.out.println("filePath: " + diskFile.getAbsolutePath());
            diskFile.deleteOnExit();
            diskOutputStream = Files.newOutputStream(diskFile.toPath());
        }
        return diskOutputStream;
    }

    private File getFile() throws IOException {
        if (FeatUtils.isNotBlank(multipartConfig.getLocation())) {
            File location = new File(multipartConfig.getLocation());
            if (location.isDirectory()) {
                return new File(location, this.hashCode() + "_" + fileName);
            }
        }
        return File.createTempFile("feat_" + this.hashCode() + "_", fileName);
    }
}
