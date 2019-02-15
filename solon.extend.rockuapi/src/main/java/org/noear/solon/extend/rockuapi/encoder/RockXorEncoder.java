package org.noear.solon.extend.rockuapi.encoder;

import noear.water.utils.EncryptUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.rockuapi.RockApiParams;
import lib.sponge.rock.models.AppModel;

public class RockXorEncoder implements  RockEncoder {
    @Override
    public String tryEncode(XContext context, AppModel app, String text) throws Exception {
        RockApiParams params = context.attr("params", null);

        if (params != null && params.data.get("g_encode").getInt() == 1) {
            return EncryptUtil.xorEncode(text, app.app_key);
        } else {
            return text;
        }
    }
}
