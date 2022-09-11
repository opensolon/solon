package test3;

import org.junit.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Around;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.aspect.InterceptorEntity;
import org.noear.solon.validation.annotation.Valid;

import java.lang.annotation.Annotation;

/**
 * @author noear 2022/8/22 created
 */
@Valid
public class AnnoTest {
    @Test
    public void test1() {
        Annotation anno = AnnoTest.class.getAnnotation(Valid.class);
        assert anno != null;
        int count = 10000;

        long time_start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            xxx1(anno);
        }
        long time_span = System.currentTimeMillis() - time_start;
        System.out.println("xxx1: " + time_span);


        time_start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            xxx2(Solon.context(), anno);
        }
        time_span = System.currentTimeMillis() - time_start;
        System.out.println("xxx2: " + time_span); //性能 10 倍之差
    }

    /**
     * 方案1
     * */
    private void xxx1(Annotation anno){
        doAroundAdd(anno.annotationType().getAnnotation(Around.class));
    }

    /**
     * 方案2
     * */
    private void xxx2(AopContext context, Annotation anno){
        //@since 1.10 //支持拦截注解的别名注解形式
        for (Annotation anno2 : anno.annotationType().getAnnotations()) {
            if (anno2 instanceof Around) {
                doAroundAdd((Around) anno2);
            } else {
                InterceptorEntity ie2 = context.beanAroundGet(anno2.annotationType());
                if (ie2 != null) {
                    doAroundAdd(ie2);
                }
            }
        }
    }

    private void doAroundAdd(Around anno){

    }

    private void doAroundAdd(InterceptorEntity anno){

    }
}
