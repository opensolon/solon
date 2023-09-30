package com.dtflys.forest.http.body;


import org.noear.solon.core.handle.UploadedFile;

public class SolonUploadRequestBodyBuilder extends RequestBodyBuilder<UploadedFile, SolonUploadRequestBody, SolonUploadRequestBodyBuilder> {

    @Override
    public SolonUploadRequestBody build(UploadedFile data, String defaultValue) {
        if (data == null) {
            return null;
        }
        SolonUploadRequestBody body = new SolonUploadRequestBody(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
