package Timetable.model.Dialogs;

import Timetable.model.Pair;
import Timetable.model.PeopleUnion;
import Timetable.model.PeopleUnionType;
import Timetable.service.PeopleUnionTypeService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class AddPeopleUnionDialog {

    TextField name;
    ChoiceBox<PeopleUnionType> unionType;
    Button okButton;

    PeopleUnionTypeService peopleUnionTypeService;

    public AddPeopleUnionDialog(PeopleUnionTypeService peopleUnionTypeService) {
        this.peopleUnionTypeService = peopleUnionTypeService;
    }


    public void show() {

        // Create the custom dialog.
        Dialog<PeopleUnion> dialog = new Dialog<>();
        dialog.setTitle("Добавление группы");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        okButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        okButton.setDisable(true);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        unionType = new ChoiceBox<>(peopleUnionTypeService.findAll());

        gridPane.add(new Label("Тип:"), 0, 0);
        gridPane.add(unionType, 1, 0);
//        gridPane.add(new Label("Имя:"), 0, 1);
//        gridPane.add(firstName, 1, 1);
//        gridPane.add(new Label("Отчество"), 0, 2);
//        gridPane.add(surName, 1, 2);
//        gridPane.add(new Label("Email"), 0, 3);
//        gridPane.add(email, 1, 3);
//        gridPane.add(new Label("Роль"), 0, 4);
//        gridPane.add(role, 1, 4);
        //name.textProperty().addListener(this::onTextChanged);
        //unionType.setItems();

        gridPane.getStylesheets().add(getClass().getResource("../../../styles.css").toExternalForm());

        dialog.getDialogPane().setContent(gridPane);

        Optional<PeopleUnion> peopleUnion = dialog.showAndWait();

    }
}
