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

import features._model.UserModel;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.wrap.ClassWrap;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class SpeetTest {
    @Test
    public void demo1() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            for(Method m: UserModel.class.getDeclaredMethods()){
                for(Parameter p : m.getParameters()){

                }
            }
        }

        long times = System.currentTimeMillis() - start;

        System.out.println("用时：" + times);
    }

    @Test
    public void demo2() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            for(Method m: ClassWrap.get(UserModel.class).getDeclaredMethods()){
                for(Parameter p : m.getParameters()){

                }
            }
        }

        long times = System.currentTimeMillis() - start;

        System.out.println("用时：" + times);
    }
}
