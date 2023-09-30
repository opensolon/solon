package demo.course.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import demo.course.support.CourseSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.noear.solon.lang.Nullable;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class Course {

    private final Long id;

    private final String name;

    private final Long instructorId;

    private final List<Long> studentIds;

    @JsonCreator
    public Course(
            @JsonProperty("id") Long id, @JsonProperty("name") String name,
            @JsonProperty("instructor") @Nullable Person instructor,
            @JsonProperty("students") @Nullable List<Person> students) {

        this.id = id;
        this.name = name;
        this.instructorId = (instructor != null ? instructor.id() : -1);
        this.studentIds = (students != null ?
                students.stream().map(Person::id).collect(Collectors.toList()) :
                Collections.emptyList());
    }

    public Course(Long id, String name, Long instructorId, List<Long> studentIds) {
        this.id = id;
        this.name = name;
        this.instructorId = instructorId;
        this.studentIds = studentIds;
    }

    public Long id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public Long instructorId() {
        return this.instructorId;
    }

    public List<Long> studentIds() {
        return this.studentIds;
    }

    public List<Person> students() {
        return this.studentIds.stream().map(CourseSupport.personMap::get)
                .collect(Collectors.toList());
    }

    public Person instructor() {
        return CourseSupport.personMap.get(this.instructorId);
    }

    public static void save(Long id, String name, Long instructorId, List<Long> studentIds) {
        Course course = new Course(id, name, instructorId, studentIds);
        CourseSupport.courseMap.put(id, course);
    }

    public static List<Course> allCourses() {
        return new ArrayList<>(CourseSupport.courseMap.values());
    }

    // Course is a key in the DataLoader map

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return this.id.equals(((Course) other).id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
