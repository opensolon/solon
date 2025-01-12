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
package features;

import features._model.UserModel;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2021/12/3 created
 */
@SolonTest(App.class)
public class HttpJsonTest extends HttpTester {
    @Test
    public void json_map() throws Exception {
        ONode oNode = new ONode();

        UserModel userModel = new UserModel();
        userModel.id = 12;
        oNode.set("1", ONode.loadObj(userModel));

        assert path("/demo2/json/map").bodyOfJson(oNode.toJson()).post().equals("12");
    }

    @Test
    public void json_map2() throws Exception {
        ONode oNode = new ONode();

        UserModel userModel = new UserModel();
        userModel.id = 12;
        oNode.set("1", ONode.loadObj(userModel));

        ONode oNode1 = new ONode();
        oNode1.set("userMap", oNode);

        assert path("/demo2/json/map").bodyOfJson(oNode1.toJson()).post().equals("12");
    }

    @Test
    public void json_list() throws Exception {
        List<UserModel> list = new ArrayList<>();

        UserModel userModel = new UserModel();
        userModel.id = 12;

        list.add(userModel);

        userModel = new UserModel();
        userModel.id = 13;

        list.add(userModel);

        assert path("/demo2/json/list").bodyOfJson(ONode.stringify(list)).post().equals("12");
    }

    @Test
    public void json_map_r() throws Exception {
        ONode oNode = new ONode();

        UserModel userModel = new UserModel();
        userModel.id = 12;
        oNode.set("1", ONode.loadObj(userModel));

        String rst = path("/demo2/json/map_r")
                .header("Accept", "application/xml")
                .bodyOfJson(oNode.toJson()).post();

        assert rst.contains("<id>12</id>");
        assert rst.contains("@type") == false;
    }

    @Test
    public void json_map_xml() throws Exception {
        ONode oNode = new ONode();

        UserModel userModel = new UserModel();
        userModel.id = 12;
        oNode.set("1", ONode.loadObj(userModel));

        String rst = path("/demo2/json/map_xml")
                .bodyOfJson(oNode.toJson()).post();

        assert rst.contains("<id>12</id>");
        assert rst.contains("@type") == false;
    }
}