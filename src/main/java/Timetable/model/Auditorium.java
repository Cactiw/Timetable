package Timetable.model;


import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

@Entity
@Component
public class Auditorium {
    // TODO set @NonNull or @Nullable to each variable and it's getter according to it's properties in the table
    // TODO alter all setters to return new object instead of mutating existing one see BorderProperties for example

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

    @ManyToMany
    private Set<AuditoriumProperty> properties;

    @OneToMany
    @JoinColumn(name = "auditorium_id")
    private Set<Pair> pairs;

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

    public Set<AuditoriumProperty> getProperties() {
        return properties;
    }

    public void setProperties(Set<AuditoriumProperty> properties) {
        this.properties = properties;
    }

    public Set<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(Set<Pair> pairs) {
        this.pairs = pairs;
    }
}
