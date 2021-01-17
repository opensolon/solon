package server.model;

import io.protostuff.Tag;
import lombok.Data;

import java.io.Serializable;

@Data
public class Point implements Serializable {
    @Tag(1)
    private int x;
    @Tag(2)
    private int y;
}
