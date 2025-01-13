package features.flow;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.NodeLink;
import org.noear.solon.flow.NodeLinkDecl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author noear 2025/1/11 created
 */
public class LinkTest {
    @Test
    public void case1(){
        Chain chain = new Chain("c1");
        List<NodeLink> links = new ArrayList<>();

        links.add(new NodeLink(chain, "n1", new NodeLinkDecl("n2").priority(1)));
        links.add(new NodeLink(chain, "n1", new NodeLinkDecl("n3").priority(3)));
        links.add(new NodeLink(chain, "n1", new NodeLinkDecl("n4").priority(2)));

        Collections.sort(links);

        System.out.println(links);
    }
}
