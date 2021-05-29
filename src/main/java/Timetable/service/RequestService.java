package Timetable.service;

import Timetable.model.Request;
import Timetable.repositories.RequestRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class RequestService {
    @Autowired
    RequestRepository requestRepository;

    public ObservableList<Request> getLastRequests() {
        return FXCollections.observableArrayList(requestRepository.findAllByOrderByCreatedAtDesc());
    }
}
