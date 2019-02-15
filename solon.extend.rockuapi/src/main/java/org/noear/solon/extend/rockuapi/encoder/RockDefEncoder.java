package org.noear.solon.extend.rockuapi.encoder;

import org.noear.solon.core.XContext;
import lib.sponge.rock.models.AppModel;

public class RockDefEncoder implements  RockEncoder {
    @Override
    public String tryEncode(XContext context, AppModel app, String text) throws Exception {
        return text;
    }
}
