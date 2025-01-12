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

import java.util.List;


/**
 * @author noear 2021/5/11 created
 */
@Data
public class ValidModel2 {
    @NotEmpty
    @Date
    private String field1;
    @NotEmpty
    @Date("yyyy-MM-dd")
    private String field2;
    @NotNull
    @DecimalMax(10.0)
    private Double field3;
    @DecimalMin(10.0)
    private double field4;
    @NotEmpty
    @Email
    private String field5;
    @NotEmpty
    @Email("\\w+\\@live.cn")
    private String field6;
    @NotNull
    @Max(10)
    private Integer field7;
    @Min(10)
    private int field8;
    @NotBlank
    private String field9;
    @NotEmpty
    private String field10;
    @NotNull
    private String field11;
    @NotNull
    private Integer field12;
    @Null
    private String field13;
    @Null
    private Double field14;
    @NotEmpty
    @Pattern("\\d{3}-\\d+")
    private String field15;
    @NotZero
    private Integer field16;
    @Length(min = 3)
    private String field17;
    @Size(min = 2)
    private List<String> field18;
}
