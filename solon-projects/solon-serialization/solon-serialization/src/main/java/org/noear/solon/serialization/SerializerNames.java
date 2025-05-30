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
package org.noear.solon.serialization;

/**
 * 序列化方案名字（内部已适配的）
 *
 * @author noear
 * @since 3.0
 */
public interface SerializerNames {
    String AT_JSON = "@json";
    String AT_JSON_TYPED = "@type_json";
    String AT_XML = "@xml";
    String AT_XML_TYPED = "@type_xml";
    String AT_PROPERTIES = "@properties";
    String AT_FURY = "@fury";
    String AT_KRYO = "@kryo";
    String AT_ABC = "@abc";
    String AT_HESSIAN = "@hessian";
    String AT_PROTOBUF = "@protobuf";
}
