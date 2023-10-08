package demo.course.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import demo.course.support.CourseSupport;

/**
 * @author fuzi1996
 * @since 2.3
 */
public class Person {

    private final Long id;

    private final String firstName;

    private final String lastName;

    @JsonCreator
    public Person(
            @JsonProperty("id") Long id,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long id() {
        return this.id;
    }

    public String firstName() {
        return this.firstName;
    }

    public String lastName() {
        return this.lastName;
    }

    public static void save(Long id, String firstName, String lastName) {
        Person person = new Person(id, firstName, lastName);
        CourseSupport.personMap.put(id, person);
    }
}
