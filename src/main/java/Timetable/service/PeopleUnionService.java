package Timetable.service;

import Timetable.model.PeopleUnion;
import Timetable.model.PeopleUnionType;
import Timetable.repositories.PeopleUnionRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PeopleUnionService {
    @NonNull
    private final PeopleUnionRepository peopleUnionRepository;

    @Autowired
    PeopleUnionService(@NonNull final PeopleUnionRepository peopleUnionRepository) {
        this.peopleUnionRepository = peopleUnionRepository;
    }

    @NonNull
    public ObservableList<PeopleUnion> findAll() {
        return FXCollections.observableArrayList(peopleUnionRepository.findAll());
    }

    @NonNull
    public ObservableList<PeopleUnion> findAllByTypeEquals(@NonNull final PeopleUnionType peopleUnionType) {
        return FXCollections.observableArrayList(peopleUnionRepository.findAllByTypeEquals(peopleUnionType));
    }

    @NonNull
    public ObservableList<PeopleUnion> searchPeopleUnions(@NonNull final String name) {
        return FXCollections.observableArrayList(peopleUnionRepository.findByNameIgnoreCaseContaining(name));
    }

    @Nullable
    public PeopleUnion getByName(@NonNull final String name) {
        return peopleUnionRepository.getByNameEquals(name);
    }

    @NonNull
    public PeopleUnion save(@NonNull final PeopleUnion peopleUnion) {
        return peopleUnionRepository.save(peopleUnion);
    }
}
