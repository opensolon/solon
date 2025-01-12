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
package demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author noear 2023/2/23 created
 */
public class UserService$$ProxyDemo extends UserService {

    private static Method method0;
    private static Method method1;

    static {
        try {
            method0 = UserService.class.getMethod("getUserName");
            method1 = UserService.class.getMethod("setUserName", String.class);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }


    private InvocationHandler handler;

    public void UserService$SolonProxy_Demo(InvocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getUserName() {
        try {
            return (String) handler.invoke(this, method0, new Object[]{});
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void setUserName(String name) throws RuntimeException {
        try {
            handler.invoke(this, method1, new Object[]{name});
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
