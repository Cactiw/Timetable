package Timetable.service;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import Timetable.model.PeopleUnion;
import Timetable.model.User;
import Timetable.repositories.PairRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class PairService {
    @NonNull
    private final PairRepository pairRepository;

    @Autowired
    public PairService(@NonNull final PairRepository pairRepository) {
        this.pairRepository = pairRepository;
    }

    @NonNull
    public Pair save(@NonNull final Pair pair) {
        return pairRepository.save(pair);
    }

    @NonNull
    public ObservableList<Pair> getDefaultWeek() {
        return FXCollections.observableArrayList(pairRepository.getAllByRepeatabilityGreaterThan(0));
    }

    // Never used
    @NonNull
    public ObservableList<ObservableList<Pair>> getDefaultWeekByDays() {
        return dividePairsByDaysOfWeek(getDefaultWeek());
    }

    @NonNull
    public ObservableList<ObservableList<Pair>> getDefaultWeekByPeopleUnionDividedByDays(@NonNull final PeopleUnion peopleUnion) {
        return dividePairsByDaysOfWeek(getDefaultWeekByPeopleUnion(peopleUnion));
    }

    @NonNull
    public ObservableList<Pair> getDefaultWeekByPeopleUnion(@NonNull final PeopleUnion peopleUnion) {
        final ObservableList<Pair> pairs = FXCollections.observableArrayList();
        PeopleUnion currentPeopleUnion = peopleUnion;
        while (currentPeopleUnion != null) {
            pairs.addAll(FXCollections.observableArrayList(pairRepository.getAllByGroupEqualsAndRepeatabilityEquals(
                    currentPeopleUnion, 1)));

            currentPeopleUnion = currentPeopleUnion.getParent();
        }
        return pairs;
    }

    @NonNull
    public ObservableList<ObservableList<Pair>> getDefaultWeekForStream(@NonNull final List<PeopleUnion> peopleUnions) {
        return dividePairsByDaysOfWeek(FXCollections.observableArrayList(
                pairRepository.getGroupsDefaultWeek(peopleUnions)));
    }

    // Получает на вход список пар в неделю, возвращает список из семи списков пар - по одному на каждый день недели.
    @NonNull
    private ObservableList<ObservableList<Pair>> dividePairsByDaysOfWeek(@NonNull final ObservableList<Pair> pairs) {
        if (pairs.isEmpty()) {
            return FXCollections.observableArrayList();
        }
//        var currentDay = pairs.get(0).getBeginTime().toLocalDate().with(DayOfWeek.MONDAY);
        var currentDay = 1;
        final ObservableList<ObservableList<Pair>> returnList = FXCollections.observableArrayList();
        for (int i = 0; i < 7; ++i) {
            final var finalCurrentDay = currentDay;
            returnList.add(pairs.filtered(e -> e.getDayOfTheWeek().equals(finalCurrentDay)).sorted(
                    Comparator.comparing(Pair::getClearBeginTIme)));
            currentDay += 1;
        }
        return returnList;
    }

    @NonNull
    public ObservableList<Pair> getDefaultWeekForTeacher(@NonNull final User teacher){
        return FXCollections.observableArrayList(
                pairRepository.getAllByRepeatabilityGreaterThanAndTeacherIdEquals(0, teacher.getId())
        );
    }

    public ObservableList<Pair> getGroupConflictPairs(@NonNull final PeopleUnion peopleUnion,
                                                      @NonNull final Integer dayOfWeek,
                                                      @NonNull final LocalTime beginTime,
                                                      @NonNull final LocalTime endTime) {
        final ObservableList<Pair> pairs = FXCollections.observableArrayList();
        PeopleUnion currentPeopleUnion = peopleUnion;
        while (currentPeopleUnion != null) {
            pairs.addAll(FXCollections.observableArrayList(pairRepository.getAllGroupConflicts(
                    currentPeopleUnion, dayOfWeek, beginTime, endTime)));
            currentPeopleUnion = currentPeopleUnion.getParent();
        }
        return pairs;
    }

    @NonNull
    public ObservableList<Pair> getAuditoriumPairs(@NonNull final Auditorium auditorium) {
        return FXCollections.observableArrayList(pairRepository.getAllByAuditoriumEquals(auditorium));
    }

    @NonNull
    public ObservableList<Pair> getAuditoriumPairsByDayOfWeek(@NonNull final Auditorium auditorium,
                                                              @NonNull final int dayOfWeek) {
        return FXCollections.observableArrayList(pairRepository.getAllByAuditoriumEqualsAndDayOfTheWeekEquals(
                auditorium, dayOfWeek));
    }

    @NonNull
    public ObservableList<Pair> getAuditoriumConflictPairs(@NonNull final Auditorium auditorium,
                                                           final int dayOfWeek,
                                                           @NonNull final LocalTime beginTime,
                                                           @NonNull final LocalTime endTime) {
        return FXCollections.observableArrayList(
                pairRepository.getAllAuditoriumConflicts(auditorium, dayOfWeek, beginTime, endTime)
        );
    }

    @NonNull
    public boolean checkConflict(@NonNull final Pair pair,
                                 @NonNull final int dayOfWeek,
                                 @NonNull final LocalTime beginTime,
                                 @NonNull final LocalTime endTime) {
        return pair.getDayOfTheWeek() == dayOfWeek && (
                pair.getClearBeginTIme().compareTo(endTime) <=0 && pair.getClearEndTIme().compareTo(beginTime) >= 0);
    }
}
