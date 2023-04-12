package org.noear.solon.test.http;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.UploadedFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2023/4/10 created
 */
public class TestContext extends ContextEmpty {

    @Override
    public String ip() {
        return "127.0.0.1";
    }

    @Override
    public String protocol() {
        return uri().getScheme();
    }

    Map<String, List<UploadedFile>> filesMap = new HashMap<>();

    public void filesAdd(String key, UploadedFile file) {
        List<UploadedFile> files = filesMap.get(key);
        if (files == null) {
            files = new ArrayList<>();
            filesMap.put(key, files);
        }

        files.add(file);
    }

    @Override
    public List<UploadedFile> files(String key) throws Exception {
        return filesMap.get(key);
    }

    InputStream bodyAsStream;

    public void bodyAsStream(InputStream inputStream) {
        bodyAsStream = inputStream;
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        if (bodyAsStream == null) {
            bodyAsStream = new ByteArrayInputStream(new byte[]{});
        }

        return bodyAsStream;
    }

    ///

    @Override
    public void output(byte[] bytes) {
        try {
            outputStream().write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            Utils.transferTo(stream, outputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    OutputStream outputStream = new ByteArrayOutputStream();

    @Override
    public OutputStream outputStream() {
        return outputStream;
    }

    @Override
    public void headerAdd(String key, String val) {
        super.headerAdd(key, val);
    }

    @Override
    public void headerSet(String key, String val) {
        super.headerSet(key, val);
    }
}
