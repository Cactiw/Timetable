package Timetable.service;

import Timetable.model.PeopleUnion;
import Timetable.model.PeopleUnionType;
import Timetable.repositories.PeopleUnionRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PeopleUnionService {
    private final PeopleUnionRepository peopleUnionRepository;

    @Autowired
    PeopleUnionService(PeopleUnionRepository peopleUnionRepository) {
        this.peopleUnionRepository = peopleUnionRepository;
    }

    public ObservableList<PeopleUnion> findAll() {
        return FXCollections.observableArrayList(peopleUnionRepository.findAll());
    }

    public ObservableList<PeopleUnion> findAllByTypeEquals (PeopleUnionType peopleUnionType) {
        return FXCollections.observableArrayList(peopleUnionRepository.findAllByTypeEquals(peopleUnionType));
    }

    public PeopleUnion save(PeopleUnion peopleUnion) {
        return peopleUnionRepository.save(peopleUnion);
    }
}
