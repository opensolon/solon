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
package benchmark;

import org.junit.jupiter.api.Test;
import org.noear.solon.Utils;

/**
 * @author noear 2022/7/8 created
 */
public class PathTest {
    @Test
    public void test1(){
        long start = System.currentTimeMillis();
        for(int i=0; i<100000; i++) {
            String path = "/a//b/c//d/e";
            if(path.contains("//")) {
                path = Utils.trimDuplicates(path, '/');
            }

            //System.out.println(path);
        }

        long timspan = System.currentTimeMillis() - start;
        System.out.println(timspan);
    }

    @Test
    public void test2(){
        long start = System.currentTimeMillis();
        for(int i=0; i<100000; i++) {
            String path = "/a//b/c//d/e";
            path = path.replace("//","/");

            //System.out.println(path);
        }

        long timspan = System.currentTimeMillis() - start;
        System.out.println(timspan);
    }

    @Test
    public void test3(){
        long start = System.currentTimeMillis();
        for(int i=0; i<100000; i++) {
            String path = "/a//b/c//d/e";
            while (path.contains("//")) {
                path = path.replace("//", "/");
            }

            //System.out.println(path);
        }

        long timspan = System.currentTimeMillis() - start;
        System.out.println(timspan);
    }
}
