package Timetable.repositories;

import Timetable.model.Auditorium;
import javafx.collections.ObservableList;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface AuditoriumRepository extends JpaRepository<Auditorium, Integer> {
    @Override
    List<Auditorium> findAll();

    @Override
    <S extends Auditorium> List<S> findAll(Example<S> example);

    Auditorium getByName(String name);

    List<Auditorium> findByNameIgnoreCaseContaining(String text);

    @Query("select a from Auditorium a left outer join Pair p on a = p.auditorium where p.id is null or " +
            "(p.beginTime > ?2 or p.endTime < ?1) order by a.maxStudents desc")
    List<Auditorium> findAvailableAuditorium(LocalDateTime beginTime, LocalDateTime endTime);
}
