package webapp.demo5_rpc.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserModel implements Serializable {
    private long id;
    private String name;
    private int sex;
    private String label;
}
