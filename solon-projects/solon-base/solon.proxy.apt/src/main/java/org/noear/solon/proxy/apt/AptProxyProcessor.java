package org.noear.solon.proxy.apt;

import com.google.auto.service.AutoService;
import org.noear.solon.aspect.annotation.Dao;
import org.noear.solon.aspect.annotation.Repository;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.annotation.ProxyComponent;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;


/**
 * Apt 代理注解处理器
 *
 * @author noear
 * @since 2.2
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"*"})
public class AptProxyProcessor extends AbstractAptProxyProcessor {

    @Override
    protected void initSupportedAnnotation() {
        addSupportedAnnotation(ProxyComponent.class);
        addSupportedAnnotation(Dao.class);
        addSupportedAnnotation(Service.class);
        addSupportedAnnotation(Repository.class);
    }
}
