package webapp.demo3_upload;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;

@Mapping("/demo3/upload")
@Controller
public class UploadController {

    //支持上传文件参数（file 变量名，与表单变量名保持一至）
    @Mapping(path = "f1", method = MethodType.POST)
    public String test_f1(Context ctx, UploadedFile file, UploadedFile file2) throws Exception {
        if (file != null) {
            if (file2 == null) {
                return "成功：" + file.getName() + ": " + file.getContentSize() + "; null";
            } else {
                return "成功：" + file.getName() + ": " + file.getContentSize() + "; " + file2.getName() + ": " + file2.getContentSize();
            }
        }

        return "失败：" + ctx.path();
    }

    @Mapping(path = "f11", method = MethodType.POST)
    public String test_f11(String userName) throws Exception {
        if (userName == null) {
            return "我没接数据：）";
        } else {
            return userName;
        }
    }

    @Mapping(path = "f11_2", method = MethodType.POST, multipart = true)
    public String test_f11_2(String userName) throws Exception {
        if (userName == null) {
            return "我没接数据：）";
        } else {
            return userName;
        }
    }

    @Mapping(path = "f12", method = MethodType.POST)
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
    public String test_f13_s(UploadModel um) throws Exception {
        if (um.userName == null) {
            return "userName is null";
        }

        if (um.file == null) {
            return "file is null";
        }

        return um.userName + ": " + um.file.getName() + "- " + um.file.getContentSize();
    }

    //支持上传文件参数
    @Mapping("f2")
    public String test_f2(Context ctx) throws Exception {
        UploadedFile file = ctx.file("file"); //（file 变量名，与表单变量名保持一至）

        return ctx.path();
    }
}
