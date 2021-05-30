package Timetable.model;

import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@Entity
@Table
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    @ManyToOne
    private Pair requestPair;

    @ManyToOne
    @NonNull
    private Pair changePair;

    @ManyToOne
    @Nullable
    private Auditorium auditorium;

    @Column
    private LocalDate changeDate;

    @Column
    private LocalDateTime newBeginTime;

    @Formula(value="new_begin_time::time")
    private LocalTime clearNewBeginTime;

    @Column
    private LocalDateTime newEndTime;

    @Formula(value="new_end_time::time")
    private LocalTime clearNewEndTime;

    @Column(columnDefinition = "default false")
    private Boolean processed;

    @CreatedDate
    @Column
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime processedAt;

    public Integer getId() {
        return id;
    }

    @NonNull
    public Pair getRequestPair() {
        return requestPair;
    }

    public void setRequestPair(@NonNull Pair requestPair) {
        this.requestPair = requestPair;
    }

    @Nullable
    public Auditorium getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(@Nullable Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    public LocalDateTime getNewBeginTime() {
        return newBeginTime;
    }

    public void setNewBeginTime(LocalDateTime newBeginTime) {
        this.newBeginTime = newBeginTime;
    }

    public LocalDateTime getNewEndTime() {
        return newEndTime;
    }

    public void setNewEndTime(LocalDateTime newEndTime) {
        this.newEndTime = newEndTime;
    }

    public LocalDate getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDate changeDate) {
        this.changeDate = changeDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public Pair getChangePair() {
        return changePair;
    }

    public void setChangePair(@NonNull Pair changePair) {
        this.changePair = changePair;
    }

    public LocalTime getClearNewBeginTime() {
        return clearNewBeginTime;
    }

    public LocalTime getClearNewEndTime() {
        return clearNewEndTime;
    }

    public Boolean getProcessed() {
        return processed;
    }
}