package classes;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.Popup;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AddPairDialog {
    TextField subject, teacher, auditorium;
    List<TextField> emptyList;
    Button okButton;

    ContextMenu auditoriumPopup, teacherPopup;
    TextField currentParentField;
    Integer auditoriumId, teacherId;


    public void show() {

        // Create the custom dialog.
        Dialog<Pair> dialog = new Dialog<>();
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


        subject = new TextField();
        subject.setPromptText("Введите предмет");
        subject.textProperty().addListener(this::onTextChanged);
        teacher = new TextField();
        teacher.setPromptText("ФИО преподавателя");
        teacher.textProperty().addListener(this::onTextChanged);
        teacher.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                SortedList<User> users;
                if (observableValue.getValue().compareTo("") == 0) {
                    teacherPopup.hide();
                } else {
                    users = new SortedList<>(User.searchUserByName(observableValue.getValue()));
                    teacherPopup.getItems().clear();
                    for (var x: users) {
                        teacherPopup.getItems().add(new MenuItem(x.formatFIO()));
                    }
                }
                teacherPopup.show(teacher, Side.BOTTOM, 0, 0);
            }
        });
        auditorium = new TextField();
        auditorium.setPromptText("Введите аудиторию");
        auditorium.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                SortedList<Auditorium> auditoriums;
                if (observableValue.getValue().compareTo("") == 0) {
                    auditoriumPopup.hide();
                    //auditoriums = new SortedList<>(Auditorium.getAuditoriums());
                } else {
                    auditoriums = new SortedList<>(Auditorium.searchAuditoriums(observableValue.getValue()));

                    ArrayList<String> auditoriumNames = new ArrayList<>();
                    auditoriumPopup.getItems().clear();
                    for (var x : auditoriums) {
                        auditoriumNames.add(x.getName());
                        auditoriumPopup.getItems().add(new MenuItem(x.getName()));
                    }
                    auditoriumPopup.show(auditorium, Side.BOTTOM, 0, 0);
                }
                verifyAddUserDialog();
            }
        });

        // Список из полей, которые должны быть не пустыми при корректном заполнении диалога
        emptyList = Arrays.asList(subject, teacher, auditorium);

        auditoriumPopup = new ContextMenu();
        auditoriumPopup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                MenuItem src = (MenuItem) actionEvent.getTarget();
                String text = src.getText();
                auditorium.setText(text);
                auditoriumId = Auditorium.getAuditoriumByName(text).getId();
                Platform.runLater(() -> auditorium.positionCaret(auditorium.getText().length()));
            }
        });

        teacherPopup = new ContextMenu();
        teacherPopup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                MenuItem src = (MenuItem) actionEvent.getTarget();
                String text = src.getText();
                teacher.setText(text);
                teacherId = User.searchUserByName(text).get(0).getId();
                Platform.runLater(() -> auditorium.positionCaret(auditorium.getText().length()));
            }
        });


        gridPane.add(subject, 1, 0);
        gridPane.add(new Label("Предмет:"), 0, 0);
        gridPane.add(new Label("Преподаватель:"), 0, 1);
        gridPane.add(teacher, 1, 1);
        ;
        gridPane.add(new Label("Аудитория:"), 0, 2);
        gridPane.add(auditorium, 1, 2);

        gridPane.getStylesheets().add(getClass().getResource("../styles.css").toExternalForm());


        dialog.getDialogPane().setContent(gridPane);
        verifyAddUserDialog();

        // Request focus on the usersubject field by default.
        Platform.runLater(() -> subject.requestFocus());

        // Convert the result to a usersubject-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Pair pair = new Pair();
                pair.setAuditoriumId(auditoriumId);
                pair.setSubject(subject.getText());
//                auditorium.setsubject(subject.getText());
//                auditorium.setteacher(Integer.valueOf(teacher.getText()));
                int code = HibernateUtil.createObject(pair);
                if (code == -1) {
                    System.out.println("Error");
                } else {
                    return pair;
                }
            }
            return null;
        });

        Optional<Pair> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            return result.get();
//        }
        result.ifPresent(pair -> {
            System.out.println("Pair created");
        });
        return;
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

    private void verifyAddUserDialog() {
        boolean correct = true;
        for (var x : emptyList) {
            boolean bool = x.getText().isEmpty();
            red(x, bool);
            if (correct) {
                correct = !bool;
            }
        }
        okButton.setDisable(!correct);
        //role.valueProperty().getValue()
    }

    private void red(TextField textField, boolean red) {
        if (red) {
            setRed(textField);
        } else {
            cancelRed(textField);
        }
    }

    private void setRed(TextField textField) {
        ObservableList<String> styleClass = textField.getStyleClass();
        if (!styleClass.contains("error")) {
            styleClass.add("error");
        }
    }

    private void cancelRed(TextField textField) {
        ObservableList<String> styleClass = textField.getStyleClass();
        if (styleClass.contains("error")) {
            styleClass.removeAll("error");
        }
    }


}
