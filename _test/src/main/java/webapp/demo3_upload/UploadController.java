package webapp.demo3_upload;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;

@Singleton(false)
@Mapping("/demo3/upload")
@Controller
public class UploadController {

    //支持上传文件参数（file 变量名，与表单变量名保持一至）
    @Mapping(path = "f1", method = MethodType.POST)
    public String test_f1(Context context, UploadedFile file) throws Exception {
        if (file != null) {
            return "成功：" + file.name;
        }

        return "失败：" + context.path();
    }

    //支持上传文件参数
    @Mapping("f2")
    public String test_f2(Context context) throws Exception{
        UploadedFile file = context.file("file"); //（file 变量名，与表单变量名保持一至）

        return context.path();
    }

}
