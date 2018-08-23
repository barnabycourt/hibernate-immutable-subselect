package org.hibernate.bugs.models;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Immutable
public class ImmutableEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String selector;

    public ImmutableEntity() {
    }

    public ImmutableEntity(String selector) {
        this.selector = selector;
    }

    public Long getId() {
        return id;
    }

    public String getSelector() {
        return selector;
    }

}