package server.model;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Point implements Serializable {
    @Tag(1)
    private int x;
    @Tag(2)
    private int y;
}
