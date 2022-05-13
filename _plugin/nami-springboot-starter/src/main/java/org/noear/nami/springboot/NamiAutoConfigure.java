package org.noear.nami.springboot;

import org.noear.nami.annotation.NamiClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Nami 自动配置
 *
 * @author desire
 */
@Configuration
@EnableConfigurationProperties(NamiClientProperties.class)
public class NamiAutoConfigure implements EnvironmentAware, ResourceLoaderAware, ImportBeanDefinitionRegistrar {
    private ResourceLoader resourceLoader;

    private Environment environment;

    private NamiClientProperties properties;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        readProperties(environment);
    }
    //读取配置文件
    private void readProperties(Environment environment) {
        this.properties = new NamiClientProperties();
        String packages = environment.getProperty("spring.nami.packages");
        if (packages != null) {
            this.properties.setPackages(new ArrayList<>(Arrays.asList(packages.split(","))));
        } else {
            this.properties.setPackages(new ArrayList<>(0));
        }
        Map<String, List<String>> services = new HashMap<>();
        this.properties.setServices(services);
        AbstractEnvironment abstractEnvironment = (AbstractEnvironment) environment;
        MutablePropertySources propertySources = abstractEnvironment.getPropertySources();
        propertySources.forEach(propertySource -> {
            if (propertySource instanceof MapPropertySource) {
                MapPropertySource mps = (MapPropertySource) propertySource;
                Set<String> keys = mps.getSource().keySet();
                for (String key : keys) {
                    if (key.startsWith("spring.nami.services.")) {
                        String v = String.valueOf(mps.getProperty(key));
                        String serviceName = key.replaceFirst("spring.nami.services\\.", "");
                        List<String> urls=new ArrayList<>(Arrays.asList(v.split(",")));
                        services.put(serviceName, urls);
                        //注册负载均衡
                        NamiUpstreamFactory.regUpstream(serviceName,urls);
                    }
                }
            }
        });
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

        //注册ConfigurationBean
        AbstractBeanDefinition configurationBeanDefition = BeanDefinitionBuilder
                .genericBeanDefinition(NamiConfigurationSpring.class)
                .setAutowireMode(GenericBeanDefinition.AUTOWIRE_NO)
                .getBeanDefinition();
        registry.registerBeanDefinition("NamiConfigurationSpring",configurationBeanDefition);


        //注册Clients
        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(NamiClient.class));
        Set<String> basePackages = getBasePackages(metadata);
        for (String basePackage : basePackages) {
            candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
        }
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                // 验证是否注解在接口上
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                Assert.isTrue(annotationMetadata.isInterface(),
                        "@NamiClient 只能声明在接口上");

                Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(NamiClient.class.getCanonicalName());
                String alias = getName(attributes);
                Assert.notNull(alias,"@NamiClient 必须设定name或url的值");

                BeanDefinitionBuilder definition = BeanDefinitionBuilder
                        .genericBeanDefinition(NamiClientFactory.class);
                definition.addPropertyValue("interfaceClass", beanDefinition.getBeanClassName());
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);


                AbstractBeanDefinition clientBeanDefinition = definition.getBeanDefinition();

                BeanDefinitionHolder holder = new BeanDefinitionHolder(clientBeanDefinition, annotationMetadata.getClassName(),
                        new String[]{alias});
                BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
            }
        }


    }

    private String getName(Map<String, Object> attributes) {
        String name = (String) attributes.get("name");
        if (!StringUtils.isEmpty(name)) {
            return "NamiClient(name="+name+")";
        }
        String url=(String) attributes.get("url");
        if (!StringUtils.isEmpty(url)) {
            return "NamiClient(url="+url+")";
        }
        return null;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    /**
     * 获取需要扫描的包
     *
     * @param importingClassMetadata
     * @return
     */
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {

        Set<String> basePackages = new HashSet<>();

        //加入配置中指定的包
        basePackages.addAll(properties.getPackages());

        //如果没有配置，默认为启动类所在包
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

}
