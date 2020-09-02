package Timetable.repositories;

import Timetable.model.Auditorium;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

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
}
