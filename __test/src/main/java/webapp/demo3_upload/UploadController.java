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
package webapp.demo3_upload;

import org.noear.snack4.ONode;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;

import java.util.List;

@Mapping("/demo3/upload")
@Controller
public class UploadController {

    //支持上传文件参数（file 变量名，与表单变量名保持一致）
    @Post
    @Mapping("f1")
    public String test_f1(Context ctx, UploadedFile file, UploadedFile file2) throws Throwable {
        ONode oNode = new ONode();

        if (file != null) {
            oNode.getOrNew("state").setValue("成功");

            oNode.getOrNew("file").then(n -> {
                n.set("name", file.getName());
                n.set("size", file.getContentSize());
            });

            if (file2 != null) {
                oNode.getOrNew("file2").then(n -> {
                    n.set("name", file2.getName());
                    n.set("size", file2.getContentSize());
                });
            }
        } else {
            oNode.getOrNew("state").setValue("失败");
        }

        if (file != null) {
            file.delete();
        }

        return oNode.toJson();
    }

    @Post
    @Mapping("f11")
    public String test_f11(String userName) throws Exception {
        if (userName == null) {
            return "我没接数据：）";
        } else {
            return userName;
        }
    }

    @Post
    @Mapping(path = "f11_2", multipart = true)
    public String test_f11_2(String userName) throws Exception {
        if (userName == null) {
            return "我没接数据：）";
        } else {
            return userName;
        }
    }

    @Post
    @Mapping("f12")
    public String test_f12(String userName, UploadedFile file) throws Exception {
        if (userName == null) {
            return "userName is null";
        }

        if (file == null) {
            return "file is null";
        }

        return userName + ": " + file.getName() + "- " + file.getContentSize();
    }

    @Mapping(path = "f12_1", method = MethodType.POST, multipart = true)
    public String test_f12_1(Context ctx, String userName) throws Exception {
        UploadedFile file = ctx.file("file");
        if (userName == null) {
            return "userName is null";
        }

        if (file == null) {
            return "file is null";
        }

        return userName + ": " + file.getName() + "- " + file.getContentSize();
    }

    @Mapping(path = "f13", method = MethodType.POST)
    public String test_f13(String userName, UploadedFile file) throws Exception {
        if (userName == null) {
            return "userName is null";
        }

        if (file == null) {
            return "file is null";
        }

        return userName + ": " + file.getName() + "- " + file.getContentSize();
    }

    @Post
    @Mapping(path = "f13_2", multipart = true)
    public String test_f13_2(UploadModel um) throws Exception {
        if (um.userName == null) {
            return "userName is null";
        }

        if (um.file == null) {
            return "file is null";
        }

        return um.userName + ": " + um.file.getName() + "- " + um.file.getContentSize();
    }

    @Post
    @Mapping(path = "f13_3", multipart = true)
    public String test_f13_3(UploadModel um) throws Exception {
        if (um.userName == null) {
            return "userName is null";
        }

        if (um.icons == null) {
            return "icons is null";
        }

        return um.userName + "-" + um.icons.length;
    }

    //支持上传文件参数
    @Mapping("f2")
    public String test_f2(Context ctx) throws Exception {
        UploadedFile file = ctx.file("file"); //（file 变量名，与表单变量名保持一致）

        return ctx.path();
    }

    @Post
    @Mapping("f3")
    public int test_f3(UploadedFile[] file) throws Exception {
        return file.length;
    }

    @Post
    @Mapping("f4")
    public int test_f4(List<UploadedFile> file) throws Exception {
        return file.size();
    }
}