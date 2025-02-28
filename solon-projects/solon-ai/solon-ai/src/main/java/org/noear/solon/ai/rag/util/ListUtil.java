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
package org.noear.solon.ai.rag.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 列表工具
 *
 * @author noear
 * @since 3.1
 */
public final class ListUtil {
    /**
     * 分列
     */
    public static <T> List<List<T>> partition(List<T> list, int pageSize) {
        if (list.size() <= pageSize) {
            return Arrays.asList(list);
        } else {
            int pageCount = list.size() / pageSize;
            int pageLastSize = list.size() % pageSize;
            if (pageLastSize > 0) {
                pageCount++;
            }

            List<List<T>> result = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
                int start = i * pageSize;

                if (i == pageCount - 1) {
                    if (pageLastSize > 0) {
                        List<T> subList = list.subList(start, start + pageLastSize);
                        result.add(subList);
                        break;
                    }
                }

                List<T> subList = list.subList(start, start + pageSize);
                result.add(subList);
            }

            return result;
        }
    }
}