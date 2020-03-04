package Timetable.service;

import Timetable.model.PeopleUnion;
import Timetable.model.PeopleUnionType;
import Timetable.repositories.PeopleUnionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
@Transactional
public class PeopleUnionTypeService {
    private final PeopleUnionTypeRepository peopleUnionTypeRepository;

    @Autowired
    PeopleUnionTypeService(PeopleUnionTypeRepository peopleUnionTypeRepository) {
        this.peopleUnionTypeRepository = peopleUnionTypeRepository;
    }

    public PeopleUnionType getByName(String name) {
        return peopleUnionTypeRepository.getByName(name);
    }

    public PeopleUnionType checkOrCreateType(String name, PeopleUnionType parent) {
        var peopleUnionType = checkOrCreateType(name);
        peopleUnionType.setParent(parent);
        return peopleUnionType;
    }

    public PeopleUnionType checkOrCreateType(String name) {
        var peopleUnionType = getByName(name);
        if (peopleUnionType == null) {
            peopleUnionType = new PeopleUnionType();
            peopleUnionType.setName(name);
            peopleUnionTypeRepository.save(peopleUnionType);
        }
        return peopleUnionType;
    }

    public void createListOfDefaultTypes(ArrayList<String> types) {
        PeopleUnionType previousType = checkOrCreateType(types.get(0));
        for (var type : types) {
            previousType = checkOrCreateType(type, previousType);
        }
    }
}
