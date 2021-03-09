package Timetable.model;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
@Entity
@Table
public class Pair {
    // TODO set @NonNull or @Nullable to each variable and it's getter according to it's properties in the table
    // TODO alter all setters to return new object instead of mutating existing one see BorderProperties for example

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

    @Formula(value="to_char(end_time, 'ID')::integer")
    private Integer dayOfTheWeek;

    @Formula(value="end_time::time")
    private LocalTime clearEndTIme;

    @Column
    @ColumnDefault("0")
    private Integer repeatability;  // 0 - нет, 1 - каждую неделю, so on

//    @Column
//    @Nullable
//    private Integer pairChangedId;

    @ManyToOne
    @JoinColumn(name = "pair_to_change_id")
    private Pair pairToChange;

    @OneToMany(mappedBy = "pairToChange")
    Set<Pair> newPairs;

    @Column
    private Boolean isCanceled;


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

    public Set<Pair> getNewPairs() {
        return newPairs;
    }

    public void setNewPairs(Set<Pair> newPairs) {
        this.newPairs = newPairs;
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

    public Boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        String pairTimePattern = "HH.mm";
        return DateTimeFormatter.ofPattern(pairTimePattern);
    }

    public String formatPair() {
        return this.getSubject() + "\n" + this.getTeacher().formatShortFIO() + "       " +  this.getAuditorium().getName();
    }

    public String formatStreamPair() {
        return this.getSubject() + "\n" + this.getTeacher().formatFIO() + "       " +  this.getAuditorium().getName();
    }

    public String formatPairTime() {
        var pairTimeFormatter = getDateTimeFormatter();
        return this.getBeginTime().format(pairTimeFormatter) + " - " + this.getEndTime().format(pairTimeFormatter);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Pair)) {
            return false;
        }
        Pair other = (Pair) obj;
        return this.getId().equals(other.getId());
    }
}
