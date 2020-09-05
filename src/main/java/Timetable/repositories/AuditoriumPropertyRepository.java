package Timetable.repositories;

import Timetable.model.Auditorium;
import Timetable.model.AuditoriumProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AuditoriumPropertyRepository extends JpaRepository<AuditoriumProperty, Integer> {
    @Nullable
    public AuditoriumProperty getByNameEquals(@NonNull String name);
}
