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
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AddPairDialog {
    TextField subject, teacher, auditorium;
    List<TextField> emptyList;
    Button okButton;

    ContextMenu popup;
    TextField currentParentField;


    public void show() {

        // Create the custom dialog.
        Dialog<Auditorium> dialog = new Dialog<>();
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
        auditorium = new TextField();
        auditorium.setPromptText("Введите аудиторию");
        auditorium.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                SortedList<Auditorium> auditoriums;
//                if (observableValue.getValue().compareTo("") == 0) {
//                    auditoriums = new SortedList<>(Auditorium.getAuditoriums());
//                } else {
//                    auditoriums = new SortedList<>(Auditorium.searchAuditoriums(observableValue.getValue()));
//                }
//                SortedList<String> auditoriumNames = new SortedList<String>();
//                for (var x: auditoriums) {
//                    auditoriumNames.add(x.getName());
//                }
            }
        });

        // Список из полей, которые должны быть не пустыми при корректном заполнении диалога
        emptyList = Arrays.asList(subject, teacher, auditorium);

        popup = new ContextMenu();
        popup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                MenuItem src = (MenuItem)actionEvent.getTarget();
                String text = src.getText();
                currentParentField.setText( text );
            }
        });


        TextField textField_1 = new TextField();
        TextField textField_2 = new TextField();
        textField_1.addEventHandler( KeyEvent.ANY, keyEventHandler );
        textField_2.addEventHandler( KeyEvent.ANY, keyEventHandler );


        gridPane.add(subject, 1, 0);
        gridPane.add(new Label("Предмет:"), 0, 0);
        gridPane.add(new Label("Преподаватель:"), 0, 1);
        gridPane.add(teacher, 1, 1);;
        gridPane.add(new Label("Аудитория:"), 0, 2);
        gridPane.add(auditorium, 1, 2);
        gridPane.add(textField_1, 1, 3);
        gridPane.add(textField_2, 1, 4);

        gridPane.getStylesheets().add(getClass().getResource("../styles.css").toExternalForm());


        dialog.getDialogPane().setContent(gridPane);
        verifyAddUserDialog();

        // Request focus on the usersubject field by default.
        Platform.runLater(() -> subject.requestFocus());

        // Convert the result to a usersubject-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Auditorium auditorium = new Auditorium();
//                auditorium.setsubject(subject.getText());
//                auditorium.setteacher(Integer.valueOf(teacher.getText()));
                int code = HibernateUtil.createObject(auditorium);
                if (code == -1) {
                    System.out.println("Error");
                } else {
                    return auditorium;
                }
            }
            return null;
        });

        Optional<Auditorium> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            return result.get();
//        }
        result.ifPresent(pair -> {
            System.out.println("Auditorium created");
        });
        return;
    }

    EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
        public void handle(KeyEvent e){
            EventTarget target = e.getTarget();
            currentParentField = (TextField)target;
            EventType<? extends Event> type = e.getEventType();
            if( type == KeyEvent.KEY_PRESSED ){
                System.out.println("Changed");
                KeyCode code = e.getCode();
                if( code == KeyCode.DOWN ){
                    popup.show( currentParentField, Side.BOTTOM, 0, 0 ); //<- this
                }
                else{
                    popup.hide();
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
        }okButton.setDisable(!correct);
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
