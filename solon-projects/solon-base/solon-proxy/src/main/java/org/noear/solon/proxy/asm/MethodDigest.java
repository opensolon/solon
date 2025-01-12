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
package org.noear.solon.proxy.asm;

/**
 * 方法摘要
 * */
public class MethodDigest {
    public final int access;
    public final String methodName;
    public final String methodDesc;

    public MethodDigest(int access, String methodName, String methodDesc) {
        this.access = access;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MethodDigest)) {
            return false;
        }
        MethodDigest digest = (MethodDigest) obj;

        //access == digest.access //可能有有
        if (methodName != null && digest.methodName != null
                && methodName.equals(digest.methodName)
                && methodDesc != null && digest.methodDesc != null
                && methodDesc.equals(digest.methodDesc)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "MethodDigest{" +
                "access=" + access +
                ", methodName='" + methodName + '\'' +
                ", methodDesc='" + methodDesc + '\'' +
                '}';
    }
}