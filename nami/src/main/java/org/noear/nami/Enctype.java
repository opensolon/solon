package org.noear.nami;

import org.noear.nami.channel.Constants;

/**
 * 编码类型
 * */
public enum  Enctype {
    application_json(Constants.ct_json),
    application_hessian(Constants.ct_hessian);

    public final String contentType;

    Enctype(String contentType) {
        this.contentType = contentType;
    }
}
