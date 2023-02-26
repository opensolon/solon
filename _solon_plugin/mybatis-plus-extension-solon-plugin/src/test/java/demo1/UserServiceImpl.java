package demo1;

import com.baomidou.mybatisplus.solon.service.impl.ServiceImpl;
import org.noear.solon.annotation.ProxyComponent;


/**
 * @author noear 2022/10/5 created
 */
@ProxyComponent
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
