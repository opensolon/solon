/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package labs.test3;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Around;
import org.noear.solon.core.AppContext;
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
        doInterceptorAdd(anno.annotationType().getAnnotation(Around.class));
    }

    /**
     * 方案2
     * */
    private void xxx2(AppContext context, Annotation anno){
        //@since 1.10 //支持拦截注解的别名注解形式
        for (Annotation anno2 : anno.annotationType().getAnnotations()) {
            if (anno2 instanceof Around) {
                doInterceptorAdd((Around) anno2);
            } else {
                InterceptorEntity ie2 = context.beanInterceptorGet(anno2.annotationType());
                if (ie2 != null) {
                    doInterceptorAdd(ie2);
                }
            }
        }
    }

    private void doInterceptorAdd(Around anno){

    }

    private void doInterceptorAdd(InterceptorEntity anno){

    }
}
