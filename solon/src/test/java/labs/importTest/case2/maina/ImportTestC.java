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
package labs.importTest.case2.maina;

import labs.importTest.case2.AutoConfig2;
import labs.importTest.case2.DemoConfig;
import org.noear.solon.Solon;

/**
 * @author noear 2023/10/18 created
 */
public class ImportTestC {
    public static void main(String[] args) {
        Solon.start(ImportTestC.class, args, app->{
            app.context().beanMake(AutoConfig2.class);
        });

        DemoConfig demoConfig = Solon.context().getBean(DemoConfig.class);
        System.out.println(":::" + demoConfig);
        assert demoConfig == null;
    }
}
