package classes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Type;
import org.hibernate.query.Query;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Entity
public class Auditorium {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String name;
    @Column
    private Integer maxStudents;

    @Type( type = "json" )
    @Column( columnDefinition = "json" )
    private Map<String, Integer> additional;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Map<String, Integer> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, Integer> additional) {
        this.additional = additional;
    }

    public static ObservableList<Auditorium> getAuditoriums() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "FROM Auditorium";
        Query query = session.createQuery(hql);
        List results = query.list();
        ObservableList auditoriums = FXCollections.observableArrayList(results);
        return auditoriums;
    }

    public static ObservableList<Auditorium> searchAuditoriums(String text) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        ObservableList<Auditorium> auditoriums;
        Transaction tx = null;
        try {

            tx = session.beginTransaction();

            // Create CriteriaBuilder
            CriteriaBuilder builder = session.getCriteriaBuilder();


            // Create CriteriaQuery
            CriteriaQuery<Auditorium> criteria = builder.createQuery(Auditorium.class);
            Root root = criteria.from(Auditorium.class);
            criteria.where(builder.like(root.get("name"), "%" + text + "%"));

            //criteria.where(Restrictions.ilike("name", text));

            // here get object
            auditoriums = FXCollections.observableArrayList(session.createQuery(criteria).getResultList());
            tx.commit();


        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            System.out.println("Exception: " + ex.getMessage());
            ex.printStackTrace(System.err);
            auditoriums = null;
        } finally {
            session.close();
        }
        return auditoriums;
    }

    public static Auditorium getAuditoriumByName(String name) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Auditorium auditorium = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            // Create CriteriaBuilder
            CriteriaBuilder builder = session.getCriteriaBuilder();

            // Create CriteriaQuery
            CriteriaQuery<Auditorium> criteria = builder.createQuery(Auditorium.class);
            Root root = criteria.from(Auditorium.class);
            criteria.where(builder.like(root.get("name"), "%" + name + "%"));
            //criteria.where(Restrictions.ilike("name", text));

            // here get object
            auditorium = FXCollections.observableArrayList(session.createQuery(criteria).setMaxResults(1).getResultList()).get(0);
            tx.commit();

        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            System.out.println("Exception: " + ex.getMessage());
            ex.printStackTrace(System.err);

        } finally {
            session.close();
        }
        return auditorium;
    }
}
