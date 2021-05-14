package org.noear.solon.ext;


import java.io.Serializable;

/**
 * 可抛出的数据，用于数据传导（以实现两种方案：return data; throw data）
 *
 * 在Solon的mvc处理链中，DataThrowable 会做为普通的数据处理渲染
 *
 * <pre><code>
 * //定义个 DataThrowable 的数据结构
 * public class UapiCode extends DataThrowable{
 *     public final int code;
 *     public final String message;
 *
 *     public UapiCode(int code, String message){
 *         super(message);
 *         this.code = code;
 *         this.message = message;
 *     }
 * }
 *
 * @Bean
 * public class DemoService{
 *     @Inject
 *     UserMapper userMapper;
 *
 *     public UserModel getUser(int userId){
 *         if(userId == 0){
 *             //当与返回类型不同时，可能过 throw 抛出
 *             throw new UapiCode(1,"参数错误");
 *         }
 *
 *         return userMapper.selectById(userId);
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class DataThrowable extends RuntimeException implements Serializable {
    private Object data;

    /**
     * 数据获取
     * */
    public Object data(){
        return data;
    }

    /**
     * 数据设定
     * */
    public DataThrowable data(Object data){
        this.data = data;
        return this;
    }


    public DataThrowable() {
        super();
    }

    public DataThrowable(Throwable cause) {
        super(cause);
    }

    public DataThrowable(String message) {
        super(message);
    }

    public DataThrowable(String message, Throwable cause) {
        super(message, cause);
    }
}
