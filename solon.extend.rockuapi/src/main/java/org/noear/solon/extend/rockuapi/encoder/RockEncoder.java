package org.noear.solon.extend.rockuapi.encoder;

import lib.sponge.rock.models.AppModel;
import org.noear.solon.core.XContext;

public interface RockEncoder {
    String tryEncode(XContext context, AppModel app, String text) throws Exception;
}
