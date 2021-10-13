package Timetable.model;

import Timetable.service.DateService;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
@Entity
@Table
public class Pair {
    // TODO set @NonNull or @Nullable to each variable and it's getter according to it's properties in the table
    // TODO alter all setters to return new object instead of mutating existing one see BorderProperties for example

    public static final int EVERY_WEEK = 1;
    public static final int NEWER = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @Column
    private String subject;

    @ManyToOne
    private User teacher;

    @ManyToOne
    @Nullable
    private Auditorium auditorium;

    @Column(columnDefinition = "boolean default false")
    private Boolean isOnline = false;

    @ManyToOne
    private PeopleUnion group;

    @Column
    private LocalDateTime beginTime;

    @Formula(value="begin_time::time")
    private LocalTime clearBeginTime;

    @Column
    private LocalDateTime endTime;

    @Formula(value="to_char(end_time, 'ID')::integer")
    private Integer dayOfTheWeek;

    @Formula(value="end_time::time")
    private LocalTime clearEndTime;

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

    @Column
    private LocalDate changeDate;


    public Integer getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Nullable
    public User getTeacher() {
        return teacher;
    }

    public String getTeacherFIO() {
        return this.getTeacher() != null ? this.getTeacher().formatFIO() : "";
    }

    public String getTeacherShortFIO() {
        return this.getTeacher() != null ? this.getTeacher().formatShortFIO() : "";
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    @Nullable
    public Auditorium getAuditorium() {
        return auditorium;
    }

    public String getAuditoriumName() {
        if (getOnline()) {
            return "Дист.";
        }
        return getAuditorium() != null ? getAuditorium().getName() : "";
    }

    public void setAuditorium(Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    @Nullable
    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(@Nullable Boolean online) {
        isOnline = online;
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

    public LocalTime getClearBeginTime() {
        return clearBeginTime != null ? clearBeginTime : getBeginTime().toLocalTime();
    }

    public void setClearBeginTime(LocalTime clearBeginTIme) {
        this.clearBeginTime = clearBeginTIme;
    }

    public Integer getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public LocalTime getClearEndTime() {
        return clearEndTime != null ? clearEndTime : getEndTime().toLocalTime();
    }

    public void setClearEndTime(LocalTime clearEndTIme) {
        this.clearEndTime = clearEndTIme;
    }

    public Boolean isCanceled() {
        return getCanceled() != null && getCanceled();
    }

    public Boolean getCanceled() {
        return isCanceled;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }

    public LocalDate getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        String pairTimePattern = "HH.mm";
        return DateTimeFormatter.ofPattern(pairTimePattern);
    }

    public String formatPair() {
        return this.getSubject() + "\n" + this.getTeacherShortFIO() + "       " +
                getAuditoriumName();
    }

    public String formatStreamPair() {
        return this.getSubject() + "\n" + this.getTeacherFIO() + "       " +
                getAuditoriumName();
    }

    public String formatPairTime() {
        var pairTimeFormatter = getDateTimeFormatter();
        return this.getBeginTime().format(pairTimeFormatter) + " - " + this.getEndTime().format(pairTimeFormatter);
    }

    public boolean isChangedThisWeek(LocalDate weekStart) {
        return this.getChangeDate() != null &&
                DateService.isBetween(Period.between(weekStart, this.getChangeDate()).getDays(), 0, 6);
    }

    public boolean isThisWeek(LocalDate weekStart) {
        return DateService.isBetween(Period.between(weekStart, this.getBeginTime().toLocalDate()).getDays(), 0, 6);
    }

    public boolean isChange() {
        return this.pairToChange != null;
    }

    public boolean isBeginTimeMinorDifference(@NonNull final Pair other) {
        System.out.println("Checking difference!");
        System.out.println(getSubject());
        System.out.println(getClearEndTime());
        System.out.println(other.getSubject());
        System.out.println(other.getClearEndTime());
        System.out.println(isBeginTimeMinorDifference(other.getClearEndTime()));
        System.out.println(getId());
        System.out.println(other.getId());
        System.out.println();
        return isBeginTimeMinorDifference(other.getClearEndTime());
    }
    public boolean isBeginTimeMinorDifference(@NonNull final LocalTime time) {
        return Math.abs(Duration.between(getClearEndTime(), time).toSeconds()) < 30 * 60;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Pair)) {
            return false;
        }
        Pair other = (Pair) obj;
        if (this.getId() == null || other.getId() == null) {
            return false;
        }
        return this.getId().equals(other.getId());
    }

    public Pair cancelPair(LocalDate weekStart) {
        var cancelPair = new Pair();
        cancelPair.setSubject(getSubject());
        cancelPair.setAuditorium(getAuditorium());
        cancelPair.setTeacher(getTeacher());
        cancelPair.setGroup(getGroup());
        cancelPair.setChangeDate(weekStart.plusDays(getDayOfTheWeek() - 1));
        var beginTime = getBeginTime().toLocalTime();
        var beginDateTime = LocalDateTime.of(weekStart.plusDays(getDayOfTheWeek() - 1), beginTime);
        var endTime = getEndTime().toLocalTime();
        var EndDateTime = LocalDateTime.of(weekStart.plusDays(getDayOfTheWeek() - 1), endTime);
        cancelPair.setBeginTime(beginDateTime);
        cancelPair.setEndTime(EndDateTime);
        cancelPair.setRepeatability(getRepeatability());
        cancelPair.setPairToChange(this);
        cancelPair.setCanceled(true);
        return cancelPair;
    }
}
