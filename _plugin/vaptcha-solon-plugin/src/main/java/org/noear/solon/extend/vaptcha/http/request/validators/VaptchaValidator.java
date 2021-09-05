package org.noear.solon.extend.vaptcha.http.request.validators;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.vaptcha.entities.iVaptcha;
import org.noear.solon.validation.Validator;
import org.noear.solon.validation.ValidatorManager;

/**
 * @author iYarnFog
 */
@Slf4j
public class VaptchaValidator implements Validator<Vaptcha> {

    private String realIp;
    private final OkHttpClient client = new OkHttpClient();
    private final Props props = Solon.cfg().getProp("vaptcha");

    @Override
    public String message(Vaptcha annotation) {
        return annotation.message();
    }

    /**
     * 校验实体的字段
     */
    @Override
    public Result<?> validateOfEntity(Class<?> clz, Vaptcha annotation, String name, Object value0, StringBuilder tmp) {
        if (value0 instanceof iVaptcha) {
            return verify(annotation, (iVaptcha) value0) ? Result.succeed() : Result.failure(name);
        } else {
            return Result.failure(name);
        }
    }

    /**
     * 校验上下文的参数
     */
    @Override
    public Result<?> validateOfContext(Context ctx, Vaptcha annotation, String name, StringBuilder tmp) {
        String value = ctx.param(name);
        return value == null || verify(annotation, ONode.deserialize(value, iVaptcha.class)) ? Result.succeed() : Result.failure(name);
    }

    private boolean verify(Vaptcha annotation, iVaptcha vaptcha) {
        Result<?> result0 = ValidatorManager.validateOfEntity(vaptcha);
        if (result0.getCode() == Result.FAILURE_CODE) {
            return false;
        }
        RequestBody body = new FormBody.Builder()
                .add("id", props.get("vid"))
                .add("secretkey", props.get("key"))
                .add("scene", "3")
                .add("token", vaptcha.getToken())
                .add("ip", this.getRealIp(vaptcha.getRealIp()))
                .build();
        Request request = new Request.Builder()
                .url(vaptcha.getServer())
                .post(body)
                .build();
        Call call = this.client.newCall(request);
        try {
            Response response = call.execute();
            if (response.body() == null) {
                return false;
            }
            iVaptcha.ValidateResult result = ONode.deserialize(response.body().string(), iVaptcha.ValidateResult.class);
            return result.isSuccess();
        } catch (Exception exception) {
            log.error("Something went wrong.", exception);
            return false;
        }
    }

    private String getRealIp(String realIp) {
        if (realIp.contains("127.0.0.1") || this.props.getBool("local", false)) {
            if (this.realIp != null) {
                return this.realIp;
            }
            Request request = new Request.Builder()
                    .url("https://ip.tool.lu/")
                    .get()
                    .build();
            Call call = this.client.newCall(request);
            try {
                Response response = call.execute();
                if (response.body() != null) {
                    String[] tmp = response.body().string().split("\\r?\\n");
                    this.realIp = tmp[0].split(" ")[1];
                    return this.realIp;
                }
            } catch (Exception exception) {
                log.error("Something went wrong.", exception);
            }
        } else {
            if (realIp.startsWith("/")) {
                realIp = realIp.substring(1);
            }
        }
        return realIp;
    }
}