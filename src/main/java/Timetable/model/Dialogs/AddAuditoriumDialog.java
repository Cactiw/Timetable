package Timetable.model.Dialogs;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import Timetable.service.AuditoriumService;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class AddAuditoriumDialog {
    public static final Pattern NUM_PATTERN = Pattern.compile("^\\d+$");

    Dialog<Auditorium> dialog;
    private Auditorium auditoriumFrom;

    private TextField name;
    private TextField maxStudents;
    private List<TextField> emptyList;
    private Button okButton;

    @NonNull
    private final AuditoriumService auditoriumService;

    public AddAuditoriumDialog(@NonNull final AuditoriumService auditoriumService) {
        this.auditoriumService = auditoriumService;
    }

    public void showFromAuditorium(Auditorium auditorium) {
        this.init();
        this.auditoriumFrom = auditorium;

        this.name.setText(auditorium.getName());
        this.maxStudents.setText(auditorium.getMaxStudents().toString());

        this.dialog.showAndWait();
    }


    public void show() {
        this.init();
        final Optional<Auditorium> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            return result.get();
//        }
        result.ifPresent(pair -> System.out.println("Auditorium created"));
    }

    public void init() {

        // Create the custom dialog.
        dialog = new Dialog<>();
        dialog.setTitle("Добавление аудитории");

        // Set the button types.
        final ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        // Studio tells me there is a duplicate code here
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        okButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        okButton.setDisable(true);

        final GridPane gridPane = new GridPane();
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
                final Auditorium auditorium = Objects.requireNonNullElseGet(auditoriumFrom, Auditorium::new);
                auditorium.setName(name.getText());
                auditorium.setMaxStudents(Integer.valueOf(maxStudents.getText()));
                return auditoriumService.save(auditorium);
            }
            return null;
        });
    }

    private void onTextChanged(@NonNull final Observable observable) {
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
        final boolean bool = !NUM_PATTERN.matcher(maxStudents.getText()).matches();
        if (correct) {
            correct = !bool;
        }
        red(maxStudents, bool);
        okButton.setDisable(!correct);
        //role.valueProperty().getValue()
    }

    // TODO these are common methods for all Add* classes, consider creating one parent class and implementing these methods there
    private void red(@NonNull final TextField textField, final boolean red) {
        if (red) {
            setRed(textField);
        } else {
            cancelRed(textField);
        }
    }

    private void setRed(@NonNull final TextField textField) {
        final ObservableList<String> styleClass = textField.getStyleClass();
        if (!styleClass.contains("error")) {
            styleClass.add("error");
        }
    }

    private void cancelRed(@NonNull final TextField textField) {
        ObservableList<String> styleClass = textField.getStyleClass();
        if (styleClass.contains("error")) {
            styleClass.removeAll("error");
        }
    }


}
