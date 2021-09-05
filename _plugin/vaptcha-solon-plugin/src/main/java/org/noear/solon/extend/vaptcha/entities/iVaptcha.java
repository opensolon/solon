package org.noear.solon.extend.vaptcha.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * @author iYarnFog
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class iVaptcha {
    /**
     * @author iYarnFog
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateResult {
        // 验证结果，1为通过，0为失败
        private boolean success;
        // 验证得分(可信度)，区间[0, 100]
        private int score;
        // 验证失败时为错误信息,验证通过但是 score 有扣分时显示扣分项
        private String msg;
    }

    @NotBlank
    String token;
    @NotBlank
    String server;
    @NotBlank
    String realIp = Context.current().realIp();
}
