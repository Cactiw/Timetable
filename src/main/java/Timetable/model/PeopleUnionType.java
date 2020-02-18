package Timetable.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Entity
@Component
public class PeopleUnionType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @Column
    private String name;

    @ManyToOne()
    private PeopleUnionType child;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PeopleUnionType getChild() {
        return child;
    }

    public void setChild(PeopleUnionType child) {
        this.child = child;
    }

    public Integer getId() {
        return id;
    }
}
