package Timetable.model.Dialogs.AddDialogs;

import Timetable.model.PeopleUnion;
import Timetable.model.PeopleUnionType;
import Timetable.service.PeopleUnionService;
import Timetable.service.PeopleUnionTypeService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Optional;

public class AddPeopleUnionDialog {

    private TextField name;
    private ChoiceBox<PeopleUnionType> unionType;
    private ChoiceBox<PeopleUnionType> parentUnionType;
    private ChoiceBox<PeopleUnion> parentPeopleUnion;
    private Button okButton;

    private final PeopleUnionTypeService peopleUnionTypeService;
    private final PeopleUnionService peopleUnionService;

    public AddPeopleUnionDialog(@NonNull final PeopleUnionTypeService peopleUnionTypeService,
                                @NonNull final PeopleUnionService peopleUnionService) {
        this.peopleUnionTypeService = peopleUnionTypeService;
        this.peopleUnionService = peopleUnionService;
    }


    public void show() {

        // Create the custom dialog.
        Dialog<PeopleUnion> dialog = new Dialog<>();
        dialog.setTitle("Добавление группы");

        // Set the button types.
        // Studio tells me there is a duplicate code here
        final ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        okButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        okButton.setDisable(true);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        final ObservableList<PeopleUnionType> allPeopleUnionTypes = peopleUnionTypeService.findAll();
        unionType = new ChoiceBox<>(allPeopleUnionTypes);
        unionType.setValue(allPeopleUnionTypes.get(0));
        unionType.setOnAction(e -> parentUnionType.setValue(unionType.getValue().getParent()));

        parentUnionType = new ChoiceBox<>(allPeopleUnionTypes);
        parentUnionType.setValue(allPeopleUnionTypes.get(0).getParent());
        parentUnionType.setDisable(true);
        parentUnionType.setOnAction(e -> {
            if (parentUnionType.getValue() != null) {
                parentPeopleUnion.setItems(peopleUnionService.findAllByTypeEquals(parentUnionType.getValue()));
            } else {
                parentPeopleUnion.setValue(null);
                parentPeopleUnion.setItems(FXCollections.observableArrayList(new ArrayList<>()));
            }
            verifyDialog();
        });

        parentPeopleUnion = new ChoiceBox<>(peopleUnionService.findAll());
        if (parentUnionType.getValue() == null) {
            // TODO fill in or remove
        }
        parentPeopleUnion.setOnAction(e -> verifyDialog());

        name = new TextField();
        name.setPromptText("Введите название");
        name.textProperty().addListener(e -> verifyDialog());

        Platform.runLater(() -> name.requestFocus());

        gridPane.add(new Label("Название"), 0, 0);
        gridPane.add(name, 1, 0);
        gridPane.add(new Label("Тип:"), 0, 1);
        gridPane.add(unionType, 1, 1);
        gridPane.add(new Label("Родительский тип:"), 0, 2);
        gridPane.add(parentUnionType, 1, 2);
        gridPane.add(new Label("Родительская группа:"), 0, 3);
        gridPane.add(parentPeopleUnion, 1, 3);

        gridPane.getStylesheets().add(getClass().getResource("../../../styles.css").toExternalForm());

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(e -> {
            if (e == loginButtonType) {
                final PeopleUnion peopleUnion = new PeopleUnion();
                peopleUnion.setName(name.getText());
                peopleUnion.setType(unionType.getValue());
                if (parentPeopleUnion.getValue() != null) {
                    peopleUnion.setParent(parentPeopleUnion.getValue());
                }
                return peopleUnionService.save(peopleUnion);
            }
            return null;
        });
        Platform.runLater(this::verifyDialog);
        final Optional<PeopleUnion> peopleUnion = dialog.showAndWait(); // Never used

    }

    private void verifyDialog() {
        boolean correct = true;
        boolean bool = name.getText().isEmpty();
        red(name, bool);
        // Always true
        if (correct) {
            correct = !bool;
        }
        if (parentUnionType.getValue() != null) {
            bool = parentPeopleUnion.getValue() == null;
            if (correct) {
                correct = !bool;
            }
            parentPeopleUnion.getStyleClass().add("error");
        } else {
            parentPeopleUnion.getStyleClass().removeAll("error");
        }
        parentPeopleUnion.setDisable(parentUnionType.getValue() == null);
        okButton.setDisable(!correct);
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
        if (! styleClass.contains("error")) {
            styleClass.add("error");
        }
    }

    private void cancelRed(@NonNull final TextField textField) {
        final ObservableList<String> styleClass = textField.getStyleClass();
        if (styleClass.contains("error")) {
            styleClass.removeAll("error");
        }
    }
}
