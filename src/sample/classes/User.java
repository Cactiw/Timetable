package sample.classes;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Map;

@Entity
public class User {
    @Id
    @GeneratedValue
    int id;
    int role;  // 0 - диспетчер, 1 - преподаватель, 2 - студент
    String name;
    String last_name;
    String surname;
    String email;

    @Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb")
    Map<String, Integer> settings;

    @Type( type = "jsonb-node" )
    @Column(columnDefinition = "jsonb")
    Map<String, Integer> additional;

    //User(int id, int role, String name, String last_name, String surname, String email);
//    static User get_user(int id) {
//        return User();
//    };

}
