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
package features.serialization.jackson3.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author noear 2024/9/4 created
 */
@Setter
@Getter
public class CustomDateDo {
    private Date date;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date2;

    @JsonIgnore
    private Date date3;

    @JsonProperty(value = "val_1")
    private String val1;

    @JsonIgnore
    private String val2;

    @JsonIgnore
    private String val3;
}
