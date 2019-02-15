package org.noear.solon.extend.rockuapi;

import lib.sponge.rock.models.AppModel;
import noear.snacks.ONode;
import noear.water.utils.TextUtil;
import org.noear.solon.core.XContext;

public abstract class RockApi implements UApi {

    public RockApiParams params = new RockApiParams();
    public RockApiResult result = new RockApiResult();

    public ONode $(String key){
        return params.data.get(key);
    }

    public Object call(XContext cxt) {
        try {
            exec(cxt);
        } catch (Exception e) {
            this.result.error = e;
        }

        return this.result;
    }

    public boolean isUsingCache(){return false;}
    public int cacheSeconds() {
        return 60 * 5;
    }
    public abstract String name();
    public abstract void exec(XContext ctx) throws Exception;


    ////////////////////////////////////////////////////////////
    private String _outs = null;
    private String _nouts = null;
    protected boolean isOut(String key) {
        if (_outs == null) {
            _outs = $("outs").getString();
        }

        if(_outs == null)
            return false;
        else
            return _outs.indexOf(key) > -1;
    }
    protected boolean isNotout(String key) {
        if (_nouts == null) {
            _nouts = $("nouts").getString();
        }

        if (_nouts == null) {
            return false;
        } else {
            return _nouts.indexOf(key) > -1;
        }
    }
    //
    //参数检查
    //
    protected final boolean checkParamsIsOk(String... keys) {
        return checkParamsIsOk(false, keys);
    }

    protected final boolean checkParamsIsOk(boolean isNotEmpty, String... keys) {
        StringBuilder sb = new StringBuilder();
        ONode d = params.data;

        for (String key : keys) {
            if (d.contains(key) == false) {
                sb.append(',').append(key);
            } else {
                if (isNotEmpty && TextUtil.isEmpty(d.get(key).getString())) {
                    sb.append(',').append(key);
                }
            }
        }

        if (sb.length() > 1) {
            result.code = RockCode.CODE_13;
            result.message = sb.toString().substring(1);
            return false;
        } else {
            return true;
        }
    }

    protected final boolean checkParamsIsNot0(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if ($(key).getInt() == 0) {
                sb.append(',').append(key);
            }
        }

        if (sb.length() > 1) {
            result.code = RockCode.CODE_13;
            result.message = sb.toString().substring(1);
            return false;
        } else {
            return true;
        }
    }


    ////////////////////////////////////////////////////////////
    //
    //获取当前通道的应用配置信息
    //
    public long getUserID(){
        return 0l;
    }

    public AppModel getApp() throws Exception {
        AppModel app = params.getApp();
        if (app == null) {
            throw new Exception("无法获取AppModel");
        }
        return app;
    }
}
