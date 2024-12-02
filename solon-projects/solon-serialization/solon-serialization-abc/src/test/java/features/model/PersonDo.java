package features.model;

import org.noear.solon.serialization.abc.chronicle.bytes.ChrBytesInput;
import org.noear.solon.serialization.abc.chronicle.bytes.ChrBytesOutput;
import org.noear.solon.serialization.abc.chronicle.bytes.ChrBytesSerializable;

public class PersonDo implements ChrBytesSerializable {
    public String name;
    public int age;
    public double height;

    @Override
    public void serializeRead(ChrBytesInput in) {
        this.name = in._.readUtf8();
        this.age = in._.readInt();
        this.height = in._.readDouble();
    }

    @Override
    public void serializeWrite(ChrBytesOutput out) {
        out._.writeUtf8(this.name);
        out._.writeInt(this.age);
        out._.writeDouble(this.height);
    }
}
