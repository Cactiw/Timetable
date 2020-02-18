package classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

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

    static public ObservableList getAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<PeopleUnionType> cq = cb.createQuery(PeopleUnionType.class);
        Root<PeopleUnionType> rootEntry = cq.from(PeopleUnionType.class);
        CriteriaQuery<PeopleUnionType> all = cq.select(rootEntry);

        TypedQuery<PeopleUnionType> allQuery = session.createQuery(all);
        ObservableList peopleUnionTypes = FXCollections.observableArrayList(allQuery.getResultList());
        return peopleUnionTypes;
    }
}
