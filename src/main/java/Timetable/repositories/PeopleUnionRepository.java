package Timetable.repositories;

import Timetable.model.PeopleUnion;
import Timetable.model.PeopleUnionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeopleUnionRepository extends JpaRepository<PeopleUnion, Integer> {
    public List<PeopleUnion> findAll();
    public List<PeopleUnion> findAllByTypeEquals(PeopleUnionType type);
    public PeopleUnion getByNameEquals(String name);
    public List<PeopleUnion> findByNameIgnoreCaseContaining(String name);
}
