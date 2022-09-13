package demo3;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * SaToken管理中心权限认证工具
 *
 * @author 多仔ヾ
 **/
public class AuthHelperForManager {

    /** 账户类型标识 **/
    public static final String TYPE = "manager";

    /** StpLogic **/
    public static StpLogic STPLOGIC ;//= new StpLogicJwtForSimple(TYPE);

}
