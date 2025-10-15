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
package labs.test4;

import org.noear.snack4.ONode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author kevin
 * @Date 2022-10-02 20:52
 * @Description
 */
public abstract class AbstractProxyService {

    /**
     * 内部list
     */
    private final List<String> innerList = new ArrayList<>();


    public AbstractProxyService() {
        //内部有个线程，不断在使用 innerList的值
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(ONode.ofBean(innerList).toJson());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        }.start();
    }

    /**
     * 外部初始化 innerList
     *
     * @param str
     */
    public void addStr(String str) {
        innerList.add(str);
    }


    public void print() {
        System.out.println(ONode.ofBean(innerList).toJson());
    }
}
