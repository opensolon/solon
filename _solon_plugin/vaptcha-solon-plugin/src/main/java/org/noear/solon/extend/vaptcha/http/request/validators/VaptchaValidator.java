package org.noear.solon.extend.vaptcha.http.request.validators;

import okhttp3.*;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.extend.vaptcha.entities.ValidateResult;
import org.noear.solon.extend.vaptcha.entities.iVaptcha;
import org.noear.solon.validation.Validator;
import org.noear.solon.validation.ValidatorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class VaptchaValidator implements Validator<Vaptcha> {
    static Logger log = LoggerFactory.getLogger(VaptchaValidator.class);

    private String realIp;
    private final OkHttpClient client = new OkHttpClient();
    private final Props props = Solon.cfg().getProp("vaptcha");

    @Override
    public String message(Vaptcha annotation) {
        return annotation.message();
    }

    @Override
    public Class<?>[] groups(Vaptcha anno) {
        return anno.groups();
    }


    /**
     * 校验实体的字段
     */
    @Override
    public Result validateOfValue(Vaptcha anno, Object val0, StringBuilder tmp) {
        if (val0 instanceof iVaptcha) {
            return verify(anno, (iVaptcha) val0) ? Result.succeed() : Result.failure();
        } else {
            return Result.failure();
        }
    }

    /**
     * 校验上下文的参数
     */
    @Override
    public Result validateOfContext(Context ctx, Vaptcha anno, String name, StringBuilder tmp) {
        String value = ctx.param(name);
        return value == null || verify(anno, ONode.deserialize(value, iVaptcha.class)) ? Result.succeed() : Result.failure(name);
    }

    private boolean verify(Vaptcha annotation, iVaptcha vaptcha) {
        Result<?> result0 = ValidatorManager.validateOfEntity(vaptcha, null);
        if (result0.getCode() == Result.FAILURE_CODE) {
            return false;
        }

        //处理终端真实IP
        String realIp = vaptcha.getRealIp();
        if(Utils.isEmpty(realIp)){
            realIp = Context.current().realIp();
        }

        RequestBody body = new FormBody.Builder()
                .add("id", props.get("vid"))
                .add("secretkey", props.get("key"))
                .add("scene", "3")
                .add("token", vaptcha.getToken())
                .add("ip", this.getRealIp(realIp))
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
            ValidateResult result = ONode.deserialize(response.body().string(), ValidateResult.class);
            return result.getSuccess();
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