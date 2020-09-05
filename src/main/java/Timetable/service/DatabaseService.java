package Timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {
    @Autowired
    PeopleUnionTypeService peopleUnionTypeService;
    @Autowired
    AuditoriumPropertyService auditoriumPropertyService;

    public void appInit() {
        peopleUnionTypeService.createListOfDefaultTypes();
        auditoriumPropertyService.createAll();
    }
}
