package Timetable.service;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import Timetable.model.PeopleUnion;
import Timetable.model.User;
import Timetable.repositories.PairRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PairService {
    @NonNull
    private final PairRepository pairRepository;
    @NonNull
    private final PeopleUnionService peopleUnionService;
    @NonNull
    private final PeopleUnionTypeService peopleUnionTypeService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final AuditoriumService auditoriumService;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    public PairService(@NonNull final PairRepository pairRepository,
                       @NonNull final PeopleUnionService peopleUnionService,
                       @NonNull final PeopleUnionTypeService peopleUnionTypeService,
                       @NonNull final UserService userService,
                       @NonNull final AuditoriumService auditoriumService
                       ) {
        this.pairRepository = pairRepository;
        this.peopleUnionService = peopleUnionService;
        this.peopleUnionTypeService = peopleUnionTypeService;
        this.userService = userService;
        this.auditoriumService = auditoriumService;
    }

    @NonNull
    public Pair saveFlush(@NonNull final Pair pair) {
        pairRepository.save(pair);
        pairRepository.flush();
        em.refresh(pair);
        return pair;
    }

    @NonNull
    public Pair save(@NonNull final Pair pair) {
        return pairRepository.save(pair);
    }

    public void delete(@NonNull final Pair pair) {
        pairRepository.delete(pair);
    }

    @NonNull
    public ObservableList<Pair> getDefaultWeek() {
        return FXCollections.observableArrayList(pairRepository.getAllByRepeatabilityGreaterThan(0));
    }

    @NonNull
    public ObservableList<Pair> adaptWeekToCurrent(@NonNull ObservableList<Pair> pairs,
                                                   @NonNull final LocalDate weekStart) {
        var week = new ArrayList<Pair>();
        for (int i = 0; i < pairs.size(); ++i) {
            var pair = pairs.get(i);
            var changes = pair.getNewPairs();
            if (changes.size() > 0) {
                var suitableChanges = changes.stream().filter(p ->
                    DateService.isBetween(
                            Period.between(weekStart, p.getBeginTime().toLocalDate()).getDays(), 0, 6
                    )).collect(Collectors.toList());
                if (suitableChanges.size() > 0) {
                    week.add(suitableChanges.get(0));
                } else {
                    week.add(pair);
                }
            } else {
                week.add(pair);
            }
        }
        return FXCollections.observableArrayList(week);
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

    @NonNull
    public ObservableList<ObservableList<Pair>> getCurrentWeekForStream(@NonNull final List<PeopleUnion> peopleUnions,
                                                                        @NonNull final LocalDate weekStart) {
        var pairsByDays = getDefaultWeekForStream(peopleUnions);
        return FXCollections.observableArrayList(
                pairsByDays.stream().map(list -> adaptWeekToCurrent(list, weekStart)).collect(Collectors.toList())
        );
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
        return FXCollections.observableArrayList(pairRepository.getAllByAuditoriumEqualsAndDayOfTheWeekEqualsAndRepeatabilityGreaterThan(
                auditorium, dayOfWeek, 0));
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

    @NonNull
    public List<String> checkPairConflicts(
            @NonNull Pair pair
    ) {
        return checkPairConflicts(
                pair.getTeacher(), pair.getGroup(), pair.getAuditorium(), pair.getBeginTime(), pair.getEndTime(), pair
        );
    }

    @NonNull
    public List<String> checkPairConflicts(
            @NonNull User teacher,
            @NonNull PeopleUnion group,
            @Nullable Auditorium auditorium,
            @NonNull LocalDateTime beginTime,
            @NonNull LocalDateTime endTime,

            @Nullable Pair pairFrom
    ) {
        StringBuilder conflicts = new StringBuilder("");
        StringBuilder suggestions = new StringBuilder("");
        // Проверка на конфликты преподавателя
        final ObservableList<Pair> teacherPairs = getDefaultWeekForTeacher(teacher);
        for (final Pair pair : teacherPairs) {
            if (!(beginTime.compareTo(pair.getEndTime()) > 0 ||
                    endTime.compareTo(pair.getBeginTime()) < 0 || (pairFrom != null && pairFrom.equals(pair)))) {
                // Пересекаются, алёрт
                conflicts.append("Преподаватель в это время занят:\n" + pair.getSubject() + " " +
                        pair.getAuditoriumName() + " " + pair.getBeginTime().toLocalTime().toString() + " - " +
                        pair.getEndTime().toLocalTime().toString() + "\n");
            }
        }
        // Проверка на конфликты аудитории
        if (auditorium != null) {
            final ObservableList<Pair> auditoriumConflictPairs = getAuditoriumConflictPairs(auditorium,
                    beginTime.getDayOfWeek().getValue(),
                    beginTime.toLocalTime(), endTime.toLocalTime());
            for (final Pair pair : auditoriumConflictPairs) {
                if (pairFrom != null && pairFrom.equals(pair)) { // Пара совпала с текущей - не конфликт
                    continue;
                }
                final ObservableList<Auditorium> availableAuditoriums = auditoriumService.getAvailableAuditoriums(beginTime, endTime);
                final String suggestion = !availableAuditoriums.isEmpty() ? "Подходящая аудитория:\n" +
                        availableAuditoriums.get(0).getName() + ", вместимость: " +
                        availableAuditoriums.get(0).getMaxStudents() : "Подходящих аудиторий не найдено.";
                conflicts.append("Аудитория в это время занята:\n" + pair.getSubject() + " " +
                        pair.getAuditoriumName() + " " + pair.getBeginTime().toLocalTime().toString() + " - " +
                        pair.getEndTime().toLocalTime().toString() + "\n");
                suggestions.append(suggestion);
            }
        }

        // Проверка на конфликты групп
        final ObservableList<Pair> pairs = getGroupConflictPairs(group, beginTime.getDayOfWeek().getValue(),
                beginTime.toLocalTime(), endTime.toLocalTime());
        for (final Pair pair : pairs) {
            if (pairFrom != null && pairFrom.equals(pair)) { // Пара совпала с текущей - не конфликт
                continue;
            }
            conflicts.append("Группа в это время занята:\n" + pair.getSubject() + " " +
                    pair.getAuditoriumName() + " " + pair.getBeginTime().toLocalTime().toString() + " - " +
                    pair.getEndTime().toLocalTime().toString() + "\n");
        }
        return Arrays.asList(
                conflicts.toString(), suggestions.toString()
        );
    }


    public void importTimetable(
            @NonNull JSONObject timetable
        ) {
        StringBuilder conflicts = new StringBuilder();
        timetable.keySet().forEach(groupName ->
        {
            var group = peopleUnionService.getByName(groupName);
            if (group == null) {
                final var courseName = groupName.substring(0, 1) + " курс";
                var course = peopleUnionService.getByName(courseName);
                PeopleUnion stream;
                if (course == null) {
                    course = new PeopleUnion();
                    course.setName(courseName);
                    course.setType(peopleUnionTypeService.getByName("Курс"));

                    peopleUnionService.save(course);

                    stream = new PeopleUnion();
                    stream.setName(courseName + "1 поток");
                    stream.setParent(course);
                    peopleUnionService.save(stream);
                } else {
                    stream = course.getChildrenUnions().get(0);
                }
                group = new PeopleUnion();
                group.setName(groupName);
                group.setType(peopleUnionTypeService.getByName("Группа"));
                group.setParent(stream);
                peopleUnionService.save(group);
            }
            System.out.println("Group: " + group.getId().toString() + " " + group.getName() + " " + group.getParent().getName());

            JSONArray pairs = (JSONArray)timetable.get(groupName);
            LocalDate monday = DateService.getFirstDayOfWeek(LocalDate.now());
            for (var pairDataRaw: pairs) {
                System.out.println(pairDataRaw.toString());
                var pairData = (JSONObject)pairDataRaw;
                int dayOfWeek = pairData.getInt("dayOfWeek");
                Boolean merged = pairData.getBoolean("merged");
                String teacherInitials = pairData.getString("teacher");
                String subject = pairData.getString("subject");
                var auditoriumNameObject = pairData.get("pair_auditorium");
                if (JSONObject.NULL.equals(auditoriumNameObject)) {
                    break;
                }
                String auditoriumName = (String) auditoriumNameObject;
                String time = pairData.getString("time");

                User teacher = userService.searchCreateParsedTeacher(teacherInitials);
                if (teacher.getJustCreated()) {
                    conflicts.append("Преподаватель был создан: ").append(teacher.formatFIO());
                    teacher.setJustCreated(false);
                }

                Auditorium auditorium = auditoriumService.searchCreateAuditorium(auditoriumName);

                var splitTime = time.split(" - ");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m");
                LocalTime beginTime = LocalTime.parse(splitTime[0], formatter);
                LocalTime endTime = LocalTime.parse(splitTime[1], formatter);

                LocalDateTime begin = LocalDateTime.of(monday.plusDays(dayOfWeek), beginTime);
                LocalDateTime end = LocalDateTime.of(monday.plusDays(dayOfWeek), endTime);

                var pair = new Pair();
                pair.setSubject(subject);
                pair.setTeacher(teacher);
                pair.setGroup(merged ? group.getParent() : group);
                pair.setAuditorium(auditorium);

                pair.setBeginTime(begin);
                pair.setEndTime(end);
                pair.setRepeatability(Pair.EVERY_WEEK);

                var conflictsResult = checkPairConflicts(pair);
                if (conflictsResult.get(0).length() > 0) {
                    conflicts.append("Не удалось создать занятие " + pair.formatPair() + ": " + conflictsResult.get(0));
                } else {
                    save(pair);
                }

            }
            System.out.println("Processed " + groupName + " " + pairs.toString());
        });
    }
}
