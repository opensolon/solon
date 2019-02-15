package org.noear.solon.extend.rockuapi.decoder;

import org.noear.solon.core.XContext;
import lib.sponge.rock.models.AppModel;

public interface RockDecoder {
    String tryDecode(XContext context, AppModel app, String text)  throws Exception;
}
