package webapp.demo2_mvc;

import lombok.Data;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * @author noear 2021/5/11 created
 */
@Data
public class ValidViewModel {
    @Validated
    List<ValidModel> list;
}
