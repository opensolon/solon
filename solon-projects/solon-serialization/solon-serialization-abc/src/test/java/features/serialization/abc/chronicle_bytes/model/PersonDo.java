package features.serialization.abc.chronicle_bytes.model;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import org.noear.solon.serialization.abc.chronicle.bytes.ChrBytesSerializable;

public class PersonDo implements ChrBytesSerializable {
    public String name;
    public int age;
    public double height;

    @Override
    public void serializeRead(BytesIn in) {
        this.name = in.readUtf8();
        this.age = in.readInt();
        this.height = in.readDouble();
    }

    @Override
    public void serializeWrite(BytesOut out) {
        out.writeUtf8(this.name);
        out.writeInt(this.age);
        out.writeDouble(this.height);
    }
}
