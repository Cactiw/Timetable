package Timetable.repositories;

import Timetable.model.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PairRepository extends JpaRepository<Pair, Integer> {
    List<Pair> getAllByRepeatabilityGreaterThan(int repeatability);
    List<Pair> getAllByRepeatabilityGreaterThanAndTeacherIdEquals(int repeatability, int teacherId);

}
