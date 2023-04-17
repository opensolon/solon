package org.noear.solon.web.xss.annotation;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

import java.util.regex.Pattern;

/**
 * Xss 校验器
 *
 * @author 多仔ヾ
 * @since 2.2
 */
public class XssValidator implements Validator<Xss> {

    /** 
     * 设置校验失败提示信息
     *
     * @author 多仔ヾ
     * @param anno Xss校验注解
     * @return 校验失败提示信息
     **/
    @Override
    public String message(Xss anno) {
        return anno.message();
    }

    /**
     * 校验强类型值
     *
     * @author 多仔ヾ
     * @param anno Xss校验注解
     * @param val 待校验数据
     * @param tmp StringBuilder
     * @return 校验结果实体
     **/
    @Override
    public Result validateOfValue(Xss anno, Object val, StringBuilder tmp) {
        return verify(anno, String.valueOf(val));
    }

    /** 
     * 校验上下文参数
     *
     * @author 多仔ヾ
     * @param ctx 上下文对象
     * @param anno Xss校验注解
     * @param name 参数名
     * @param tmp StringBuilder
     * @return 校验结果实体
     **/
    @Override
    public Result validateOfContext(Context ctx, Xss anno, String name, StringBuilder tmp) {
        String val = ctx.param(name);
        return verify(anno, val);
    }

    /**
     * 执行校验
     *
     * @author 多仔ヾ
     * @param anno Xss注解
     * @param val 待校验数据
     * @return 校验结果实体
     **/
    private Result verify(Xss anno, String val) {
        if (Utils.isBlank(val)) {
            // 校验通过
            return Result.succeed();
        }

        if (Pattern.compile("\"<(\\\\S*?)[^>]*>.*?|<.*? />\"").matcher(val).matches()) {
            // 校验拒绝
            return Result.failure();
        } else {
            // 校验通过
            return Result.succeed();
        }
    }
}
