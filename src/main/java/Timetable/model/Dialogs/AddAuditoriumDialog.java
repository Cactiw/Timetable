package Timetable.model.Dialogs;

import Timetable.model.Auditorium;
import Timetable.service.AuditoriumService;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AddAuditoriumDialog {
    TextField name, maxStudents;
    List<TextField> emptyList;
    Button okButton;

    AuditoriumService auditoriumService;

    public static final Pattern NUM_PATTERN = Pattern.compile("^\\d+$");

    public AddAuditoriumDialog(AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }


    public void show() {

        // Create the custom dialog.
        Dialog<Auditorium> dialog = new Dialog<>();
        dialog.setTitle("Добавление аудитории");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        okButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        okButton.setDisable(true);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));


        name = new TextField();
        name.setPromptText("Введите название");
        name.textProperty().addListener(this::onTextChanged);
        maxStudents = new TextField();
        maxStudents.setPromptText("Введите вместимость");
        maxStudents.textProperty().addListener(this::onTextChanged);

        emptyList = Arrays.asList(name, maxStudents);


        gridPane.add(name, 1, 0);
        gridPane.add(new Label("Название:"), 0, 0);
        gridPane.add(new Label("Максимум студентов:"), 0, 1);
        gridPane.add(maxStudents, 1, 1);


        gridPane.getStylesheets().add(getClass().getResource("../../../styles.css").toExternalForm());


        dialog.getDialogPane().setContent(gridPane);
        verifyAddUserDialog();

        // Request focus on the username field by default.
        Platform.runLater(() -> name.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Auditorium auditorium = new Auditorium();
                auditorium.setName(name.getText());
                auditorium.setMaxStudents(Integer.valueOf(maxStudents.getText()));
                return auditoriumService.save(auditorium);
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
        Boolean bool = !NUM_PATTERN.matcher(maxStudents.getText()).matches();
        if (correct) {
            correct = !bool;
        }
        red(maxStudents, bool);
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
