package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2022/3/1 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class BigHeaderTest extends HttpTestBase {
    @Test
    public void test_header() throws Exception {
        //构发大json数据块(7k)
        ONode oNode = new ONode();

        for (int i = 0; i < 40; i++) {
            ONode n1 = new ONode();
            n1.set("name", String.valueOf(i));
            n1.set("time", System.currentTimeMillis());
            n1.set("data0", "升级解密功能，支持eval,window.eval,window[\"eval\"]等的解密");
            n1.set("data1", "2008年的开幕式给我留下了深刻的印象，因为它在最伟大的时刻展现了中国精神，演员们穿着中国传统服装，齐声演奏中国传统乐器，展示了大量的功夫和杂技。就像中国说的：“这就是我们，我们不会改变。”正是这句话所呈现出的“大胆”，激励着我来到中国。");
            n1.set("data2", "驻韩大使馆发言人指出，冬奥会是竞技比赛，专业性和技术性极强，每个项目都有明确的规则、标准和章程。短道速滑是一项危险性较高、容易出现争议的项目，为最大限度保障参赛运动员安全，提高比赛公平公正性，国际滑联不断修改、完善和细化比赛规则。北京冬奥会短道速滑项目就是根据国际滑联最新修订规则进行的。根据国际滑联的要求，比赛现场还新配备了时速高达90公里的“猎豹”超高速4K轨道摄像机，为裁判提供了充足的技术支持和依据。本次短道速滑项目英国籍裁判长彼得·沃斯先生曾担任过包括平昌冬奥会在内的三届冬奥会短道赛事裁判长，具有权威性。对于韩方有关质疑，国际滑联日前已发表正式声明，详细介绍了评判细则和事实依据。");
            oNode.add(n1);
        }

        String json = Base64.getEncoder().encodeToString(oNode.toJson().getBytes(StandardCharsets.UTF_8));
        String len = String.valueOf(json.length());
        System.out.println(json.length());

        assert path("/demo2/json/header")
                .header("p", json)
                .get().equals(len);
    }

    @Test
    public void test_cookie() throws Exception {
        //构发大json数据块(7k)
        ONode oNode = new ONode();

        for (int i = 0; i < 40; i++) {
            ONode n1 = new ONode();
            n1.set("name", String.valueOf(i));
            n1.set("time", System.currentTimeMillis());
            n1.set("data0", "升级解密功能，支持eval,window.eval,window[\"eval\"]等的解密");
            n1.set("data1", "2008年的开幕式给我留下了深刻的印象，因为它在最伟大的时刻展现了中国精神，演员们穿着中国传统服装，齐声演奏中国传统乐器，展示了大量的功夫和杂技。就像中国说的：“这就是我们，我们不会改变。”正是这句话所呈现出的“大胆”，激励着我来到中国。");
            n1.set("data2", "驻韩大使馆发言人指出，冬奥会是竞技比赛，专业性和技术性极强，每个项目都有明确的规则、标准和章程。短道速滑是一项危险性较高、容易出现争议的项目，为最大限度保障参赛运动员安全，提高比赛公平公正性，国际滑联不断修改、完善和细化比赛规则。北京冬奥会短道速滑项目就是根据国际滑联最新修订规则进行的。根据国际滑联的要求，比赛现场还新配备了时速高达90公里的“猎豹”超高速4K轨道摄像机，为裁判提供了充足的技术支持和依据。本次短道速滑项目英国籍裁判长彼得·沃斯先生曾担任过包括平昌冬奥会在内的三届冬奥会短道赛事裁判长，具有权威性。对于韩方有关质疑，国际滑联日前已发表正式声明，详细介绍了评判细则和事实依据。");
            oNode.add(n1);
        }

        String json = Base64.getEncoder().encodeToString(oNode.toJson().getBytes(StandardCharsets.UTF_8));
        String len = String.valueOf(json.length());
        System.out.println(json.length());

        Map<String, String> cookieMap = new HashMap<>();
        cookieMap.put("p", json);

        assert Integer.parseInt(path("/demo2/json/cookie")
                .cookies(cookieMap)
                .get()) > (json.length() - 10);
    }
}
