/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package features.serialization.protostuff.render;

import features.serialization.protostuff.model.UserDo;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.protostuff.ProtostuffBytesSerializer;
import org.noear.solon.serialization.protostuff.ProtostuffEntityConverter;
import org.noear.solon.serialization.protostuff.ProtostuffRender;
import org.noear.solon.test.SolonTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author noear 2024/5/30 created
 */
@SolonTest
public class BaseTest {
    @Test
    public void hello2() throws Throwable {
        UserDo userDo = new UserDo();
        userDo.setN1(12L);
        userDo.setS1("test2");

        ContextEmpty ctx = new ContextEmpty() {
            private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            @Override
            public OutputStream outputStream() {
                return outputStream;
            }

            @Override
            public void output(byte[] bytes) {
                try {
                    outputStream().write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public byte[] bodyAsBytes() throws IOException {
                return outputStream.toByteArray();
            }
        };

        ProtostuffRender render = new ProtostuffRender(new ProtostuffEntityConverter(new ProtostuffBytesSerializer()));
        render.render(userDo, ctx);

        ProtostuffBytesSerializer serializer = new  ProtostuffBytesSerializer();
        UserDo userDo2 = (UserDo) serializer.deserializeFromBody(ctx, UserDo.class);

        System.out.println(userDo2);

        assert userDo.getB0() == userDo2.getB0();
        assert userDo.getS1().equals(userDo2.getS1());
        assert userDo.getN1().equals(userDo2.getN1());
    }
}
