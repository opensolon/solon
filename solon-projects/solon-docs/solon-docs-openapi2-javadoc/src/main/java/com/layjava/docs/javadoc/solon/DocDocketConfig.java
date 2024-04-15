package com.layjava.docs.javadoc.solon;

import cn.hutool.core.collection.CollUtil;
import com.layjava.docs.javadoc.solon.properties.DocketProperty;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiContact;
import org.noear.solon.docs.models.ApiInfo;

@Configuration
public class DocDocketConfig {

    private static final String PREFIX_NAME = "com.layjava.docs.javadoc.solon.plugin-";

    /**
     * 构建 DocDocket 实例，并配置相关信息
     */
    @Bean
    public void buildDocDocket(@Inject DocketProperty docketProperty) {

        // 检查参数
        if (docketProperty == null || CollUtil.isEmpty(docketProperty.getDocs())) {
            return;
        }

        docketProperty.getDocs().stream()
                // 获取所有启用
                .filter(docsProperty -> Boolean.TRUE.equals(docsProperty.isEnabled()))
                .forEach(docsProperty -> {
                    //构建 DocDocket 实例
                    DocDocket docDocket = new DocDocket();
                    docDocket.groupName(docsProperty.getGroupName());

                    //配置API信息
                    ApiInfo apiInfo = new ApiInfo().title(docsProperty.getTitle())
                            .description(docsProperty.getDescription())
                            .version(docsProperty.getVersion());

                    //配置联系人信息
                    if (docsProperty.getContact() != null) {
                        apiInfo.contact(new ApiContact()
                                .name(docsProperty.getContact().getName())
                                .url(docsProperty.getContact().getUrl())
                                .email(docsProperty.getContact().getEmail())
                        );
                    }

                    docDocket.info(apiInfo)
                            // 配置扫描的包路径
                            .apis(docsProperty.getPackageName())
                            .globalResult(docsProperty.getGlobalData())
                            .globalResponseInData(docsProperty.getGlobalResponseInData())
                            .schemes(docsProperty.getSchemes().toArray(new String[0]));

                    //包装Bean（指定名字）
                    BeanWrap beanWrap = Solon.context().wrap(DocDocket.class, docDocket);
                    Solon.context().putWrap(PREFIX_NAME + docsProperty.getGroupName(), beanWrap);
                });

    }


}
