package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.StringSerializer;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * @author noear 2021/10/12 created
 */
@Configuration
public class Config {
    @Bean
    public void initRender() {
        //
        // 自己定义一个序列化器（虽然，是个错的）
        //
        StringSerializer serializer = source -> source.toString();

        //
        // 注册为 @json 的渲染器
        //
        RenderManager.mapping("@json", new StringSerializerRender(false, serializer));

        //注：通过 StringSerializerRender ，可以快速实现一个Json渲染器。
    }
}
