package Timetable.model;

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
@Table(name="users")
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class),
        @TypeDef(name = "json-node", typeClass = JsonNodeStringType.class),
})
public class User {
    // TODO set @NonNull or @Nullable to each variable and it's getter according to it's properties in the table
    // TODO alter all setters to return new object instead of mutating existing one see BorderProperties for example

    public static final int OperatorRole = 0;
    public static final int TeacherRole = 1;
    public static final int StudentRole = 2;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private Integer role;  // 0 - диспетчер, 1 - преподаватель, 2 - студент

    @Column
    private String name;

    @Column
    private String lastName;

    @Column
    private String surName;

    @Column
    private String email;

    @ManyToOne
    private PeopleUnion group;

    @Type( type = "json" )
    @Column( columnDefinition = "json" )
    private Map<String, Integer> settings;

    @Type( type = "json" )
    @Column( columnDefinition = "json" )
    private Map<String, Integer> additional;

    // For server use
    @Column(length = 64)
    private String password;

    // Always null
    @Column(length = 64)
    private String clear_password;

    public Boolean getJustCreated() {
        return justCreated;
    }

    public void setJustCreated(Boolean justCreated) {
        this.justCreated = justCreated;
    }

    private Boolean justCreated = false;

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

    public PeopleUnion getGroup() {
        return group;
    }

    public void setGroup(PeopleUnion group) {
        this.group = group;
    }

    public String formatFIO() {
        return getLastName() + " " + getName() + " " + getSurName();
    }

    public String formatShortFIO() {
        return getLastName() + " " + getName().substring(0, 1) + ". " + getSurName().substring(0, 1) + ".";
    }

}
