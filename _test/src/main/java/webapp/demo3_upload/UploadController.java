package webapp.demo3_upload;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XParam;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import webapp.models.UserModel;

import java.util.Date;

@XSingleton(false)
@XMapping("/demo3/upload")
@XController
public class UploadController {

    //支持上传文件参数（file 变量名，与表单变量名保持一至）
    @XMapping("f1")
    public String test_f1(XContext context, XFile file) throws Exception {
        if (file != null) {
            return "成功：" + file.name;
        }

        return "失败：" + context.path();
    }

    //支持上传文件参数
    @XMapping("f2")
    public String test_f2(XContext context) throws Exception{
        XFile file = context.file("file"); //（file 变量名，与表单变量名保持一至）

        return context.path();
    }

}
