package classes;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
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
    private Integer id;

    @Column
    private Integer role;  // 0 - диспетчер, 1 - преподаватель, 2 - студент

    @Column
    private String name;

    @Column
    private String last_name;

    @Column
    private String surname;

    @Column
    private String email;

//    @Type( type = "json" )
//    @Column( columnDefinition = "json" )
//    private Map<String, Integer> settings;

//    @Type( type = "json" )
//    @Column( columnDefinition = "json" )
//    private Map<String, Integer> additional;

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

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User() {}

//    public Map<String, Integer> getSettings() {
//        return settings;
//    }
//
//    public void setSettings(Map<String, Integer> settings) {
//        this.settings = settings;
//    }
//
//    public Map<String, Integer> getAdditional() {
//        return additional;
//    }
//
//    public void setAdditional(Map<String, Integer> additional) {
//        this.additional = additional;
//    }

//User(int id, int role, String name, String last_name, String surname, String email);
//    static User get_user(int id) {
//        return User();
//    };

}
