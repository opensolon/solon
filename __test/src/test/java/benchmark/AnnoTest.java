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
package benchmark;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author noear 2021/5/8 created
 */
@Mapping
@Controller
@Options
public class AnnoTest {
    @Test
    public void test1() {
        List<MethodType> methodTypes = new ArrayList<>();

        long start = System.currentTimeMillis();

        for(int i=0;i<1000000; i++) {
            findMethodTypes1(methodTypes, t -> AnnoTest.class.getAnnotation(t) != null);
        }

        System.out.println(System.currentTimeMillis() - start);

    }

    @Test
    public void test2() {
        List<MethodType> methodTypes = new ArrayList<>();

        long start = System.currentTimeMillis();
        Annotation[] annotations = AnnoTest.class.getAnnotations();
        for(int i=0;i<100000; i++) {
            findMethodTypes2(methodTypes,annotations);
        }

        System.out.println(System.currentTimeMillis() - start);

    }


    private static void findMethodTypes1(List<MethodType> list, Predicate<Class> checker) {
        if (checker.test(Delete.class)) {
            list.add(MethodType.DELETE);
        }

        if (checker.test(Get.class)) {
            list.add(MethodType.GET);
        }

        if (checker.test(Patch.class)) {
            list.add(MethodType.PATCH);
        }

        if (checker.test(Post.class)) {
            list.add(MethodType.POST);
        }

        if (checker.test(Put.class)) {
            list.add(MethodType.PUT);
        }

        if (checker.test(Head.class)) {
            list.add(MethodType.HEAD);
        }
    }

    private static void findMethodTypes2(List<MethodType> list, Annotation[] annotations) {
        for(Annotation anno : annotations){
            if (anno.equals(Get.class)) {
                list.add(MethodType.GET);
            }

            if (anno.equals(Post.class)) {
                list.add(MethodType.POST);
            }

            if (anno.equals(Put.class)) {
                list.add(MethodType.PUT);
            }


            if (anno.equals(Patch.class)) {
                list.add(MethodType.PATCH);
            }

            if (anno.equals(Delete.class)) {
                list.add(MethodType.DELETE);
            }


            if (anno.equals(Head.class)) {
                list.add(MethodType.HEAD);
            }
        }

    }
}
