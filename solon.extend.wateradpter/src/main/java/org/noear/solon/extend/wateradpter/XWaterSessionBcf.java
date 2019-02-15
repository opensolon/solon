package org.noear.solon.extend.wateradpter;


import noear.bcf.BcfClient;
import noear.bcf.models.BcfUserModel;
import noear.water.WaterSession;
import noear.water.utils.TextUtil;
import org.noear.solon.core.XContext;

public abstract class XWaterSessionBcf extends WaterSession {

    @Override
    public int expiry() {
        return 60 * 60 * 2;
    }

    public XContext context() {
        return XContext.current();
    }


    public abstract String service();

    public abstract void loadModel(BcfUserModel model) throws Exception;

    @Override
    public String cookieGet(String key) {
        String val = context().cookie(key);

        if (TextUtil.isEmpty(val)) {
            return stateGet(key);
        } else {
            return val;
        }
    }

    @Override
    public void cookieSet(String key, String val) {
        context().cookieSet(key, val, domain(), expiry());

        stateSet(key, val);
    }

    private int doGetPuid() {
        return Integer.parseInt(doGet("puid", "0"));
    }

    public final int getPUID() {
        int temp = doGetPuid();

        if (temp > 0 && hasReload()) {
            try {
                BcfUserModel user = BcfClient.login(temp);
                loadModel(user);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            if (stateHas("SESSION_UPDATE") == false) {
                stateSet("SESSION_UPDATE", "1");
                updateSessionID();
            }
        }

        return temp;
    }

    public final void setPUID(int puid) {
        doSet("puid", puid);
        set("puid", puid);
    }

    public final String getUserId() {
        return doGet("user_id", null);
    }

    public final void setUserId(String user_id) {
        doSet("user_id", user_id);
    }

    public final String getUserName() {
        return doGet("user_name", null);
    }

    public final void setUserName(String user_name) {
        doSet("user_name", user_name);
    }

    /////////////////////////////////////////////////

    public boolean has(String key) {
        return doHas(service() + "_" + key);
    }

    public boolean hasReload() {
        return doGetPuid() != servicePUID();
    }

    private int servicePUID() {
        return getAsInt("puid", 0);
    }

    public void set(String key, Object val) {
        doSet(service() + "_" + key, val);
    }

    public String get(String key, String def) {
        return doGet(service() + "_" + key, def);
    }

    public int getAsInt(String key, int def) {
        return Integer.parseInt(get(key, String.valueOf(def)));
    }

    public long getAsLong(String key, long def) {
        return Long.parseLong(get(key, String.valueOf(def)));
    }

    public double getAsDouble(String key, double def) {
        return Double.parseDouble(get(key, String.valueOf(def)));
    }

    protected String stateGet(String key) {
        return context().attr(key, null);
    }

    protected void stateSet(String key, String val) {
        context().attrSet(key, val);
    }

    protected boolean stateHas(String key) {
        return context().attr(key, null) != null;
    }
}