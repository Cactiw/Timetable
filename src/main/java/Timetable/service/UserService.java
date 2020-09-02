package Timetable.service;

import Timetable.model.User;
import Timetable.repositories.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserService {
    @NonNull
    private final UserRepository userRepository;

    @NonNull
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserService(@NonNull final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    public User save(@NonNull final User user) {
        return userRepository.save(user);
    }


    @NonNull
    public ObservableList<User> searchUserByName(@NonNull final String text, @NonNull final Integer role) {
        final List<String> words = Arrays.asList(text.toLowerCase().split(" ").clone());
        final StringBuilder QUERY = new StringBuilder("FROM User WHERE role = ").append(role).append(" AND ");

        for (int i = 0; i < words.size(); ++i) {
            QUERY.append("LOWER(lastName)  || ' ' || LOWER(name) || ' ' || LOWER(surName) LIKE ?").append(i).append(" AND ");
        }
        // here get object
        var q = entityManager.createQuery(QUERY.substring(0, QUERY.length() - 4));
        for (int i = 0; i < words.size(); ++i) {
            q.setParameter(i, "%" + words.get(i) + "%");
        }

        return FXCollections.observableArrayList(q.getResultList());   // Look into this warning, this is a potential crash
    }
}
