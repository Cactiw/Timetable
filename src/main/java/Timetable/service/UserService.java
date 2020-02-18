package Timetable.service;

import Timetable.model.User;
import Timetable.repositories.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class UserService {
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public ObservableList<User> searchUserByName(String text, Integer role) {
        var words = text.split(" ");
        StringBuilder QUERY = new StringBuilder("FROM User WHERE role = ").append(role).append(" AND ");

        for (int i = 0; i < words.length; ++i) {
            QUERY.append("LOWER(lastName)  || ' ' || LOWER(name) || ' ' || LOWER(surName) LIKE ?").append(Integer.toString(i)).append(" AND ");
        }

        ObservableList<User> users = null;
        // here get object
        var q = entityManager.createQuery(QUERY.toString().substring(0, QUERY.length() - 4));
        for (int i = 0; i < words.length; ++i) {
            q.setParameter(i, "%" + words[i] + "%");
        }
        users = FXCollections.observableArrayList(q.getResultList());

        return users;
    }
}
