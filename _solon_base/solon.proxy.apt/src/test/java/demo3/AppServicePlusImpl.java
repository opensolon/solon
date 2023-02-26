package demo3;

import com.baomidou.mybatisplus.solon.service.impl.ServiceImpl;
import org.noear.solon.annotation.ProxyComponent;

/**
 * @author noear 2022/3/28 created
 */
@ProxyComponent
public class AppServicePlusImpl extends ServiceImpl<AppxMapperPlusEx, AppxModel> implements AppServicePlus {
}
