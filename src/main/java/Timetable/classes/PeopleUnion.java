package Timetable.classes;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Component
public class PeopleUnion {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private PeopleUnionType type;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="parent_id")
    private PeopleUnion parent;

    @OneToMany(mappedBy = "parent")
    Set<PeopleUnion> unions = new HashSet<>();

    @OneToMany
    private Set<User> users;

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
    }

    public Set<PeopleUnion> getUnions() {
        return unions;
    }

    public void setUnions(Set<PeopleUnion> unions) {
        this.unions = unions;
    }
}
