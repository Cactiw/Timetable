package Timetable.repositories;

import Timetable.model.PeopleUnion;
import Timetable.model.PeopleUnionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeopleUnionRepository extends JpaRepository<PeopleUnion, Integer> {
    @NonNull
    List<PeopleUnion> findAll();
    @NonNull
    List<PeopleUnion> findAllByTypeEquals(@NonNull final PeopleUnionType type);
    @NonNull
    PeopleUnion getByNameEquals(@NonNull final String name);
    @NonNull
    List<PeopleUnion> findByNameIgnoreCaseContaining(@NonNull final String name);
}
