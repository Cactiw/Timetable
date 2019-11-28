package classes;

import javax.persistence.*;

@Entity
public class Pair {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @Column
    private String subject;
    
    @Column
    private Integer teacherId;
    
    @Column
    private Integer auditoriumId;
    
    @Column
    private Integer beginTime;

    @Column
    private Integer duration;

    @Column
    private Integer repeatability;  // 0 - нет, 1 - каждую неделю, so on

    public Integer getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getAuditoriumId() {
        return auditoriumId;
    }

    public void setAuditoriumId(Integer auditoriumId) {
        this.auditoriumId = auditoriumId;
    }

    public Integer getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Integer beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getRepeatability() {
        return repeatability;
    }

    public void setRepeatability(Integer repeation) {
        this.repeatability = repeation;
    }
}
