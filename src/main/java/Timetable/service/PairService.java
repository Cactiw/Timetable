package Timetable.service;

import Timetable.model.Pair;
import Timetable.model.User;
import Timetable.repositories.PairRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PairService {
    private final PairRepository pairRepository;

    @Autowired
    public PairService(PairRepository pairRepository) {
        this.pairRepository = pairRepository;
    }

    public Pair save(Pair pair) {
        return pairRepository.save(pair);
    }

    public ObservableList<Pair> getDefaultWeek() {
        ObservableList<Pair> pairs = FXCollections.observableArrayList(pairRepository.getAllByRepeatabilityGreaterThan(0));
        return pairs;
    }

    public ObservableList<Pair> getDefaultWeekForTeacher(User teacher){
        ObservableList<Pair> pairs = FXCollections.observableArrayList(pairRepository.getAllByRepeatabilityGreaterThanAndTeacherIdEquals(0, teacher.getId()));
        return pairs;
    }
}
