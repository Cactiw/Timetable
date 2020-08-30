package Timetable.model;

import org.springframework.stereotype.Component;

import javax.persistence.*;

@Entity
@Component
public class PeopleUnionType {
    // TODO set @NonNull or @Nullable to each variable and it's getter according to it's properties in the table
    // TODO alter all setters to return new object instead of mutating existing one see BorderProperties for example

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @Column
    private String name;

    @ManyToOne()
    private PeopleUnionType parent;

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PeopleUnionType getParent() {
        return parent;
    }

    public void setParent(PeopleUnionType parent) {
        this.parent = parent;
    }

    public Integer getId() {
        return id;
    }
}
