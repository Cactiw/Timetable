package Timetable.repositories;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import Timetable.model.PeopleUnion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface PairRepository extends JpaRepository<Pair, Integer> {
    @NonNull
    List<Pair> getAllByAuditoriumEquals(@NonNull final Auditorium auditorium);

    @NonNull
    List<Pair> getAllByAuditoriumEqualsAndDayOfTheWeekEqualsAndRepeatabilityGreaterThan(
            @NonNull final Auditorium auditorium,
            @NonNull final int dayOfWeek,
            @NonNull final int repeatability);

    @Query("select p from Pair p where p.auditorium = ?1 and p.dayOfTheWeek = ?2 and " +
            "(p.clearBeginTime <= ?4 and p.clearEndTime >= ?3)")
    @NonNull
    List<Pair> getAllAuditoriumConflicts(@NonNull final Auditorium auditorium,
                                          final int dayOfWeek,
                                         @NonNull final LocalTime beginTime,
                                         @NonNull final LocalTime endTime);

    @Query("select p from Pair p where p.group = ?1 and p.dayOfTheWeek = ?2 and " +
            "(p.clearBeginTime <= ?4 and p.clearEndTime >= ?3)")
    @NonNull
    List<Pair> getAllGroupConflicts(@NonNull final PeopleUnion peopleUnion,
                                    final int dayOfWeek,
                                    @NonNull final LocalTime beginTime,
                                    @NonNull final LocalTime endTime);

    @Query("select p from Pair p where p.repeatability > 0 and p.group in ?1 and p.pairToChange is null order by p.dayOfTheWeek asc, " +
            "p.clearEndTime asc")
    @NonNull
    List<Pair> getGroupsDefaultWeek(@NonNull final List<PeopleUnion> peopleUnions);

    // No use
    @NonNull
    List<Pair> getAllByGroupInAndRepeatabilityEqualsOrderByEndTimeAsc(@NonNull final List<PeopleUnion> peopleUnions, int repeatability);

    @NonNull
    List<Pair> getAllByRepeatabilityGreaterThan(final int repeatability);
    @NonNull
    List<Pair> getAllByRepeatabilityGreaterThanAndTeacherIdEquals(final int repeatability, final int teacherId);
    @NonNull
    List<Pair> getAllByGroupEquals(@NonNull final PeopleUnion peopleUnion);    // Never used
    @NonNull
    List<Pair> getAllByGroupEqualsAndRepeatabilityEquals(@NonNull final PeopleUnion peopleUnion, final int repeatability);
}
