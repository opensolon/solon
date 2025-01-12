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
package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.annotation.Singleton;

import java.io.IOException;

/**
 * @author noear 2020/12/20 created
 */

@Singleton(false)
@Mapping("/demo2/param2/anno")
@Controller
public class Param2AnnoController {

    @Mapping("required")
    public Object test_pm_required(@Param(required = true) String name) throws IOException {
        return name;
    }

    @Mapping("required_def")
    public Object test_pm_reqdef(@Param(required = true, defaultValue = "noear") String name) throws IOException {
        return name;
    }

    @Mapping("def")
    public Object test_pm_def(@Param(defaultValue = "noear") String name) throws IOException {
        return name;
    }

    @Mapping("name")
    public Object test_pm_name(@Param("n2") String name) throws IOException {
        return name;
    }
}
