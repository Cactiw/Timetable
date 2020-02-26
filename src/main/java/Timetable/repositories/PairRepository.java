package Timetable.repositories;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PairRepository extends JpaRepository<Pair, Integer> {
    List<Pair> getAllByAuditoriumEquals(Auditorium auditorium);
    @Query("select p from Pair p where p.auditorium = ?1 and (p.beginTime < ?3 and p.endTime > ?2)")
    List<Pair> getAllAuditoriumConflicts(Auditorium auditorium, LocalDateTime beginTime, LocalDateTime endTime);
    List<Pair> getAllByRepeatabilityGreaterThan(int repeatability);
    List<Pair> getAllByRepeatabilityGreaterThanAndTeacherIdEquals(int repeatability, int teacherId);
}
