package org.noear.solon.extend.graphql.test;

import static com.jayway.jsonpath.JsonPath.read;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import demo.App;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.snack.ONode;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

/**
 * @author fuzi1996
 * @since 2.3
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class ParamAnnoTest extends HttpTester {

    @Test
    public void testParamAnno() throws IOException {
        ONode param = new ONode();
        ONode variables = new ONode();
        param.set("query",
                ResourceUtil.getResourceAsString("/query/queryBookById.gqls"));
        variables.set("id", "book-1");
        param.set("variables", variables);
        String json = param.toJson();

        String content = path("/graphql").bodyJson(json).post();
        assertThat(read(content, "$.bookById"), notNullValue());
        assertThat(read(content, "$.bookById.name"), is("book-1"));
        assertThat(read(content, "$.bookById.pageCount"), is(1));
        assertThat(read(content, "$.bookById.author"), notNullValue());
        assertThat(read(content, "$.bookById.author.firstName"), is("J"));
        assertThat(read(content, "$.bookById.author.lastName"), is("K"));

    }

}
