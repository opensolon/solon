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

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.handle.MethodType;
import webapp.models.UserModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Singleton(false)
@Mapping("/demo2/param")
@Controller
public class ParamController {
    //支持post和get参数
    @Mapping("body")
    public String test_body(Context ctx) throws IOException {
        return ctx.body();
    }

    @Mapping("header")
    public String test_header(@Header("Test-Token") String token) throws IOException {
        return token;
    }

    @Mapping("cookie")
    public String test_cookie(@Cookie("Test-Token") String token) throws IOException {
        return token;
    }

    @Mapping("int")
    public Object test_int(int num) throws IOException {
        return num; //没有传入时，默认为0
    }

    @Mapping("decimal")
    public Object test_decimal(BigDecimal num) throws IOException {
        return num;
    }

    //支持post和get参数
    @Mapping("d/*")
    public String test_d(Context ctx, String name) {
        return name;
    }


    //支持path var
    @Mapping("e/{p_q}/{obj}/{id}")
    public String test_e(Context ctx, String p_q, String obj, String id) {
        return ctx.path() + ":" + p_q + "-" + obj + "-" + id;
    }

    //支持字符串数组参数（暂时只支持字符串数据）
    @Mapping("array_str")
    public Object test_f(Context ctx, String[] aaa, String ccc) throws Exception{
        return aaa;
    }

    @Mapping("array_str2")
    public Object test_f2(Context ctx, List<String> aaa, String ccc) throws Exception{
        return aaa;
    }

    @Mapping("array_Int")
    public Object test_f2(Context ctx, Integer[] aaa, String ccc) throws Exception{
        return aaa;
    }

    @Mapping("array_Int2")
    public Object test_f22(Context ctx, Integer[] aaa, String ccc) throws Exception{
        return aaa;
    }

    @Mapping("array_int")
    public Object test_f3(Context ctx, int[] aaa, String ccc) throws Exception{
        return aaa;
    }

    //支持上传文件参数
    @Mapping("file")
    public String test_g(Context ctx, String title, UploadedFile file, String label) throws Exception{
        return ctx.path();
    }

    @Mapping("model")
    public UserModel test_h1(Context ctx, UserModel model) throws Throwable {
        return model;
    }

    //支持时间参数（要加XParam指定格式）
    @Mapping("date")
    public Object test_h2(Context ctx, Date date, Date date2) throws Exception{
        return date + " # " + date2;
    }

    @Mapping(value = "login", method = MethodType.POST)
    public Object test_h3(String username, String password) throws Exception{
        return username + " # " + password;
    }
}
