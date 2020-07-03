package Timetable.repositories;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import Timetable.model.PeopleUnion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PairRepository extends JpaRepository<Pair, Integer> {
    List<Pair> getAllByAuditoriumEquals(Auditorium auditorium);

    @Query("select p from Pair p where p.auditorium = ?1 and p.dayOfTheWeek = ?2 and " +
            "(p.clearBeginTIme <= ?4 and p.clearEndTIme >= ?3)")
    List<Pair> getAllAuditoriumConflicts(Auditorium auditorium, Integer dayOfWeek,
                                         LocalTime beginTime, LocalTime endTime);

    @Query("select p from Pair p where p.group = ?1 and p.dayOfTheWeek = ?2 and " +
            "(p.clearBeginTIme <= ?4 and p.clearEndTIme >= ?3)")
    List<Pair> getAllGroupConflicts(PeopleUnion peopleUnion, Integer dayOfWeek,
                                         LocalTime beginTime, LocalTime endTime);

    @Query("select p from Pair p where p.repeatability > 0 and p.group in ?1 order by p.dayOfTheWeek asc, " +
            "p.clearEndTIme asc")
    List<Pair> getGroupsDefaultWeek(List<PeopleUnion> peopleUnions);

    List<Pair> getAllByGroupInAndRepeatabilityEqualsOrderByEndTimeAsc(List<PeopleUnion> peopleUnions, int repeatability);

    List<Pair> getAllByRepeatabilityGreaterThan(int repeatability);
    List<Pair> getAllByRepeatabilityGreaterThanAndTeacherIdEquals(int repeatability, int teacherId);
    List<Pair> getAllByGroupEquals(PeopleUnion peopleUnion);
    List<Pair> getAllByGroupEqualsAndRepeatabilityEquals(PeopleUnion peopleUnion, int repeatability);
}
