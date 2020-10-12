/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.test.spring;
import net.hasor.core.InjectSettings;

import javax.inject.Singleton;

/**
 * 使用Hasor的方式注入Spring的Bean。
 * @version : 2016年2月15日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class HasorBean {
    @InjectSettings("msg_1")
    private String msgValue1;
    @InjectSettings("${msg_2}")
    private String msgValue2;

    public String getMsgValue1() {
        return msgValue1;
    }

    public String getMsgValue2() {
        return msgValue2;
    }
}
