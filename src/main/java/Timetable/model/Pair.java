package Timetable.model;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    @ManyToOne
    private PeopleUnion group;

    @Column
    private LocalDateTime beginTime;

    @Formula(value="begin_time::time")
    private LocalTime clearBeginTIme;

    @Column
    private LocalDateTime endTime;

    @Formula(value="to_char(end_time, 'ID')")
    private Integer dayOfTheWeek;

    @Formula(value="end_time::time")
    private LocalTime clearEndTIme;

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

    public PeopleUnion getGroup() {
        return group;
    }

    public void setGroup(PeopleUnion group) {
        this.group = group;
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

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getRepeatability() {
        return repeatability;
    }

    public void setRepeatability(Integer repeation) {
        this.repeatability = repeation;
    }

    public LocalTime getClearBeginTIme() {
        return clearBeginTIme;
    }

    public void setClearBeginTIme(LocalTime clearBeginTIme) {
        this.clearBeginTIme = clearBeginTIme;
    }

    public Integer getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public LocalTime getClearEndTIme() {
        return clearEndTIme;
    }

    public void setClearEndTIme(LocalTime clearEndTIme) {
        this.clearEndTIme = clearEndTIme;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        String pairTimePattern = "HH.mm";
        return DateTimeFormatter.ofPattern(pairTimePattern);
    }

    public String formatPair() {
        return this.getSubject() + "\n" + this.getTeacher().formatShortFIO() + "       " +  this.getAuditorium().getName();
    }

    public String formatStreamPair() {
        return this.getSubject() + "  " +  this.getAuditorium().getName() + "\n" + this.getTeacher().formatFIO();
    }

    public String formatPairTime() {
        var pairTimeFormatter = getDateTimeFormatter();
        return this.getBeginTime().format(pairTimeFormatter) + " - " + this.getEndTime().format(pairTimeFormatter);
    }
}
