package server.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class Person implements Serializable {
    private int age;
    private String name;
    /**
     * transient字段不会序列化
     */
    private transient String sensitiveInformation;
    private Date birthDay;
}