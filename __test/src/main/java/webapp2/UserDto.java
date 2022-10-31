package webapp2;

import lombok.Getter;

/**
 * @author noear 2022/10/31 created
 */
@Getter
public class UserDto {
    private long userId;
    private String name;

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                '}';
    }
}
