package Timetable.model.Dialogs;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import Timetable.model.PeopleUnion;
import Timetable.model.User;
import Timetable.service.AuditoriumService;
import Timetable.service.PairService;
import Timetable.service.PeopleUnionService;
import Timetable.service.UserService;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Component
public class AddPairDialog {
    Dialog<Pair> dialog;

    TextField subject, teacher, auditorium, group;
    List<TextField> emptyList;
    List<ComboBoxBase> notNullList;
    Button okButton;
    Text conflicts, suggestions;
    TextFlow conflictsCheck;
    Pair pairFrom;

    ContextMenu auditoriumPopup, teacherPopup, groupPopup;
    TextField currentParentField;
    Auditorium auditoriumEntity;
    User teacherEntity;
    PeopleUnion groupEntity;
    JFXDatePicker beginDate;
    JFXTimePicker beginTime, endTime;

    Boolean beginTimeChanged = false;

    ChoiceBox<String> repeatability;

    LocalTime PAIR_LENGTH = LocalTime.of(1, 35);

    @Autowired
    private UserService userService;
    @Autowired
    private AuditoriumService auditoriumService;
    @Autowired
    private PairService pairService;
    @Autowired
    private PeopleUnionService peopleUnionService;

    public void show() {
        pairFrom = null;
        this.init();
        Optional<Pair> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            return result.get();
//        }
        result.ifPresent(pair -> {
            System.out.println("Pair created");
        });
        return;
    }

    public void showFromPair(Pair pair) {
        pairFrom = pair;
        this.init();

        subject.setText(pair.getSubject());
        setTeacher(pair.getTeacher());
        setAuditorium(pair.getAuditorium());
        setGroup(pair.getGroup());
        beginDate.valueProperty().setValue(pair.getBeginTime().toLocalDate());
        beginTime.valueProperty().setValue(pair.getBeginTime().toLocalTime());
        endTime.valueProperty().setValue(pair.getEndTime().toLocalTime());

        repeatability.setValue(repeatability.itemsProperty().get().get(pair.getRepeatability()));
        Optional<Pair> result = dialog.showAndWait();
        return;
    }

    public static void fromPair(Pair pair) {
        var dialog = new AddPairDialog();
        dialog.showFromPair(pair);
    }

