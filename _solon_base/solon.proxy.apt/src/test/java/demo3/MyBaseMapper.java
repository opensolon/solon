package demo3;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyBaseMapper<T> extends BaseMapper<T> {
}
