package validation.demo1;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author noear 2021/5/10 created
 */
@Controller
public class ValidatorController {

    @Inject
    private ValidatorService validatorService;

    @Mapping
    public void validator() {
        ValidatorModel model = new ValidatorModel();
        model.setName("张三");
        model.setFriendNames(Arrays.asList("李四", "王五"));
        model.setAge(20);
        model.setTel("188888888881");
        model.setMoney(new BigDecimal(100));
        validatorService.validator(model,"也可以在参数上做验证");
    }
}
