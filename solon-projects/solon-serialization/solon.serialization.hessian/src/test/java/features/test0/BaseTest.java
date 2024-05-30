package features.test0;

import features.model.UserDo;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.hessian.HessianBytesSerializer;
import org.noear.solon.serialization.hessian.HessianRender;
import org.noear.solon.test.SolonTest;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2024/5/30 created
 */
@SolonTest
public class BaseTest {
    @Test
    public void hello2() throws Throwable{
        UserDo userDo = new UserDo();

        Map<String, Object> data = new HashMap<>();
        data.put("time", new Date(1673861993477L));
        data.put("long", 12L);
        data.put("int", 12);
        data.put("null", null);

        userDo.setMap1(data);

        ContextEmpty ctx = new ContextEmpty(){
            private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            private ByteArrayInputStream inputStream;
            @Override
            public OutputStream outputStream() {
                return outputStream;
            }

            @Override
            public InputStream bodyAsStream() throws IOException {
                if(inputStream == null){
                    inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                }
                return inputStream;
            }
        };

        HessianRender render = new HessianRender();
        render.render(userDo, ctx);

        HessianBytesSerializer serializer = new HessianBytesSerializer();
        UserDo userDo2 = (UserDo)serializer.deserializeFromBody(ctx);

        System.out.println(userDo2);

        assert userDo.getB0() == userDo2.getB0();
        assert userDo.getS1().equals(userDo2.getS1());
        assert userDo.getMap1().size() == userDo2.getMap1().size();
    }
}
