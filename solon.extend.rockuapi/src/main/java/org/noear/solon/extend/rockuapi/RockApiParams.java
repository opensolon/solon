package org.noear.solon.extend.rockuapi;

import lib.sponge.rock.RockClient;
import lib.sponge.rock.models.AppModel;
import noear.snacks.ONode;
import noear.water.utils.TextUtil;
import org.noear.solon.core.XContext;

import java.sql.SQLException;

//接口输入参数
public class RockApiParams {
    public XContext context;

    //原始参数
    public String org_param = "";
    //原始令牌
    public String org_token;

    //签名（由客户端传入）
    public String sgin;
    //通道ID（由客户端传入）
    public int appID;
    //版本ID（由客户端传入）
    public int verID;

    //参数的数据
    public ONode data = new ONode();

    public int agroup_id() throws SQLException{
        if(appID>0) {
            return getApp().agroup_id;
        }else{
            return 0;
        }
    }

    private String _ip;
    public String getIP(){
        if(_ip == null){
            _ip = do_getIP(context);
        }

        return _ip;
    }

    //当前通道信息（通过appID获取）
    private AppModel _app;
    public AppModel getApp() throws SQLException {
        if (_app == null) {
            if (appID > 0) {
                _app = RockClient.getApp(appID);
            } else if (data.contains("akey")) {
                _app = RockClient.getApp(data.get("akey").getString());
            }
        }
        return _app;
    }

    //====================================

    @Override
    public String toString() {
        if(data == null)
            return "";
        else
            return data.toJson();
    }

    private static String do_getIP(XContext context){
        String ip =  context.header("RemoteIp");

        if (TextUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = context.header("X-Forwarded-For");
        }

        if (TextUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = context.header("Proxy-Client-IP");
        }

        if (TextUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = context.header("WL-Proxy-Client-IP");
        }

        if (TextUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = context.ip();
        }

        return ip;
    }
}
