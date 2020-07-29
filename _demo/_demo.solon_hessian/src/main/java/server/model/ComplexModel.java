package server.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ComplexModel<T> implements Serializable {
    private Integer id;
    private Person person;
    private List<T> points;
}