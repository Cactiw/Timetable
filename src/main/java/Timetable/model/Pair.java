package Timetable.model;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Entity
@Table
public class Pair {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @Column
    private String subject;

    @ManyToOne
    private User teacher;

    @ManyToOne
    private Auditorium auditorium;

    @Column
    private LocalDate beginDate;

    @Column
    private LocalTime beginTime;

    @Column
    private LocalTime endTime;

    @Column
    @ColumnDefault("0")
    private Integer repeatability;  // 0 - нет, 1 - каждую неделю, so on

//    @Column
//    @Nullable
//    private Integer pairChangedId;

    @OneToOne
    @JoinColumn(name = "pair_to_change_id")
    private Pair pairToChange;

    @OneToOne(mappedBy = "pairToChange")
    private Pair newPair;

    public Integer getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Auditorium getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    public Pair getPairToChange() {
        return pairToChange;
    }

    public void setPairToChange(Pair pairToChange) {
        this.pairToChange = pairToChange;
    }

    public Pair getNewPair() {
        return newPair;
    }

    public void setNewPair(Pair newPair) {
        this.newPair = newPair;
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
