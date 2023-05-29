package demo1;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author noear 2022/10/5 created
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
