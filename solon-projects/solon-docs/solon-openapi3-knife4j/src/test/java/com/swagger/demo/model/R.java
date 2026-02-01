package com.swagger.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "返回结果")
public class R<T> {

    @Schema(description = "状态码", example = "200")
    private int code;

    @Schema(description = "返回信息", example = "操作成功")
    private String message;

    @Schema(description = "返回数据")
    private T data;

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setData(data);
        return r;
    }

}
