package Application.repositories;

import Application.classes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Component
@Transactional
public interface UserRepository extends JpaRepository <User, Integer> {
}
