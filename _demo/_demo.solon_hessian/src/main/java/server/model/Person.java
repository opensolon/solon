package server.model;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class Person implements Serializable {
    @Tag(1)
    private int age;
    @Tag(2)
    private String name;
    @Tag(3)
    private transient String sensitiveInformation;
    @Tag(4)
    private Date birthDay;
}