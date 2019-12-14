package classes.Dialogs;

import classes.Pair;
import classes.PeopleUnionType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class AddPeopleUnionDialog {

    TextField name;
    ChoiceBox<PeopleUnionType> unionType;
    Button okButton;


    public void show() {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/addPeopleUnion.fxml"));
//
//
//        loader.setRoot(this);
//        loader.setController(this);
//
//        try {
//            loader.load();
//        } catch (IOException e) {
//            System.out.println("Error opening AddPeopleUnionDialog: " + e.getMessage());
//        }

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


        name = new TextField();
        name.setPromptText("Введите предмет");
        //name.textProperty().addListener(this::onTextChanged);
        unionType = new ChoiceBox<>();
        //unionType.setItems();



    }
}
