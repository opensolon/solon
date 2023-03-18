package org.noear.solon.swagger;


import org.noear.solon.swagger.util.KvMap;
import io.swagger.annotations.ApiModel;

/**
 * @author: lbq
 * 联系方式: 526509994@qq.com
 * 创建日期: 2020/9/9
 */
@ApiModel(description = "响应状态")
public class SwaggerHttpCode {

    public SwaggerHttpCode() {
        KvMap kv = KvMap.by(200, "请求成功")
                .set(400, "服务器不理解请求的语法")
                .set(403, "服务器拒绝请求")
                .set(404, "服务器找不到请求的网页")
                .set(405, "禁用请求中指定的方法")
                .set(500, "服务器遇到错误，无法完成请求");

        this.httpCodeKv = kv;
    }

    private KvMap httpCodeKv;

    public KvMap getHttpCodeKv() {
        return httpCodeKv;
    }

    public void setHttpCodeKv(KvMap httpCodeKv) {
        this.httpCodeKv = httpCodeKv;
    }
}
