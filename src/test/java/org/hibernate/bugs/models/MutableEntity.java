package org.hibernate.bugs.models;

import org.hibernate.bugs.models.ImmutableEntity;

import javax.persistence.*;
import java.util.Date;


@Entity
public class MutableEntity{

    @Id
    @GeneratedValue
    private Long id;

    private String changeable;

    @OneToOne
    private ImmutableEntity trouble;

    public MutableEntity() {
    }

    public MutableEntity(ImmutableEntity trouble, String changeable) {
        this.trouble = trouble;
        this.changeable = changeable;
    }

    public Long getId() {
        return id;
    }

    public ImmutableEntity getTrouble() {
        return trouble;
    }

    public String getChangeable() {
        return changeable;
    }

    public void setChangeable(String changeable) {
        this.changeable = changeable;
    }
}