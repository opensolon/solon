package webapp.models;

import lombok.Data;

/**
 * @author noear 2022/12/10 created
 */
@Data
public class PageRequest<T> extends ModelRequest<T> {
    private int page;
    private int pageSize;
}
