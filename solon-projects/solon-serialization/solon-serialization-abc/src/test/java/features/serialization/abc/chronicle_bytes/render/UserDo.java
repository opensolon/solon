/*
 * Copyright 2017-2024 noear.org and authors
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
package features.serialization.abc.chronicle_bytes.render;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.solon.ChrBytesSerializable;


/**
 * @author noear 2023/1/16 created
 */
@ToString
@Getter
@Setter
public class UserDo implements ChrBytesSerializable {
    String s0 = "";

    String s1 = "noear";

    Boolean b0 = false;
    boolean b1 = true;

    Long n0 = 0L;
    Long n1 = 1L;

    Double d0 = 0D;
    Double d1 = 1.0D;

    @Override
    public void serializeRead(BytesIn in) {
        s0 = in.readUtf8();
        s1 = in.readUtf8();
        b0 = in.readBoolean();
        b1 = in.readBoolean();
        n0 = in.readLong();
        n1 = in.readLong();
        d0 = in.readDouble();
        d1 = in.readDouble();

    }

    @Override
    public void serializeWrite(BytesOut out) {
        out.writeUtf8(s0);
        out.writeUtf8(s1);
        out.writeBoolean(b0);
        out.writeBoolean(b1);
        out.writeLong(n0);
        out.writeLong(n1);
        out.writeDouble(d0);
        out.writeDouble(d1);
    }
}