    private void init() {
        // Create the custom dialog.
        dialog = new Dialog<>();
        dialog.setTitle("Добавление занятия");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        okButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        okButton.setDisable(true);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        gridPane.getStylesheets().add(getClass().getResource("../../../styles.css").toExternalForm());

        subject = new TextField();
        subject.setPromptText("Введите предмет");
        subject.textProperty().addListener(this::onTextChanged);
        teacher = new TextField();
        teacher.setPromptText("ФИО преподавателя");
        teacher.textProperty().addListener((observableValue, s, t1) -> {
            teacherEntity = null;
            SortedList<User> users;
            if (observableValue.getValue().compareTo("") == 0) {
                teacherPopup.hide();
            } else {
                users = new SortedList<>(userService.searchUserByName(observableValue.getValue(), 1));
                teacherPopup.getItems().clear();
                for (var x : users) {
                    teacherPopup.getItems().add(new MenuItem(x.formatFIO()));
                }
            }
            teacherPopup.show(teacher, Side.BOTTOM, 0, 0);
            verifyAddUserDialog();
        });
        auditorium = new TextField();
        auditorium.setPromptText("Введите аудиторию");
        auditorium.textProperty().addListener((observableValue, s, t1) -> {
            auditoriumEntity = null;
            SortedList<Auditorium> auditoriums;
            if (observableValue.getValue().compareTo("") == 0) {
                auditoriumPopup.hide();
                //auditoriums = new SortedList<>(Auditorium.getAuditoriums());
            } else {
                auditoriums = new SortedList<>(auditoriumService.searchAuditoriums(observableValue.getValue()));

                ArrayList<String> auditoriumNames = new ArrayList<>();
                auditoriumPopup.getItems().clear();
                for (var x : auditoriums) {
                    auditoriumNames.add(x.getName());
                    auditoriumPopup.getItems().add(new MenuItem(x.getName()));
                }
                auditoriumPopup.show(auditorium, Side.BOTTOM, 0, 0);
            }
            verifyAddUserDialog();
        });
        group = new TextField();
        group.setPromptText("Введите название группы");
        group.textProperty().addListener(e -> {
            groupEntity = null;
            if (group.getText().equals("")) {
                groupPopup.hide();
            } else {
                fillGroupPopupItems(group.getText());
                groupPopup.show(group, Side.BOTTOM, 10, 10);
            }
            verifyAddUserDialog();
        });

        Label groupLabel = new Label("Группа:");

        groupPopup = new ContextMenu();
        groupPopup.setOnAction(e -> {
            var text = ((MenuItem) e.getTarget()).getText();
            setGroup(peopleUnionService.getByName(text));
            groupPopup.hide();
        });
        fillGroupPopupItems(group.getText());


        auditoriumPopup = new ContextMenu();
        auditoriumPopup.setOnAction(actionEvent -> {
            String text = ((MenuItem) actionEvent.getTarget()).getText();
            setAuditorium(auditoriumService.getAuditoriumByName(text));
        });

        teacherPopup = new ContextMenu();
        teacherPopup.setOnAction(actionEvent -> {
            String text = ((MenuItem) actionEvent.getTarget()).getText();
            setTeacher(userService.searchUserByName(text, 1).get(0));
        });

        endTime = new JFXTimePicker();
        endTime.set24HourView(true);
//        endTime.setDefaultColor(Color.valueOf("#009688"));
        endTime.valueProperty().addListener((observableValue, localTime, t1) -> {
            if (!beginTimeChanged) {
                verifyAddUserDialog();
            }
            beginTimeChanged = false;
        });

        beginDate = new JFXDatePicker();
        beginDate.valueProperty().addListener((observableValue, localDate, t1) -> {
            verifyAddUserDialog();
        });
        beginTime = new JFXTimePicker();
        beginTime.set24HourView(true);
        beginTime.valueProperty().addListener((observableValue, localTime, t1) -> {
            if (observableValue.getValue() != null) {
                beginTimeChanged = true;
                endTime.setValue(LocalTime.of(observableValue.getValue().getHour(), observableValue.getValue().
                        getMinute()).plusHours(PAIR_LENGTH.getHour()).plusMinutes(PAIR_LENGTH.getMinute()));
            }
            verifyAddUserDialog();
        });

        repeatability = new ChoiceBox<>();
        repeatability.setItems(FXCollections.observableArrayList(Arrays.asList("Один раз", "Еженедельно")));
        repeatability.setValue("Еженедельно");
        repeatability.valueProperty().addListener(this::onTextChanged);


        gridPane.add(subject, 1, 0);
        gridPane.add(new Label("Предмет:"), 0, 0);
        gridPane.add(new Label("Преподаватель:"), 0, 1);
        gridPane.add(teacher, 1, 1);

        gridPane.add(new Label("Аудитория:"), 0, 2);
        gridPane.add(auditorium, 1, 2);
        gridPane.add(groupLabel, 0, 3);
        gridPane.add(group, 1, 3);
        gridPane.add(new Label("Дата занятия:"), 0, 4);
        gridPane.add(beginDate, 1, 4);
        gridPane.add(new Label("Начало занятия:"), 0, 5);
        gridPane.add(beginTime, 1, 5);
        gridPane.add(new Label("Окончание занятия:"), 0, 6);
        gridPane.add(endTime, 1, 6);
        gridPane.add(new Label("Периодичность:"), 0, 7);
        gridPane.add(repeatability, 1, 7);

        conflicts = new Text("");
        suggestions = new Text("\n");
        suggestions.setStyle("-fx-underline: true");
        suggestions.setOnMouseClicked(e -> {

        });
        suggestions.setFont(Font.font("Calibri", 15));
        setNoConflicts();
        conflicts.setFont(Font.font("Calibri", 15));
//        gridPane.add(conflicts, 2, 1, 5, 5);

        conflictsCheck = new TextFlow(conflicts, suggestions);
        conflictsCheck.setPrefWidth(250);
//        conflictsCheck.setLayoutY();

        gridPane.add(conflictsCheck, 2, 2, 7, 7);


        // Список из полей, которые должны быть не пустыми при корректном заполнении диалога
        emptyList = Arrays.asList(subject, teacher, auditorium, group);
        notNullList = Arrays.asList(beginDate, beginTime, endTime);

        dialog.getDialogPane().setContent(gridPane);
        verifyAddUserDialog();

        // Request focus on the usersubject field by default.
        Platform.runLater(() -> subject.requestFocus());

        // Convert the result to a usersubject-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Pair pair = Objects.requireNonNullElseGet(pairFrom, Pair::new);
                pair.setAuditorium(auditoriumEntity);
                pair.setTeacher(teacherEntity);
                pair.setGroup(groupEntity);
                pair.setSubject(subject.getText());
                pair.setBeginTime(getBeginTime());
                pair.setEndTime(getEndTime());
                pair.setRepeatability(repeatability.getSelectionModel().getSelectedIndex());

                return pairService.save(pair);

            }
            return null;
        });
    }

    EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
        public void handle(KeyEvent e) {
            EventTarget target = e.getTarget();
            currentParentField = (TextField) target;
            EventType<? extends Event> type = e.getEventType();
            if (type == KeyEvent.KEY_PRESSED) {
                System.out.println("Changed");
                KeyCode code = e.getCode();
                if (code == KeyCode.DOWN) {
                    auditoriumPopup.show(currentParentField, Side.BOTTOM, 0, 0); //<- this
                } else {
                    auditoriumPopup.hide();
                }
            }
        }
    };

    private void onTextChanged(Observable observable) {
        verifyAddUserDialog();
    }

    private LocalDateTime getBeginTime() {
        return LocalDateTime.of(beginDate.valueProperty().get(), beginTime.valueProperty().get());
    }

    private LocalDateTime getEndTime() {
        return LocalDateTime.of(beginDate.valueProperty().get(), endTime.valueProperty().get());
    }


    private void setTeacher(User newTeacher) {
        teacher.setText(newTeacher.formatFIO());
        teacherEntity = newTeacher;
        Platform.runLater(() -> teacher.positionCaret(teacher.getText().length()));
        verifyAddUserDialog();
    }

    private void setAuditorium(Auditorium newAuditorium) {
        auditorium.setText(newAuditorium.getName());
        auditoriumEntity = newAuditorium;
        Platform.runLater(() -> auditorium.positionCaret(auditorium.getText().length()));
        verifyAddUserDialog();
    }

    private void setGroup(PeopleUnion newGroup) {
        group.setText(newGroup.getName());
        groupEntity = newGroup;
        Platform.runLater(() -> group.positionCaret(group.getText().length()));
        verifyAddUserDialog();
    }

    private void verifyAddUserDialog() {
        boolean correct = true;
        for (var x : emptyList) {
            boolean bool = x.getText().isEmpty();
            red(x, bool);
            if (correct) {
                correct = !bool;
            }
        }
        if (auditoriumEntity == null) {
            correct = false;
            red(auditorium, true);
        }
        if (teacherEntity == null) {
            correct = false;
            red(teacher, true);
        }
        if (groupEntity == null) {
            correct = false;
            red(group, true);
        }
        for (var x : notNullList) {
            boolean bool = x.valueProperty().isNull().get();
            if (bool) {
                correct = false;
            }
            red(x, bool);
//            beginDate.setDefaultColor(Color.RED);
        }
        if (correct) {
            setNoConflicts();
            // Проверка на конфликты преподавателя
            var teacherPairs = pairService.getDefaultWeekForTeacher(teacherEntity);
            for (var pair : teacherPairs) {
                if (getBeginTime().compareTo(pair.getEndTime()) > 0 ||
                        getEndTime().compareTo(pair.getBeginTime()) < 0 || (pairFrom != null && pairFrom.equals(pair))) {
                    // Не пересекаются, всё норм
                } else {
                    // Пересекаются, алёрт
                    setConflict("Преподаватель в это время занят:\n" + pair.getSubject() + " " +
                            pair.getAuditorium().getName() + " " + pair.getBeginTime().toLocalTime().toString() + " - " +
                            pair.getEndTime().toLocalTime().toString(), "");
                    correct = false;
                }
            }
            // Проверка на конфликты аудитории
            var auditoriumConflictPairs = pairService.getAuditoriumConflictPairs(auditoriumEntity,
                    getBeginTime().getDayOfWeek().getValue(),
                    getBeginTime().toLocalTime(), getEndTime().toLocalTime());
            for (var pair : auditoriumConflictPairs) {
                if (pairFrom != null && pairFrom.equals(pair)) { // Пара совпала с текущей - не конфликт
                    continue;
                }
                var availableAuditoriums = auditoriumService.getAvailableAuditoriums(getBeginTime(), getEndTime());
                String suggestion = !availableAuditoriums.isEmpty() ? "Подходящая аудитория:\n" +
                        availableAuditoriums.get(0).getName() + ", вместимость: " +
                        availableAuditoriums.get(0).getMaxStudents() : "Подходящих аудиторий не найдено.";
                setConflict("Аудитория в это время занята:\n" + pair.getSubject() + " " +
                        pair.getAuditorium().getName() + " " + pair.getBeginTime().toLocalTime().toString() + " - " +
                        pair.getEndTime().toLocalTime().toString(), suggestion);
                correct = false;
            }

            // Проверка на конфликты групп
            var pairs = pairService.getGroupConflictPairs(groupEntity, getBeginTime().getDayOfWeek().getValue(),
                    getBeginTime().toLocalTime(), getEndTime().toLocalTime());
            for (var pair : pairs) {
                if (pairFrom != null && pairFrom.equals(pair)) { // Пара совпала с текущей - не конфликт
                    continue;
                }
                setConflict("Группа в это время занята:\n" + pair.getSubject() + " " +
                        pair.getAuditorium().getName() + " " + pair.getBeginTime().toLocalTime().toString() + " - " +
                        pair.getEndTime().toLocalTime().toString(), "");
                correct = false;
            }
        }
        okButton.setDisable(!correct);

        ;
        //role.valueProperty().getValue()
    }

    private void setNoConflicts() {
        conflicts.setText("✅Нет конфликтов");
        conflicts.setFill(Color.GREEN);

        suggestions.setVisible(false);
        suggestions.setText("\n");
        suggestions.setFill(Color.YELLOWGREEN);
    }

    private void setConflict(String conflict, String suggestion) {
        suggestions.setText(suggestions.getText() + "\n" + suggestion);
        suggestions.setVisible(true);
        if (conflicts.getFill() == Color.ORANGERED) {
            // Добавляем конфликт
            conflicts.setText(conflicts.getText() + "\n" + conflict);
        } else {
            conflicts.setText(conflict);
            conflicts.setFill(Color.ORANGERED);
        }
    }

    private void red(Control field, boolean red) {
        if (red) {
            setRed(field);
        } else {
            cancelRed(field);
        }
    }

    private void setRed(Control field) {
        ObservableList<String> styleClass = field.getStyleClass();
        if (!styleClass.contains("error")) {
            styleClass.add("error");
        }
    }

    private void cancelRed(Control field) {
        ObservableList<String> styleClass = field.getStyleClass();
        if (styleClass.contains("error")) {
            styleClass.removeAll("error");
        }
    }

    private void fillGroupPopupItems(String name) {
        ObservableList<PeopleUnion> peopleUnions;
        if (!name.equals("")) {
            peopleUnions = peopleUnionService.searchPeopleUnions(name);
        } else {
            peopleUnions = peopleUnionService.findAll();
        }
        groupPopup.getItems().clear();
        peopleUnions.forEach(peopleUnion -> {
            groupPopup.getItems().add(new MenuItem(peopleUnion.toString()));
        });
    }


}
