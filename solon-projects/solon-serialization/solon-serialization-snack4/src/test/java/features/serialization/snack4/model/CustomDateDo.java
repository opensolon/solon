package features.serialization.snack4.model;

import lombok.Getter;
import lombok.Setter;
import org.noear.snack4.annotation.ONodeAttr;

import java.util.Date;

/**
 * @author noear 2024/9/4 created
 */
@Setter
@Getter
public class CustomDateDo {
    private Date date;

    @ONodeAttr(format = "yyyy-MM-dd")
    private Date date2;
}
