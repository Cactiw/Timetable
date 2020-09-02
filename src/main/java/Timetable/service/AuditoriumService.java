package Timetable.service;

import Timetable.model.Auditorium;
import Timetable.repositories.AuditoriumRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriumService {
    @NonNull
    private final AuditoriumRepository auditoriumRepository;

    @Autowired
    public AuditoriumService(@NonNull final AuditoriumRepository auditoriumRepository) {
        this.auditoriumRepository = auditoriumRepository;
    }

    @NonNull
    public Auditorium save(@NonNull final Auditorium auditorium) {
        return auditoriumRepository.save(auditorium);
    }

    @NonNull
    public ObservableList<Auditorium> getAuditoriums() {
        return FXCollections.observableArrayList(auditoriumRepository.findAll());
    }

    @NonNull
    public ObservableList<Auditorium> searchAuditoriums(@NonNull final String text) {
        return FXCollections.observableArrayList(auditoriumRepository.findByNameIgnoreCaseContaining(text));
    }

    @NonNull
    public ObservableList<Auditorium> getAvailableAuditoriums(@NonNull final LocalDateTime beginTime,
                                                              @NonNull final LocalDateTime endTime) {
        return FXCollections.observableArrayList(auditoriumRepository.findAvailableAuditorium(beginTime, endTime));
    }

    @Nullable
    public Auditorium getAuditoriumByName(@NonNull final String name) {
        final ObservableList<Auditorium> auditoriums = searchAuditoriums(name);
        if (auditoriums.isEmpty()) {
            return null;
        } else {
            return auditoriums.get(0);
        }
    }
}
