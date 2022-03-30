package com.baomidou.mybatisplus.test.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.solon.service.IService;
import com.baomidou.mybatisplus.solon.service.impl.ServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.GenericUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author nieqiurong 2021/1/19.
 */
public class ServiceTest {

    static class Demo {

    }

    interface DemoMapper extends BaseMapper<Demo> {

    }

    static class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> {

    }

    static class DemoServiceExtend extends DemoServiceImpl {

    }

    @Test
    @SuppressWarnings("unchecked")
    void genericTest() {
        IService<Demo>[] services = new IService[]{new DemoServiceImpl(), new DemoServiceExtend()};
        for (IService<Demo> service : services) {
            Assertions.assertEquals(Demo.class, service.getEntityClass());
            Assertions.assertEquals(DemoMapper.class, ReflectionKit.getFieldValue(service, "mapperClass"));
        }
    }


    static class MyServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    }

    static class MyServiceExtend extends MyServiceImpl<DemoMapper, Demo> {

    }

    @Test
    void testSuperClassGenericType() {
        // 多重继承测试
        assertThat(getSuperClassGenericType(MyServiceExtend.class,
            ServiceImpl.class, 0).equals(DemoMapper.class));
        assertThat(getSuperClassGenericType(MyServiceExtend.class,
            ServiceImpl.class, 1).equals(Demo.class));
    }

    public static Class<?> getSuperClassGenericType(final Class<?> clazz, final Class<?> genericIfc, final int index) {
        Class<?>[] typeArguments = GenericUtil.resolveTypeArguments(ClassUtils.getUserClass(clazz), genericIfc);
        return null == typeArguments ? null : typeArguments[index];
    }
}
