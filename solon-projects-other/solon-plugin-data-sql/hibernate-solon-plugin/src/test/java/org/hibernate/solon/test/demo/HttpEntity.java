package org.hibernate.solon.test.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "http_entity")
public class HttpEntity extends BaseTime {
    @Id
    private String id;
    @Column(name = "description")
    private String description;
    @Column(name = "type")
    private String type;
}