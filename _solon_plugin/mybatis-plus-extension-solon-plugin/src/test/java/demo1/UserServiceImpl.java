package demo1;

import com.baomidou.mybatisplus.solon.service.impl.ServiceImpl;
import org.noear.solon.aspect.annotation.Service;


/**
 * @author noear 2022/10/5 created
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
