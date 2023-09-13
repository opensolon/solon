package demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "http_entity")
public class HttpEntity {
    @Id
    private String id;
    @Column(name = "description")
    private String description;
    @Column(name = "type")
    private String type;
}