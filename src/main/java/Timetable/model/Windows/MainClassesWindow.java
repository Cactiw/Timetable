package Timetable.model.Windows;

import Timetable.model.Dialogs.ViewDialogs.ViewPairDialog;
import Timetable.model.Pair;
import Timetable.model.Parameters.StyleParameter;
import Timetable.model.PeopleUnion;
import Timetable.service.*;
import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import org.springframework.lang.NonNull;

import java.time.*;
import java.util.List;

public class MainClassesWindow {

    private HBox classesSettings;
    private VBox classes;
    private ScrollPane classesScrollPane;

    private GridPane classesPane;

    private ChoiceBox<PeopleUnion> courseSelect;
    private ChoiceBox<PeopleUnion> streamSelect;

    private PeopleUnionService peopleUnionService;
    private PeopleUnionTypeService peopleUnionTypeService;
    private PairService pairService;
    private ViewPairDialog viewPairDialog;

    private StackPane modes;

    private BorderPane loaderPane;
    private RotateTransition loaderAnimation;

    private DatePicker weekPicker;
    private Task<GridPane> updateTask;

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
        HBox.setMargin(classesSettings, new Insets(0, 30, 0, 0));

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

        streamSelect.valueProperty().addListener(e -> {
            this.weekPicker.setValue(weekPicker.getValue());
//            this.updateClasses();
        });

        weekPicker = WeekPicker.getWeekPicker();
        weekPicker.setValue(DateService.getFirstDayOfWeek(LocalDate.now()));
        weekPicker.setPrefWidth(230);
        weekPicker.valueProperty().addListener(e -> {
            this.updateClasses();
        });
        Region left = new Region();
        HBox.setHgrow(left, Priority.ALWAYS);
        final Image forwardIcon = new Image("/icons/forward.png");
        final Image backIcon = new Image("/icons/backward.png");
        final JFXButton forwardWeekButton = new JFXButton("", new ImageView(forwardIcon));
        final JFXButton backWeekButton = new JFXButton("", new ImageView(backIcon));
        forwardWeekButton.setOnMouseClicked(e -> {
            weekPicker.setValue(weekPicker.getValue().plusDays(7));
        });
        backWeekButton.setOnMouseClicked(e -> {
            weekPicker.setValue(weekPicker.getValue().minusDays(7));
        });
        classesSettings.getChildren().addAll(courseLabel, courseSelect, new Label("      "),
                streamLabel, streamSelect, left, backWeekButton, weekPicker, forwardWeekButton);

        classes.getChildren().add(classesSettings);

        loaderPane = new BorderPane();
        ImageView updateIcon = new ImageView(new Image("icons/refresh.png"));
        loaderAnimation = new RotateTransition(javafx.util.Duration.millis(3000), updateIcon);
        loaderAnimation.setByAngle(360);
        loaderAnimation.setCycleCount(Animation.INDEFINITE);
        loaderAnimation.setInterpolator(Interpolator.LINEAR);
        loaderPane.setCenter(updateIcon);

        addClassesWindow();

