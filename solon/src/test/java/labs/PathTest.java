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

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Import;
import org.noear.solon.core.AppContext;

/**
 * @author noear 2022/12/2 created
 */
@Import(PathTest.class)
@Configuration
public class PathTest {
    public static void main(String[] args) {
        AppContext appContext = new AppContext();
        appContext.lifecycle(() -> {
            appContext.subBeansOfType(String.class, bean -> {
                System.out.println(bean);
            });
        });
        appContext.beanMake(PathTest.class);
        appContext.start();
    }

    @Managed("str1")
    public String str1() {
        return "1";
    }

    @Managed("str2")
    public String str2() {
        return "2";
    }
}