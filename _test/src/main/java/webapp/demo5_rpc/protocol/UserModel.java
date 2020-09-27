package webapp.demo5_rpc.protocol;

import lombok.Data;

@Data
public class UserModel {
    private long id;
    private String name;
    private int sex;
    private String label;
}
