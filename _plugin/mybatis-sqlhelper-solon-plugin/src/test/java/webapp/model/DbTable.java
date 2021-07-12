package webapp.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author noear 2021/7/5 created
 */
@Getter
@Setter
@ToString
public class DbTable {

    private String tableName;

    private String tableComment;

}
