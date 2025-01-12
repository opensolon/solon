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
package webapp.models;

import lombok.Data;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 账号登录实体类
 * @Author: 李涵祥
 * @Date: 2022/5/16
 */
@Data
public class LoginUsername {

    @NotBlank(message = "请输入用户名")
    @Length(min = 6, max = 16, message = "用户名最少6位最多16位")
    private String username;

    @NotBlank(message = "请输入密码")
    @Length(min = 6, max = 16, message = "密码最少6位最多16位")
    private String password;

    @NotNull(message = "请同意站点隐私政策 和 站点协议")
    private Boolean privacy;

}
