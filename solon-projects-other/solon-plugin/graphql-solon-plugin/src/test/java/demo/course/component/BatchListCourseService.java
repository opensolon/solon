package demo.course.component;

import demo.course.entity.Course;
import demo.course.entity.Person;
import demo.course.support.CourseSupport;
import graphql.solon.annotation.BatchMapping;
import graphql.solon.annotation.QueryMapping;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.noear.solon.annotation.Component;

/**
 * @author fuzi1996
 * @since 2.3
 */
@Component
public class BatchListCourseService {

    @QueryMapping
    public Collection<Course> courses() {
        return CourseSupport.courseMap.values();
    }

    @BatchMapping
    public List<Person> instructor(List<Course> courses) {
        return courses.stream().map(Course::instructor).collect(Collectors.toList());
    }

    @BatchMapping
    public List<List<Person>> students(List<Course> courses) {
        return courses.stream().map(Course::students).collect(Collectors.toList());
    }
}
