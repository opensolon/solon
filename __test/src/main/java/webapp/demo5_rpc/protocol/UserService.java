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
package webapp.demo5_rpc.protocol;

import org.noear.nami.annotation.NamiMapping;
import org.noear.nami.annotation.NamiParam;
import org.noear.nami.common.ContentTypes;
import org.noear.solon.core.handle.UploadedFile;

public interface UserService {
    UserModel getUser(Integer userId);

    @NamiMapping("PUT getUser")
    UserModel getUserPut(Integer userId);

    UserModel addUser(@NamiParam("user") UserModel user);

    @NamiMapping(headers = ContentTypes.FORM_DATA)
    String uploadFile(UploadedFile file);

    @NamiMapping("POST")
    String postEmpty();

    //@NamiMapping(headers = ContentTypes.FORM_DATA)
    //String addFile(File file);

    void showError();

    void showVoid();

    default UserModel getUserDef(Integer userId) {
        return getUser(userId);
    }
}