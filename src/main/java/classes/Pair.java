package classes;

import com.sun.istack.NotNull;
import javafx.scene.control.DatePicker;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Pair {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @Column
    @NotNull
    private String subject;

    @Column
    private Integer teacherId;

    @Column
    @NotNull
    private Integer auditoriumId;

    @Column
    @NotNull
    private LocalDate beginDate;

    @Column
    @NotNull
    private LocalTime beginTime;

    @Column
    @NotNull
    private LocalTime endTime;

    @Column
    @NotNull
    @ColumnDefault("0")
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

    public LocalDate getBeginDate() { return beginDate; }

    public void setBeginDate(LocalDate beginDate) { this.beginDate = beginDate; }

    public LocalTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime duration) {
        this.endTime = duration;
    }

    public Integer getRepeatability() {
        return repeatability;
    }

    public void setRepeatability(Integer repeation) {
        this.repeatability = repeation;
    }
}
