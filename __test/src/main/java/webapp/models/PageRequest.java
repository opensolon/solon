package webapp.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author noear 2022/12/10 created
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PageRequest<T> extends ModelRequest<T> {
    private int page;
    private int pageSize;
}
