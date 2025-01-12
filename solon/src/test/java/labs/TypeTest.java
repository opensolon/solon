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
package labs;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author noear 2024/7/3 created
 */
public class TypeTest {
    public static void main(String[] args) {
        for(Method method : Model.class.getDeclaredMethods()) {
            System.out.println(method.getName());
            System.out.println(method.getReturnType());
            System.out.println(method.getGenericReturnType());
            System.out.println(method.getGenericReturnType().getClass());
            System.out.println(method.getAnnotatedReturnType());
            for(Type type : method.getGenericParameterTypes()) {
                System.out.println(">>" + type);
            }

            System.out.println("----------");
        }
    }

    public static class Model {
        public String fun1() {
            return "";
        }

        public List<String> fun2() {
            return Collections.emptyList();
        }

        public <T> T fun3(T v) {
            return null;
        }
    }
}
