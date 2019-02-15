package org.noear.solon.extend.rockuapi.decoder;

import org.noear.solon.core.XContext;
import lib.sponge.rock.models.AppModel;

public class RockDefDecoder implements RockDecoder {
    @Override
    public String tryDecode(XContext context, AppModel app, String text) throws Exception {
        return text;
    }
}
