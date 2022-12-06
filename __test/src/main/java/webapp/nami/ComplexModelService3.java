package webapp.nami;


import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.nami.annotation.Body;
import org.noear.nami.annotation.Mapping;
import org.noear.nami.annotation.NamiClient;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.solon.Utils;

/**
 * @author noear 2022/12/6 created
 */
@NamiClient(name="local", path="/nami/ComplexModelService3/", headers="TOKEN=a")
public interface ComplexModelService3 extends Filter {
    @Mapping("PUT")
    void save(@Body ComplexModel model);

    @Mapping("GET api/1.0.1")
    ComplexModel read(Integer modelId);

    //自带个过滤器，过滤自己：）
    default Result doFilter(Invocation inv) throws Throwable{
        inv.headers.put("Token", "Xxx");
        inv.headers.put("TraceId", Utils.guid());
        inv.config.setDecoder(SnackDecoder.instance);
        inv.config.setEncoder(SnackEncoder.instance);

        return inv.invoke();
    }
}
