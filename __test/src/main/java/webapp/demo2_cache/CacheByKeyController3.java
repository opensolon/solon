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
package webapp.demo2_cache;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.data.annotation.CacheRemove;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
public class CacheByKeyController3 {
    /**
     * 执行结果缓存10秒，设定 key=test3_${label} ，并添加 tag=test3 标签（可以批量删除）
     */
    @Cache(key = "test3_${label}", tags = "test3", seconds = 10)
    @Mapping("/cache3/")
    public String cache(int label) {
        return LocalDateTime.now().toString();
    }
    /**
     * 执行后，清除 tag=test3 的所有缓存，并更新 key=test3_${label} 的缓存块
     */
    @CachePut(key = "test3_${label}")
    @CacheRemove(tags = "test3")
    @Mapping("/cache3/update")
    public String remove(int label) {
        return "清除成功-" + new Date();

    }




    @Cache(key = "test3", seconds = 10)
    @Mapping("/cache3/cache2")
    public String cache2() {
        return LocalDateTime.now().toString();
    }


}
