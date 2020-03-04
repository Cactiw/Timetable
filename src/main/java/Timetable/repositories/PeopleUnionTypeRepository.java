package Timetable.repositories;

import Timetable.model.PeopleUnionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleUnionTypeRepository extends JpaRepository<PeopleUnionType, Integer> {
    PeopleUnionType getByName(String name);
}
