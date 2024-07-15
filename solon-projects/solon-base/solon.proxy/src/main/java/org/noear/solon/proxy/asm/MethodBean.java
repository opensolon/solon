/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.proxy.asm;

public class MethodBean {

    public int access;
    public String methodName;
    public String methodDesc;

    public MethodBean() {
    }

    public MethodBean(int access, String methodName, String methodDesc) {
        this.access = access;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (!(obj instanceof MethodBean)){
            return false;
        }
        MethodBean bean = (MethodBean) obj;

        //access == bean.access //不管访问性，因为代理需要的只是 public
        //                &&

        if (methodName != null
                && bean.methodName != null
                && methodName.equals(bean.methodName)
                && methodDesc != null
                && bean.methodDesc != null
                && methodDesc.equals(bean.methodDesc)){
            return true;
        }
        return false;
    }
}