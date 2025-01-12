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
package webapp.demo2_mvc;

import lombok.Data;
import org.noear.solon.validation.annotation.*;

/**
 * @author noear 2021/5/11 created
 */
@Data
public class ValidModel3 {
    @Date
    private String field1;

    @NotEmpty
    @Date
    private String field10;

    @Email
    private String field2;


    @NotEmpty
    @Email
    private String field20;

    @Pattern("\\d{3}-\\d+")
    private String field3;

    @NotEmpty
    @Pattern("\\d{3}-\\d+")
    private String field30;
}
