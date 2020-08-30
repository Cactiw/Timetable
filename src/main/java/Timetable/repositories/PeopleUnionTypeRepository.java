package Timetable.repositories;

import Timetable.model.PeopleUnionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeopleUnionTypeRepository extends JpaRepository<PeopleUnionType, Integer> {
    @NonNull
    PeopleUnionType getByName(@NonNull final String name);
    @NonNull
    List<PeopleUnionType> findAll();
}
