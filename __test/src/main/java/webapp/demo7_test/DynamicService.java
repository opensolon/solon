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
package webapp.demo7_test;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.data.dynamicds.DynamicDs;
import org.noear.solon.data.dynamicds.DynamicDsKey;

/**
 * @author noear 2023/9/1 created
 */
@Managed
public class DynamicService {
    @DynamicDs("db_rock1")
    public String test1() throws Exception {
        System.out.println("ds===" + DynamicDsKey.current());
        return DynamicDsKey.current();
    }


    @DynamicDs("db_rock2")
    @Mapping("/test2")
    public String test2() throws Exception {
        System.out.println("ds===" + DynamicDsKey.current());
        return DynamicDsKey.current();
    }


    @DynamicDs
    @Mapping("/test3")
    public String test3() throws Exception {
        System.out.println("ds===" + DynamicDsKey.current());
        return DynamicDsKey.current();
    }
}
