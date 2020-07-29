package server.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Point implements Serializable {
    private int x;
    private int y;
}
