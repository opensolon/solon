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
package org.noear.solon.logging.utils;

import org.slf4j.MDC;

/**
 * @author noear
 * @since 1.0
 */
public final class TagsMDC {
    private static final TagsMetainfo metainfo = new TagsMetainfo();

    public static TagsMetainfo tag0(String tag0) {
        return metainfo.tag0(tag0);
    }

    public static TagsMetainfo tag1(String tag1) {
        return metainfo.tag1(tag1);
    }

    public static TagsMetainfo tag2(String tag2) {
        return metainfo.tag2(tag2);
    }

    public static TagsMetainfo tag3(String tag3) {
        return metainfo.tag3(tag3);
    }

    public static TagsMetainfo tag4(String tag4) {
        return metainfo.tag4(tag4);
    }

    public static class TagsMetainfo {
        public TagsMetainfo tag0(String tag0) {
            MDC.put("tag0", tag0);
            return this;
        }

        public TagsMetainfo tag1(String tag1) {
            MDC.put("tag1", tag1);
            return this;
        }

        public TagsMetainfo tag2(String tag2) {
            MDC.put("tag2", tag2);
            return this;
        }

        public TagsMetainfo tag3(String tag3) {
            MDC.put("tag3", tag3);
            return this;
        }

        public TagsMetainfo tag4(String tag4) {
            MDC.put("tag4", tag4);
            return this;
        }
    }
}
