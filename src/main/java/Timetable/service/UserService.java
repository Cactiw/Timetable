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

    @NonNull User searchCreateParsedTeacher(@NonNull final String text) {
        ObservableList<User> searchResult;
        if (!text.contains(".")) {
            searchResult = searchUserByName(text, User.TeacherRole);
            if (searchResult.size() > 0) {
                return searchResult.get(0);
            }
            var user = new User();
            final List<String> names = Arrays.asList(text.split(" ").clone());
            user.setLastName(names.get(0));
            user.setName(names.get(1));
            user.setSurName(names.get(2));
            user.setJustCreated(true);
            save(user);
            return user;
        } else {
            final List<String> words = Arrays.asList(text.toLowerCase().split("\\.").clone());
            final List<String> words2 = Arrays.asList(words.get(0).split(" ").clone());
            String last_name = words2.get(0);
            String name_initial = words2.get(1);
            String surname_initial = words.get(1);

            searchResult = searchParsedTeacherByName(last_name, name_initial, surname_initial);
            if (searchResult.size() > 0) {
                return searchResult.get(0);
            }
            var user = new User();
            user.setLastName(last_name);
            user.setName(name_initial);
            user.setSurName(surname_initial);
            user.setJustCreated(true);
            save(user);
            return user;
        }

    }

    @NonNull
    public ObservableList<User> searchParsedTeacherByName(
            @NonNull final String last_name,
            @NonNull final String name_initial,
            @NonNull final String surname_initial
    ) {

        final StringBuilder QUERY = new StringBuilder("FROM User WHERE role = ?1").append(" AND ");

        QUERY.append("LOWER(lastName) LIKE ?2 AND LOWER(name) LIKE ?3 AND LOWER(surName) LIKE ?4");
        // here get object
        var q = entityManager.createQuery(QUERY.toString());
        q.setParameter(1, User.TeacherRole);
        q.setParameter(2, last_name);
        q.setParameter(3, name_initial + "%");
        q.setParameter(4, surname_initial + "%");

        return FXCollections.observableArrayList(q.getResultList());   // Look into this warning, this is a potential crash
    }
}
