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
import java.util.List;

@Service
@Transactional
public class PeopleUnionTypeService {
    private final ArrayList<String> DEFAULT_TYPES = new ArrayList<>(List.of("Курс", "Поток", "Группа"));

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

    public void createListOfDefaultTypes() {
        PeopleUnionType previousType = checkOrCreateType(DEFAULT_TYPES.get(0));
        for (var type : DEFAULT_TYPES.subList(1, DEFAULT_TYPES.size())) {
            previousType = checkOrCreateType(type, previousType);
        }
    }
}
