package Timetable.service;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import Timetable.model.PeopleUnion;
import Timetable.model.User;
import Timetable.repositories.PairRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

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

    public ObservableList<ObservableList<Pair>> getDefaultWeekByDays() {
        ObservableList<Pair> pairs = getDefaultWeek();
        return dividePairsByDaysOfWeek(pairs);
    }

    public ObservableList<ObservableList<Pair>> getDefaultWeekByPeopleUnionDividedByDays(PeopleUnion peopleUnion) {
        return dividePairsByDaysOfWeek(getDefaultWeekByPeopleUnion(peopleUnion));
    }

    public ObservableList<Pair> getDefaultWeekByPeopleUnion(PeopleUnion peopleUnion) {
        ObservableList<Pair> pairs = FXCollections.observableArrayList();
        PeopleUnion currentPeopleUnion = peopleUnion;
        while (currentPeopleUnion != null) {
            pairs.addAll(FXCollections.observableArrayList(pairRepository.getAllByGroupEqualsAndRepeatabilityEquals(
                    currentPeopleUnion, 1)));

            currentPeopleUnion = currentPeopleUnion.getParent();
        }
        return pairs;
    }

    public ObservableList<ObservableList<Pair>> getDefaultWeekForStream(List<PeopleUnion> peopleUnions) {
        return dividePairsByDaysOfWeek(FXCollections.observableArrayList(
                pairRepository.getGroupsDefaultWeek(peopleUnions)));
    }

    // Получает на вход список пар в неделю, возвращает список из семи списков пар - по одному на каждый день недели.
    private ObservableList<ObservableList<Pair>> dividePairsByDaysOfWeek(ObservableList<Pair> pairs) {
        if (pairs.isEmpty()) {
            return FXCollections.observableArrayList();
        }
//        var currentDay = pairs.get(0).getBeginTime().toLocalDate().with(DayOfWeek.MONDAY);
        var currentDay = 1;
        ObservableList<ObservableList<Pair>> returnList = FXCollections.observableArrayList();;
        for (int i = 0; i < 7; ++i) {
            final var finalCurrentDay = currentDay;
            returnList.add(pairs.filtered(e -> e.getDayOfTheWeek().equals(finalCurrentDay)).sorted(
                    Comparator.comparing(Pair::getClearBeginTIme)));
            currentDay += 1;
        }
        return returnList;
    }

    public ObservableList<Pair> getDefaultWeekForTeacher(User teacher){
        ObservableList<Pair> pairs = FXCollections.observableArrayList(pairRepository.getAllByRepeatabilityGreaterThanAndTeacherIdEquals(0, teacher.getId()));
        return pairs;
    }

    public ObservableList<Pair> getAuditoriumPairs(Auditorium auditorium) {
        return FXCollections.observableArrayList(pairRepository.getAllByAuditoriumEquals(auditorium));
    }

    public ObservableList<Pair> getAuditoriumConflictPairs(Auditorium auditorium, Integer dayOfWeek,
                                                           LocalTime beginTime, LocalTime endTime) {
        return FXCollections.observableArrayList(pairRepository.getAllAuditoriumConflicts(auditorium, dayOfWeek,
                beginTime, endTime));
    }
}
