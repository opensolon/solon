package server.model;

import io.protostuff.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Person implements Serializable {
    @Tag(1)
    private int age;
    @Tag(2)
    private String name;
    private transient String sensitiveInformation;
    @Tag(3)
    private Date birthDay;
}