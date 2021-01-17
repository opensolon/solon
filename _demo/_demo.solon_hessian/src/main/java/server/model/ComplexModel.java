package server.model;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ComplexModel<T> implements Serializable {
    @Tag(1)
    private Integer id;
    @Tag(2)
    private Person person;
    @Tag(3)
    private List<T> points;
}