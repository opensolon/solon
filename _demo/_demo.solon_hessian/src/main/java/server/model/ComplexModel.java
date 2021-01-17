package server.model;

import io.protostuff.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ComplexModel<T> implements Serializable {
    @Tag(1)
    private Integer id;
    @Tag(2)
    private Person person;
    @Tag(3)
    private List<T> points;
}