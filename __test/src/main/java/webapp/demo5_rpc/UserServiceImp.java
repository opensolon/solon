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
package webapp.demo5_rpc;

import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import org.noear.solon.annotation.Post;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.UploadedFile;
import webapp.demo5_rpc.protocol.UserModel;
import webapp.demo5_rpc.protocol.UserService;

import java.io.File;

//开启bean的远程服务
@Mapping("/demo5/user/")
@Remoting
public class UserServiceImp implements UserService {
    @Get
    @Post
    @Override
    public UserModel getUser(Integer userId) {
        UserModel model = new UserModel();
        model.setId(userId);
        model.setName("user-" + userId);

        return model;
    }

    @Override
    public UserModel getUserPut(Integer userId) {
        return null;
    }

    @Override
    public UserModel addUser(UserModel user) {
        return user;
    }

    @Override
    public String uploadFile(UploadedFile file) {
        return file.getName();
    }

    @Override
    public void showError() {
        throw new RuntimeException("我要出错");
    }

    @Override
    public void showVoid() {

    }
}
