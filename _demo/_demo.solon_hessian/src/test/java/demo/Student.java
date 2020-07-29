package demo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Student implements Serializable {
    private String name;
    public static String hobby = "eat";
    transient private String address;
}
