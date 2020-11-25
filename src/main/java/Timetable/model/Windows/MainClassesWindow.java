package Timetable.model.Windows;

import Timetable.model.Dialogs.ViewDialogs.ViewPairDialog;
import Timetable.model.Pair;
import Timetable.model.Parameters.StyleParameter;
import Timetable.model.PeopleUnion;
import Timetable.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

public class MainClassesWindow {

    private HBox classesSettings;
    private VBox classes;

    private GridPane classesPane;

    private ChoiceBox<PeopleUnion> courseSelect;
    private ChoiceBox<PeopleUnion> streamSelect;

    private PeopleUnionService peopleUnionService;
    private PeopleUnionTypeService peopleUnionTypeService;
    private PairService pairService;
    private ViewPairDialog viewPairDialog;

    private StackPane modes;

    public MainClassesWindow(@NonNull final StackPane modes,
                             @NonNull final PeopleUnionService peopleUnionService,
                             @NonNull final PeopleUnionTypeService peopleUnionTypeService,
                             @NonNull final PairService pairService,
                             @NonNull final ViewPairDialog viewPairDialog) {
        this.modes = modes;
        this.peopleUnionService = peopleUnionService;
        this.peopleUnionTypeService = peopleUnionTypeService;
        this.pairService = pairService;
        this.viewPairDialog = viewPairDialog;
    }


    public Pane initiateClassesWindow() {

        classes = new VBox();
        classes.toBack();
        classes.setStyle("-fx-background-color: white");

        final ObservableList<PeopleUnion> courses = peopleUnionService
                .findAllByTypeEquals(peopleUnionTypeService.checkOrCreateType("Курс"));
        final ObservableList<PeopleUnion> streams = peopleUnionService
                .findAllByTypeEquals(peopleUnionTypeService.checkOrCreateType("Поток"));

        classesSettings = new HBox();
        classesSettings.setPadding(new Insets(25, 10, 0, 10));
        classesSettings.setAlignment(Pos.CENTER_LEFT);

        classesSettings.spacingProperty().setValue(10);

        Label courseLabel = new Label("Курс:");
        Label streamLabel = new Label("Поток:");

        courseSelect = new ChoiceBox<>(courses);
        if (!courses.isEmpty()) {
            courseSelect.setValue(courses.get(0));
        }
        streamSelect = new ChoiceBox<>(streams);
        if (!streams.isEmpty()) {
            streamSelect.setValue(streams.get(0));
        }

        courseSelect.setOnAction(e -> {
//            streams = peopleUnionService.findAllByTypeEquals(courseSelect.getValue());
            final ObservableList<PeopleUnion> currentStreams = FXCollections
                    .observableArrayList(courseSelect.getValue().getChildrenUnions());
            streamSelect.setItems(currentStreams);
            if (!currentStreams.isEmpty()) {
                streamSelect.setValue(currentStreams.get(0));
            }
        });

        streamSelect.setOnAction(e -> updateClasses());

        classesSettings.getChildren().addAll(courseLabel, courseSelect, new Label("      "),
                streamLabel, streamSelect);

        classes.getChildren().add(classesSettings);

        addClassesWindow();

        ScrollPane classesScrollPane = new ScrollPane();
        classesScrollPane.setContent(classes);
        classesScrollPane.setFitToWidth(true);

        modes.getChildren().add(classesScrollPane);

        return classes;
    }

    public void updateClasses() {
        classes.getChildren().clear();
        classes.getChildren().add(classesSettings);
        addClassesWindow();
    }

    private void addClassesWindow() {
        /*
        Функция, создающее окно, вызывающееся по кнопке "Занятия"
         */

        if (classes != null && classesPane != null) {
            classes.getChildren().remove(classesPane);
        }

        classesPane = getClassesPane();
        classes.getChildren().add(classesPane);
        HBox.setHgrow(classesPane, Priority.ALWAYS);

    }

