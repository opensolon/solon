package org.noear.solon.extend.rockuapi;

import noear.snacks.ONode;

import java.io.IOException;
import java.io.Serializable;

//接口输出结果
public class RockApiResult implements Serializable {
    public RockApiResult(){

    }

    public RockApiResult(int code){
        this.code = code;
    }

    //结果代码
    public int code = 1;
    //结果消息
    public String message = "";
    //结果数据
    public ONode data = new ONode().asObject();


    //异常信息
    public Exception error;

    ////////////////////////////

    //*自定义序列化支持
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        ONode temp = new ONode();
        temp.set("code", code);
        temp.set("msg", message);
        temp.set("data", data);

        out.writeObject(temp.toJson());
    }

    //*自定义序列化支持
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        String json = (String) in.readObject();
        ONode temp = ONode.tryLoad(json);

        code = temp.get("code").getInt();
        message = temp.get("msg").getString();
        data = temp.get("data");
    }

    ////////////////////////////

    public String toJson() {
        ONode temp = new ONode();
        temp.set("code", code);
        temp.set("msg", message);

        if (data != null && data.count() > 0) {
            temp.set("data", data);
        }

        return temp.toJson();
    }

    public String toString() {
        return toJson();
    }
}
