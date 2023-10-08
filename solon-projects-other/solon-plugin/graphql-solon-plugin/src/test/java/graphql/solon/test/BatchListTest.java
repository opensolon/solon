package graphql.solon.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.nullValue;

import demo.course.entity.Course;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.snack.ONode;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import test.App;

/**
 * 测试批量查询
 *
 * @author fuzi1996
 * @since 2.3
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(value = App.class, properties = "tbk=demo.course")
public class BatchListTest extends HttpTester {

    /**
     * 测试批量查询中一对一查询
     */
    @Test
    public void testBatchQueryOne2One() throws IOException {
        ONode param = new ONode();
        param.set("query",
                ResourceUtil.getResourceAsString("/query/queryInstructorOneToOne.gqls"));
        String json = param.toJson();

        String content = path("/graphql").bodyJson(json).post();
        ONode oNode = ONode.loadStr(content);

        List<Course> courses = oNode.get("courses").toObjectList(Course.class);
        assertThat(courses, iterableWithSize(2));
        Course course0 = courses.get(0);
        Course course1 = courses.get(1);

        assertThat(course0.id(), is(19L));
        assertThat(course0.name(), is("Docker and Kubernetes"));
        assertThat(course0.instructor().id(), is(17L));
        assertThat(course0.instructor().firstName(), is("Albert"));
        assertThat(course0.instructor().lastName(), is("Murray"));
        assertThat(course0.students(), iterableWithSize(0));

        assertThat(course1.id(), is(11L));
        assertThat(course1.name(), is("Ethical Hacking"));
        assertThat(course1.instructor().id(), is(15L));
        assertThat(course1.instructor().firstName(), is("Josh"));
        assertThat(course1.instructor().lastName(), is("Kelly"));
        assertThat(course1.students(), iterableWithSize(0));
    }

    /**
     * 测试批量查询中一对多查询
     */
    @Test
    public void testBatchQueryOne2Many() throws IOException {
        ONode param = new ONode();
        param.set("query",
                ResourceUtil.getResourceAsString("/query/queryInstructorOneToMany.gqls"));
        String json = param.toJson();

        String content = path("/graphql").bodyJson(json).post();
        ONode oNode = ONode.loadStr(content);

        List<Course> courses = oNode.get("courses").toObjectList(Course.class);
        assertThat(courses, iterableWithSize(2));
        Course course0 = courses.get(0);
        Course course1 = courses.get(1);

        assertThat(course0.id(), is(19L));
        assertThat(course0.name(), is("Docker and Kubernetes"));
        assertThat(course0.instructor(), nullValue());
        assertThat(course0.students(), iterableWithSize(4));
        assertThat(course0.studentIds(), hasItems(31L, 39L, 44L, 45L));

        assertThat(course1.id(), is(11L));
        assertThat(course1.name(), is("Ethical Hacking"));
        assertThat(course1.instructor(), nullValue());
        assertThat(course1.students(), iterableWithSize(3));
        assertThat(course1.studentIds(), hasItems(22L, 26L, 31L));

    }
}
