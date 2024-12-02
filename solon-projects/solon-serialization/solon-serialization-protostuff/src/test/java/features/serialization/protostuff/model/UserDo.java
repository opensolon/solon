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
package features.serialization.protostuff.model;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author noear 2023/1/16 created
 */
@ToString
@Getter
@Setter
public class UserDo {
    @Tag(1) //从1开始
    String s0 = "";
    @Tag(2)
    String s1 = "noear";

    @Tag(3)
    Boolean b0 = false;
    @Tag(4)
    boolean b1 = true;

    @Tag(5)
    Long n0 = 0L;
    @Tag(6)
    Long n1 = 1L;

    @Tag(7)
    Double d0 = 0D;
    @Tag(8)
    Double d1 = 1.0D;
}