        modes.getChildren().add(classes);

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
            System.out.println("Classes & classesPane not Null");
            classes.getChildren().remove(classesScrollPane);
        }

        classes.getChildren().add(loaderPane);
        enableLoader();
        if (updateTask != null && updateTask.isRunning()) {
            updateTask.cancel();
        }
        updateTask = new Task<GridPane>() {
            @Override
            protected GridPane call() throws Exception {
                return getClassesPane();
            }
        };
        updateTask.setOnSucceeded(e -> {
            Platform.runLater( () -> updateClassesWindowLater(updateTask.getValue()) );
        });
        updateTask.setOnFailed(e -> {
                Throwable throwable = updateTask.getException();
                throwable.printStackTrace();
            });
        new Thread(updateTask).start();
    }

    private void updateClassesWindowLater(GridPane newClassesPane) {
        classesPane = newClassesPane;
        classesScrollPane = new ScrollPane();
        classesScrollPane.setContent(classesPane);
        classesScrollPane.setFitToWidth(true);
//        classesScrollPane.setFitToHeight(true);

        classes.getChildren().remove(loaderPane);
        classes.getChildren().add(classesScrollPane);

        HBox.setHgrow(classesPane, Priority.ALWAYS);
    }

    private void enableLoader() {
        loaderAnimation.play();
    }

    private void disableLoader() {
        loaderAnimation.stop();
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
//            final ObservableList<ObservableList<Pair>> week = pairService.getDefaultWeekForStream(groupsTmp);
            final ObservableList<ObservableList<Pair>> week = pairService.getCurrentWeekForStream(groupsTmp, weekPicker.getValue());
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
                    LocalTime currentTime = currentDayPairs.get(0).getClearEndTime();
                    GridPaneService.addToGridPane(classesPane, new Label(currentDayPairs.get(0).formatPairTime()),
                            0, currentRow);
                    for (int pairIndex = 0; pairIndex < currentDayPairs.size(); ++pairIndex) {
                        var pair = currentDayPairs.get(pairIndex);

                        final StyleParameter style = new StyleParameter();
                        if (pair.getPairToChange() != null) {
                            if (pair.getCanceled() != null && pair.getCanceled()) {
                                style.setPaneStyle("red");

                                if (checkChangeOverwritten(pair, pairIndex, currentDayPairs)) {

                                    // На месте этой отменённой пары возникла новая - не отражать отменённую
                                    // ToDo Решить, что с этим делать
                                    continue;
                                }
                            } else {
                                style.setPaneStyle("blue");
//                                if (pair.isChangedThisWeek(weekPicker.getValue())) {
//                                    currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
//                                }
                            }
                        }

                        Label pairLabel = new Label(pair.formatPair());
                        pairLabel.setWrapText(true);
                        pairLabel.setAlignment(Pos.CENTER);
                        pairLabel.setTextAlignment(TextAlignment.CENTER);
                        if (pair.getClearEndTime().compareTo(currentTime) > 0) {
                            if (pair.isBeginTimeMinorDifference(currentTime)) {
                                // Незначительное различие по времени
                                pairLabel.setText(pair.getClearBeginTime().toString() + " — " +
                                        pair.getClearEndTime().toString() + " " +
                                        pairLabel.getText()
                                );
                            } else {
                                // Пара следующая по времени
                                currentTime = pair.getClearEndTime();
                                currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
                                GridPaneService.addToGridPane(classesPane, new Label(pair.formatPairTime()), 0,
                                        currentRow);
                            }
                        }

                        pairLabel.setTextAlignment(TextAlignment.CENTER);

                        addPairPaneToGrid(pair, pairLabel, style, groups, fatherPeopleUnion, groupsCount, currentRow);
                    }
                }

                if (dayIndex != DateService.daysOfWeek.size() - 1) {
                    currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
                }
            }
        }
        return classesPane;
    }

    private void addPairPaneToGrid(
            @NonNull final Pair pair,
            @NonNull final Label pairLabel,
            @NonNull final StyleParameter style,
            @NonNull final List<PeopleUnion> groups,
            @NonNull final PeopleUnion fatherPeopleUnion,
            @NonNull final Integer groupsCount,
            @NonNull final Integer currentRow
            ) {
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
            pairLabel.setMaxWidth(200);
            style.setLabelStyle("pair");
            pairPane = GridPaneService.addToGridPane(classesPane, pairLabel,
                    groups.indexOf(pair.getGroup()) + 1, currentRow, style);
        }
        pairPane.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {  // On double click
                viewPairDialog.show(modes, pair, weekPicker.getValue());
                viewPairDialog.getDialog().setOnDialogClosed(skip -> updateClasses());
            }
        });
    }

    private boolean checkChangeOverwritten(
            @NonNull final Pair pair,
            @NonNull final Integer pairIndex,
            @NonNull final ObservableList<Pair> currentDayPairs
    ) {
        int curIndex = pairIndex;
        while (curIndex > 0) {
            curIndex -= 1;
            var curPair = currentDayPairs.get(curIndex);
            if (curPair.getGroup().equals(pair.getGroup())) {
                if (curPair.isChange() && curPair.isBeginTimeMinorDifference(pair)) {
                    return true;
                }
                break;
            }
        }

        curIndex = pairIndex;
        while (curIndex < currentDayPairs.size() - 1) {
            curIndex += 1;
            var curPair = currentDayPairs.get(curIndex);
            if (curPair.getGroup().equals(pair.getGroup())) {
                if (curPair.isChange() && curPair.isBeginTimeMinorDifference(pair)) {
                    return true;
                }
                break;
            }
        }
        return false;
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
