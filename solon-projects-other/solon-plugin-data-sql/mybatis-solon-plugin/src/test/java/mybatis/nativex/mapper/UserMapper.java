package mybatis.nativex.mapper;

import mybatis.nativex.entity.TestUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author songyinyin
 * @since 2023/5/27 12:31
 */
@Mapper
public interface UserMapper {

    TestUser selectById(Long userId);

}
