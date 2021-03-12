package org.noear.nami.integration.springboot;

import org.noear.nami.Nami;
import org.noear.nami.NamiException;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.common.InfoUtils;
import org.noear.solon.Utils;
import org.noear.solon.extend.springboot.EnableSolon;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 1.2
 */
@EnableSolon
@Configuration
public class AutoConfigurationNami extends InstantiationAwareBeanPostProcessorAdapter {
    private Map<NamiClient, Object> cached = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        //兼容1.0.x
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.startsWith("org.springframework") == false) {
            try {
                Class<?> beanClz = bean.getClass();

                ReflectionUtils.doWithFields(beanClz, (field -> {
                    if (Modifier.isFinal(field.getModifiers())
                            || Modifier.isStatic(field.getModifiers())) {
                        return;
                    }

                    NamiClient client = field.getAnnotation(NamiClient.class);

                    if (client != null) {
                        if (field.getType().isInterface()) {
                            field.setAccessible(true);
                            field.set(bean, postAnno(client, field));
                        }
                    }
                }));
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return bean;
    }

    private Object postAnno(NamiClient anno, Field field) {
        if (Utils.isEmpty(anno.url()) && Utils.isEmpty(anno.name())) {
            NamiClient anno2 = field.getType().getAnnotation(NamiClient.class);
            if (anno2 != null) {
                anno = anno2;
            }
        }

        if (Utils.isEmpty(anno.url()) && Utils.isEmpty(anno.name()) && anno.upstream().length == 0) {
            throw new NamiException("@NamiClient configuration error!");
        } else {
            InfoUtils.print(field.getType(), anno);
        }

        Object obj = cached.get(anno);
        if (obj == null) {
            synchronized (anno) {
                obj = cached.get(anno);
                if (obj == null) {
                    obj = Nami.builder().create(field.getType(), anno);
                    cached.putIfAbsent(anno, obj);
                }
            }
        }

        return obj;
    }
}
