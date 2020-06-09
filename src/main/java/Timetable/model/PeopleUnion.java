package Timetable.model;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Component
public class PeopleUnion {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @ManyToOne
    private PeopleUnionType type;


    @ManyToOne
    @Nullable
    private PeopleUnion parent;

    @OneToMany(mappedBy = "parent")
    List<PeopleUnion> unions = new ArrayList<PeopleUnion>();

    @OneToMany(mappedBy = "group")
    private List<User> users;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public PeopleUnionType getType() {
        return type;
    }

    public void setType(PeopleUnionType type) {
        this.type = type;
    }

    public PeopleUnion getParent() {
        return parent;
    }

    public void setParent(PeopleUnion parent) {
        this.parent = parent;
        parent.getChildrenUnions().add(this);
    }

    public List<PeopleUnion> getChildrenUnions() {
        return unions;
    }

    public void setUnions(List<PeopleUnion> unions) {
        this.unions = unions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof PeopleUnion)) {
            return false;
        }
        PeopleUnion other = (PeopleUnion) obj;
        return this.getId().equals(other.getId());
    }
}
