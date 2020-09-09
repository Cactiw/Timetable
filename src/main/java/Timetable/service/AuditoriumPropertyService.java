package Timetable.service;

import Timetable.model.AuditoriumProperty;
import Timetable.repositories.AuditoriumPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AuditoriumPropertyService {
    @Autowired
    private AuditoriumPropertyRepository auditoriumPropertyRepository;

    private final List<String> defaultProperties = Arrays.asList("Проектор", "Компьютеры", "Розетки");

    public void createAll() {
        for (String propertyName: defaultProperties) {
            AuditoriumProperty auditoriumProperty = auditoriumPropertyRepository.getByNameEquals(propertyName);
            if (auditoriumProperty == null) {
                auditoriumProperty = new AuditoriumProperty();
                auditoriumProperty.setName(propertyName);
                save(auditoriumProperty);
            }
        }
    }

    @NonNull public List<AuditoriumProperty> findAll() {
        return auditoriumPropertyRepository.findAll();
    }

    @NonNull public AuditoriumProperty save(@NonNull final AuditoriumProperty auditoriumProperty) {
        return auditoriumPropertyRepository.save(auditoriumProperty);
    }
}
