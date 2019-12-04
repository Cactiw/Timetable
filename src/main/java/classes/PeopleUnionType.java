package classes;

import javax.persistence.*;

@Entity
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
