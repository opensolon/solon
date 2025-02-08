package demo.ai;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.rx.SimpleSubscriber;
import org.reactivestreams.Publisher;

import java.io.IOException;

/**
 * @author noear 2025/1/28 created
 */
public class DemoFuncTest {

    @Test
    public void case11() throws IOException {
        //过程参考：https://blog.csdn.net/owenc1/article/details/142812656
        //https://blog.csdn.net/star_nwe/article/details/140559454
        ChatModel chatModel = ChatModel.of("http://localhost:8080")
                //.globalFunctionAdd(Solon.context().getBeanOrNew(WeatherChatFunction.class)) //方便组件模式构建
                .globalFunctionAdd("get_weather", decl -> decl
                        .description("获取指定城市的天气情况")
                        .stringParam("location", "根据用户提到的地点推测城市")
                        .handle(args -> {
                            String location = (String) args.get("location");
                            return location + "的天气是24c.";
                        })
                )
                .build();

        //一次性返回
        ChatResponse resp = chatModel
                .prompt(ChatMessage.ofUser("今天的杭州天气怎么样？"))
                .call();

        //打印消息
        System.out.println(resp.getMessage().getContent());
    }

    @Test
    public void case12() throws IOException {
        //过程参考：https://blog.csdn.net/owenc1/article/details/142812656
        //https://blog.csdn.net/star_nwe/article/details/140559454
        ChatModel chatModel = ChatModel.of("http://localhost:8080")
                .build();

        //一次性返回
        ChatResponse resp = chatModel
                .prompt(ChatMessage.ofUser("今天的杭州天气怎么样？"))
                .options(o -> o
                        //.functionAdd(Solon.context().getBeanOrNew(WeatherChatFunction.class)) //方便组件模式构建
                        .functionAdd("get_weather", decl -> decl
                                .description("获取指定城市的天气情况")
                                .stringParam("location", "根据用户提到的地点推测城市")
                                .handle(args -> {
                                    String location = (String) args.get("location");
                                    return location + "的天气是24c.";
                                })
                        ))
                .call();

        //打印消息
        System.out.println(resp.getMessage().getContent());
    }

    @Test
    public void case13() throws IOException {
        ChatModel chatModel = ChatModel.of("http://localhost:8080")
                .globalFunctionAdd("server_start", decl -> decl
                        .description("根据ip启动云主机")
                        .stringParam("ip", "根据用户描述推测ip")
                        .stringParam("time", "根据用户描述推测时间")
                        .handle(args -> {
                            String ip = (String) args.get("ip");
                            String time = (String) args.get("time");
                            //尝试调用启动接口...开始反馈：
                            return "已经尝试启动云主机 " + ip + "，并在" + time + "后检查了它的状态。";
                        })
                )
                .build();

        ChatResponse resp = chatModel
                .prompt(ChatMessage.ofUser("启动云主机，两分钟后告知我状态 172.18.26.11？"))
                .call();

        System.out.println(resp.getMessage().getContent());
    }
}