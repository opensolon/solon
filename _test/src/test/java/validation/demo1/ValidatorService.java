package validation.demo1;


import org.noear.solon.extend.aspect.annotation.Service;

import javax.validation.constraints.*;
import javax.validation.*;

/**
 * @author noear 2021/5/10 created
 */
@Service
public class ValidatorService {
    public void validator(@Valid ValidatorModel model, @NotNull String param) {

    }
}