    @NonNull
    private GridPane getClassesPane() {

        classesPane = new GridPane();

        classesPane.getStyleClass().add("classes-grid");

        int mode = 0;

        // Always true, consider removing
        if (mode == 0) {
            final PeopleUnion fatherPeopleUnion = streamSelect.getValue();
            if (fatherPeopleUnion == null) {
                return classesPane;
            }
            final List<PeopleUnion> groups = fatherPeopleUnion.getChildrenUnions();
            final int groupsCount = groups.size();

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            classesPane.getColumnConstraints().add(columnConstraints);

            for (PeopleUnion ignored : groups) {
                classesPane.getColumnConstraints().add(columnConstraints);
            }

            final ObservableList<PeopleUnion> groupsTmp = FXCollections.observableArrayList(groups);
            groupsTmp.add(fatherPeopleUnion);

            GridPaneService.addToGridPane(classesPane, new Label(" "), 0, 0);
            final ObservableList<ObservableList<Pair>> week = pairService.getDefaultWeekForStream(groupsTmp);
            if (week.isEmpty()) {
                return classesPane;
            }
            int currentRow = 0;
            final StyleParameter labelStyle = new StyleParameter();
            labelStyle.setLabelStyle("white-text");
            for (int dayIndex = 0; dayIndex < DateService.daysOfWeek.size(); ++dayIndex) {
                final ObservableList<Pair> currentDayPairs = week.get(dayIndex);
                final String dayName = DateService.daysOfWeek.get(dayIndex);

                // Записываем имя дня
                GridPaneService.fillRowEmpty(classesPane, currentRow, groupsCount, new
                        StyleParameter("grey"));
                GridPaneService.addToGridPane(classesPane, new Label(dayName), 0, currentRow, labelStyle);
                writeAllGroups(classesPane, groups, currentRow, 1, labelStyle);
                currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);

                if (!currentDayPairs.isEmpty()) {
                    LocalTime currentTime = currentDayPairs.get(0).getClearEndTIme();
                    GridPaneService.addToGridPane(classesPane, new Label(currentDayPairs.get(0).formatPairTime()),
                            0, currentRow);
                    for (Pair pair : currentDayPairs) {
                        Label pairLabel = new Label(pair.formatPair());
                        if (pair.getClearEndTIme().compareTo(currentTime) > 0) {

                            System.out.println(Math.abs(Duration.between(pair.getClearEndTIme(), currentTime).toSeconds()));
                            System.out.println(pair.getSubject());
                            if (Math.abs(Duration.between(pair.getClearEndTIme(), currentTime).toSeconds()) < 30 * 60) {
                                // Незначительное различие по времени
                                pairLabel.setText(pair.getClearBeginTIme().toString() + " — " +
                                        pair.getClearEndTIme().toString() + " " +
                                        pairLabel.getText()
                                );
                            } else {
                                // Пара следующая по времени
                                currentTime = pair.getClearEndTIme();
                                currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
                                GridPaneService.addToGridPane(classesPane, new Label(pair.formatPairTime()), 0,
                                        currentRow);
                            }
                        }

                        pairLabel.setTextAlignment(TextAlignment.CENTER);
                        final StyleParameter style = new StyleParameter();
                        // No need to make it null, just initialize it before first read
                        final Pane pairPane;
                        if (pair.getGroup().equals(fatherPeopleUnion)) {
                            // Общепоточная пара
                            classesPane.getChildren().remove(classesPane.getChildren().size() -
                                    groupsCount - 1, classesPane.getChildren().size() - 1);  // Удаляю пустые поля
                            style.setLabelStyle("big");
                            pairLabel.setText(pair.formatStreamPair());
                            pairPane = GridPaneService.addToGridPane(classesPane, pairLabel, 1,
                                    currentRow, groupsCount, style);
                        } else {
                            // Пара отдельной группы
                            style.setLabelStyle("pair");
                            pairPane = GridPaneService.addToGridPane(classesPane, pairLabel,
                                    groups.indexOf(pair.getGroup()) + 1, currentRow, style);
                        }
                        pairPane.setOnMouseClicked(e -> {
                            if (e.getClickCount() >= 2) {  // On double click
                                viewPairDialog.show(modes, pair);
                                viewPairDialog.getDialog().setOnDialogClosed(skip -> updateClasses());
                            }
                        });
                    }
                }

                if (dayIndex != DateService.daysOfWeek.size() - 1) {
                    currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
                }
            }
        }
        return classesPane;
    }

    private int increaseGridRowIndex(@NonNull final GridPane gridPane,
                                     int rowIndex,
                                     final int increaseValue, // It's always 1, consider removing it
                                     final int groupsCount) {
        for (int i = 0; i < increaseValue; ++i) {
            rowIndex += 1;
            GridPaneService.fillRowEmpty(gridPane, rowIndex, groupsCount);
        }
        return rowIndex;
    }

    private void writeAllGroups(@NonNull final GridPane gridPane,
                                @NonNull final List<PeopleUnion> groups,
                                final int rowIndex,
                                final int beginColumn,  // It's always 1, consider removing it
                                @NonNull final StyleParameter style) {
        int colIndex = beginColumn;
        for (final PeopleUnion group: groups) {
            GridPaneService.addToGridPane(gridPane, new Label(group.getName()), colIndex, rowIndex, style);
            colIndex += 1;
        }
    }
}
