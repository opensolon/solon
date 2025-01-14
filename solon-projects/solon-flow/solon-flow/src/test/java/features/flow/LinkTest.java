package features.flow;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.Link;
import org.noear.solon.flow.LinkDecl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 连接优先级排序测试
 *
 * @author noear 2025/1/11 created
 */
public class LinkTest {
    @Test
    public void case1(){
        Chain chain = new Chain("c1");
        List<Link> links = new ArrayList<>();

        links.add(new Link(chain, "n1", new LinkDecl("n2").priority(1)));
        links.add(new Link(chain, "n1", new LinkDecl("n3").priority(3)));
        links.add(new Link(chain, "n1", new LinkDecl("n4").priority(2)));

        Collections.sort(links);

        System.out.println(links);
    }
}
