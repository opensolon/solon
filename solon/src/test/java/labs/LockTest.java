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

import org.noear.solon.Utils;

/**
 * @author noear 2024/3/1 created
 */
public class LockTest {
    public static void main(String[] args){
        Utils.locker().lock();

        try{
            Utils.locker().lock();

            System.out.println("test");

            initAaa();

        }finally {
            Utils.locker().unlock();
        }
    }

    public static void initAaa(){
        Utils.locker().lock();

        try{
            System.out.println("init");

        }finally {
            Utils.locker().unlock();
        }
    }
}
