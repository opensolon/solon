package org.noear.solon.extend.rockuapi.encoder;

import noear.water.utils.EncryptUtil;
import org.noear.solon.core.XContext;
import lib.sponge.rock.models.AppModel;

public class RockMd5Encoder implements  RockEncoder {
    @Override
    public String tryEncode(XContext context, AppModel app, String text) throws Exception {
        return EncryptUtil.md5(text);
    }
}
