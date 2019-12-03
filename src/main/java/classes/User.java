package classes;

import com.sun.istack.NotNull;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.query.Query;
import org.hibernate.type.StringNVarcharType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@Entity
@Table
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
        @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
})
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private Integer role;  // 0 - диспетчер, 1 - преподаватель, 2 - студент

    @NotNull
    @Column
    private String name;

    @NotNull
    @Column
    private String lastName;

    @Column
    private String surName;

    @Column
    private String email;

    @Type( type = "json" )
    @Column( columnDefinition = "json" )
    private Map<String, Integer> settings;

    @Type( type = "json" )
    @Column( columnDefinition = "json" )
    private Map<String, Integer> additional;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User() {}

    public Map<String, Integer> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Integer> settings) {
        this.settings = settings;
    }

    public Map<String, Integer> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, Integer> additional) {
        this.additional = additional;
    }

    public String formatFIO() {
        return getLastName() + " " + getName() + " " + getSurName();
    }

    public static ObservableList<User> searchUserByName(String text, Integer role) {
//        text = "%" + text.replace(" ", "%") + "%";
        var words = text.split(" ");
        ArrayList<org.hibernate.type.Type> types = new ArrayList<>();
        StringBuilder QUERY = new StringBuilder("FROM User WHERE role = ").append(role).append(" AND ");
        //lastName  || ' ' || name || ' ' || surName";

        Session session = HibernateUtil.getSessionFactory().openSession();
        //var q = session.createQuery(QUERY.toString());
        for (int i = 0; i < words.length; ++i) {
            QUERY.append("lastName  || ' ' || name || ' ' || surName LIKE ?").append(Integer.toString(i)).append(" AND ");
            types.add(StringNVarcharType.INSTANCE);
        }
        ObservableList<User> users = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
//            String QUERY = "FROM User WHERE lastName  || ' ' || name || ' ' || surName LIKE :search";

            // here get object
            var q = session.createQuery(QUERY.toString().substring(0, QUERY.length() - 4));
            for (int i = 0; i < words.length; ++i) {
                q.setParameter(i, "%" + words[i] + "%");
            }
            users = FXCollections.observableArrayList(q.getResultList());
            //users = FXCollections.observableArrayList(session.createQuery(QUERY.toString().substring(0, QUERY.length() - 4)).setParameters(words, types.toArray(org.hibernate.type.Type[]::new)).getResultList());
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
        return users;
    }

//User(int id, int role, String name, String lastName, String surName, String email);
//    static User get_user(int id) {
//        return User();
//    };

}
