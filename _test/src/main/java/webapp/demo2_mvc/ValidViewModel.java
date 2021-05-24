package webapp.demo2_mvc;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author noear 2021/5/11 created
 */
@Data
public class ValidViewModel {
    @Valid
    List<ValidModel> list;
}
