package demo.course.support;

import demo.course.entity.Course;
import demo.course.entity.Person;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class CourseSupport {

    public static final Map<Long, Course> courseMap = new HashMap<>();

    public static final Map<Long, Person> personMap = new HashMap<>();

    static {
        Course.save(11L, "Ethical Hacking", 15L, Arrays.asList(22L, 26L, 31L));
        Course.save(19L, "Docker and Kubernetes", 17L, Arrays.asList(31L, 39L, 44L, 45L));

        Person.save(15L, "Josh", "Kelly");
        Person.save(17L, "Albert", "Murray");
        Person.save(22L, "Bonnie", "Gray");
        Person.save(26L, "John", "Perry");
        Person.save(31L, "Alaine", "Baily");
        Person.save(39L, "Jeff", "Peterson");
        Person.save(44L, "Jared", "Mccarthy");
        Person.save(45L, "Benjamin", "Brown");
    }
}
