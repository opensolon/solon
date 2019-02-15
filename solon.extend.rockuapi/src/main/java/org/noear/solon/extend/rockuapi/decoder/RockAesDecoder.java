package org.noear.solon.extend.rockuapi.decoder;

import noear.water.utils.EncryptUtil;
import org.noear.solon.core.XContext;
import lib.sponge.rock.models.AppModel;

import java.net.URLDecoder;

public class RockAesDecoder implements RockDecoder {
    @Override
    public String tryDecode(XContext context, AppModel app, String text) throws Exception {
        if (text.indexOf("{") < 0 && text.indexOf("<") < 0) {
            if (text.indexOf('%') >= 0) {
                text = new String(URLDecoder.decode(text, "UTF-8"));
            }

            return EncryptUtil.aesDecrypt(text, app.app_key, null);
        }

        return text;
    }
}
