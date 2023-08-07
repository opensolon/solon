package org.noear.solon.admin.client.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 应用程序数据
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Data
@Builder
public class Application {

    private final String name;

    private final String token;

    private String baseUrl;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final String metadata;

    /**
     * 是否展示敏感信息，如：环境变量
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final boolean showSecretInformation;

    /**
     * 环境信息
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final EnvironmentInformation environmentInformation;

}
