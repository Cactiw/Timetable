package Timetable.service;

import Timetable.model.PeopleUnionType;
import Timetable.repositories.PeopleUnionTypeRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
@Transactional
public class PeopleUnionTypeService {
    @NonNull
    private final PeopleUnionTypeRepository peopleUnionTypeRepository;

    @Autowired
    PeopleUnionTypeService(@NonNull final PeopleUnionTypeRepository peopleUnionTypeRepository) {
        this.peopleUnionTypeRepository = peopleUnionTypeRepository;
    }

    @NonNull
    public ObservableList<PeopleUnionType> findAll() {
        return FXCollections.observableArrayList(peopleUnionTypeRepository.findAll());
    }

    @NonNull
    public PeopleUnionType getByName(@NonNull final String name) {
        return peopleUnionTypeRepository.getByName(name);
    }

    @NonNull
    public PeopleUnionType checkOrCreateType(@NonNull final String name, @NonNull final PeopleUnionType parent) {
        final PeopleUnionType peopleUnionType = checkOrCreateType(name);
        peopleUnionType.setParent(parent);
        return peopleUnionType;
    }

    // consider replacing each call with getByName call
    @NonNull
    public PeopleUnionType checkOrCreateType(@NonNull final String name) {
        return getByName(name);
    }

    @NonNull
    public void createListOfDefaultTypes(@NonNull final ArrayList<String> types) {
        PeopleUnionType previousType = checkOrCreateType(types.get(0));
        for (var type : types.subList(1, types.size())) {
            previousType = checkOrCreateType(type, previousType);
        }
    }
}
