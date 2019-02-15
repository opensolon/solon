package org.noear.solon.extend.rockuapi;

import org.noear.solon.extend.rockuapi.encoder.RockSha1Encoder;
import org.noear.solon.extend.rockuapi.encoder.RockXorEncoder;
import org.noear.solon.extend.rockuapi.interceptor.*;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.rockuapi.decoder.RockXorDecoder;

//@XMapping("/CMD/")
//@XBean
class _demo_api extends UApiHandler {

    @Override
    public void register() {
        addInterceptor(new StartInterceptor());

        addInterceptor(new ParamsBuildInterceptor(new RockXorDecoder()));
        addInterceptor(new ParamsAuthInterceptor(new RockSha1Encoder()));
        addInterceptor(new ExecuteInterceptor(null));

        addInterceptor(new OutputBuildInterceptor(new RockXorEncoder(),5));
        addInterceptor(new OutputSignInterceptor(new RockSha1Encoder())); //可选

        addInterceptor(new OutputInterceptor());


        addInterceptor(new EndInterceptor("#sevice","#tag"));

        addApi(CMD_0_0_1::new);
    }

    public class CMD_0_0_1 extends RockApi {

        @Override
        public String name() {
            return null;
        }

        public void exec(XContext context){

        }
    }
}