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

import org.noear.snack4.ONode;
import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import webapp.models.UserModel;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/12/3 created
 */
@Mapping("/demo2/json/")
@Controller
public class JsonController {
    @Produces("text/json")
    @Mapping("/json")
    public Object json() {
        return "{}";
    }

    @Mapping("/map")
    public Object map(Map<String, UserModel> userMap, ModelAndView mv) {
        if (userMap == null) {
            return null;
        } else {
            return userMap.get("1").getId();
        }
    }

    @Mapping("/map_r")
    public Object map_r(Map<String, UserModel> userMap, ModelAndView mv) {
        return userMap;
    }

    @Produces("text/xml")
    @Mapping("/map_xml")
    public Object map_xml(Map<String, UserModel> userMap, ModelAndView mv) {
        return userMap;
    }

    @Mapping("/list")
    public Object list(@Body List<UserModel> userAry, ModelAndView mv) {
        if (userAry == null) {
            return null;
        } else {
            return userAry.get(0).getId();
        }
    }

    @Mapping("/list_query1")
    public Object list(@Body List<UserModel> userAry, String query1, Context ctx) {
        return query1 + ":" + ctx.queryString();
    }

    @Mapping("/bean")
    public Object bean(UserModel user) {
        return user;
    }

    @Mapping("/bean_map_str")
    public String bean_map_str(@Body Map<String, Object> user) {
        return ONode.ofBean(user).toJson();
    }

    @Mapping("/body")
    public Integer body(@Body String body) {
        if (Utils.isEmpty(body)) {
            return 0;
        } else {
            return body.length();
        }
    }

    @Mapping("/form")
    public Integer form(String p) {
        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }

    @Mapping("/header")
    public Integer header(@Header("p") String p) {
        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }

    @Mapping("/cookie2")
    public Integer cookie2(@Cookie("p") String p) {
        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }

    @Mapping("/cookie")
    public Integer cookie(Context ctx) {
        String p = ctx.cookie("p");

        if (Utils.isEmpty(p)) {
            return 0;
        } else {
            return p.length();
        }
    }
}
