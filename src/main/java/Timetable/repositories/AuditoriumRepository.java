package Timetable.repositories;

import Timetable.model.Auditorium;
import Timetable.model.AuditoriumProperty;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface AuditoriumRepository extends JpaRepository<Auditorium, Integer> {
    @NonNull
    @Override
    List<Auditorium> findAll();

    @NonNull
    @Override
    <S extends Auditorium> List<S> findAll(@NonNull final Example<S> example);

    @NonNull
    Auditorium getByName(@NonNull final String name); //Never used

    @NonNull
    List<Auditorium> findByNameIgnoreCaseContaining(@NonNull final String text);

    @Query("select a from Auditorium a left outer join Pair p on a = p.auditorium where p.id is null or " +
            "(p.beginTime > ?2 or p.endTime < ?1) order by a.maxStudents desc")
    @NonNull
    List<Auditorium> findAvailableAuditorium(@NonNull final LocalDateTime beginTime,
                                             @NonNull final LocalDateTime endTime);

    @Query(value = "select a from Auditorium a join a.properties p where " +
            "upper(a.name) like upper(concat('%', ?1, '%')) and a.maxStudents >= ?2 and " +
            "p in ?3 group by a having count(p) >= (" +
            "select count(p2) from AuditoriumProperty p2 where p2 in ?3)")
    List<Auditorium> findByNameIgnoreCaseContainingAndMaxStudentsGreaterThanEqualAndPropertiesIn
            (
            @NonNull final String name,
            @NonNull final int maxStudents,
            @NonNull final Set<AuditoriumProperty> properties);

    List<Auditorium> findByNameIgnoreCaseContainingAndMaxStudentsGreaterThanEqual(
            @NonNull final String name,
            @NonNull final int maxStudents
    );
}
