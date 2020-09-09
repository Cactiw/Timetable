package Timetable.model.Dialogs.AddDialogs;

import Timetable.model.PeopleUnion;
import Timetable.model.User;
import Timetable.service.PeopleUnionService;
import Timetable.service.UserService;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AddUserDialog {
    private TextField firstName;
    private TextField lastName;
    private TextField surName;
    private TextField email;
    private TextField group;
    private List<TextField> emptyList;
    private ChoiceBox<String> role;
    private Button okButton;
    private Label groupLabel;
    private ContextMenu groupPopup;
    private PeopleUnion selectedGroup;

    @NonNull
    private final UserService userService;
    @NonNull
    private final PeopleUnionService peopleUnionService;

    public AddUserDialog(@NonNull final UserService userService, @NonNull final PeopleUnionService peopleUnionService) {
        this.userService = userService;
        this.peopleUnionService = peopleUnionService;
    }

    public void show() {

        // Create the custom dialog.
        final Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Добавление пользователя");

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

        firstName = new TextField();
        firstName.setPromptText("Введите имя");
        firstName.textProperty().addListener(this::onTextChanged);
        lastName = new TextField();
        lastName.setPromptText("Введите фамилию");
        lastName.textProperty().addListener(this::onTextChanged);
        surName = new TextField();
        surName.setPromptText("Введите Отчество");
        surName.textProperty().addListener(this::onTextChanged);
        email = new TextField();
        email.setPromptText("Введите Email");
        email.textProperty().addListener(this::onTextChanged);
        role = new ChoiceBox<>();
        role.setItems(FXCollections.observableArrayList("Преподаватель", "Студент"));
        role.setValue("Студент");
        role.valueProperty().addListener(this::onTextChanged);

        group = new TextField();
        group.setPromptText("Введите группу");
        // Studio tells me there is a duplicate code here
        group.textProperty().addListener(e -> {
            selectedGroup = null;
            if (group.getText().equals("")) {
                groupPopup.hide();
            } else {
                fillGroupPopupItems(group.getText());
                groupPopup.show(group, Side.BOTTOM, 10, 10);
            }
            verifyAddUserDialog();
        });
        groupLabel = new Label("Группа:");

        groupPopup = new ContextMenu();
        groupPopup.setOnAction(e -> {
            var text = ((MenuItem)e.getTarget()).getText();
            group.setText(text);
            selectedGroup = peopleUnionService.getByName(text);
            Platform.runLater(() -> group.positionCaret(group.getText().length()));
            groupPopup.hide();
            verifyAddUserDialog();
        });
        fillGroupPopupItems(group.getText());


        emptyList = Arrays.asList(firstName, lastName, surName, email);

        gridPane.add(new Label("Фамилия:"), 0, 0);
        gridPane.add(lastName, 1, 0);
        gridPane.add(new Label("Имя:"), 0, 1);
        gridPane.add(firstName, 1, 1);
        gridPane.add(new Label("Отчество"), 0, 2);
        gridPane.add(surName, 1, 2);
        gridPane.add(new Label("Email"), 0, 3);
        gridPane.add(email, 1, 3);
        gridPane.add(new Label("Роль"), 0, 4);
        gridPane.add(role, 1, 4);
        gridPane.add(groupLabel, 2, 3, 2, 2);
        gridPane.add(group, 2, 4, 2, 2);


        gridPane.getStylesheets().add(getClass().getResource("../../../../styles/styles.css").toExternalForm());

        dialog.getDialogPane().setContent(gridPane);
        verifyAddUserDialog();

        // Request focus on the username field by default.
        Platform.runLater(() -> lastName.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                User user = new User();
                user.setName(firstName.getText());
                user.setLastName(lastName.getText());
                user.setSurName(surName.getText());
                user.setEmail(email.getText());
                user.setRole(role.getSelectionModel().getSelectedIndex() + 1);
                if (selectedGroup != null) {
                    user.setGroup(selectedGroup);
                }
                userService.save(user);
                return user;
            }
            return null;
        });

        final Optional<User> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            return result.get();
//        }
        result.ifPresent(pair -> System.out.println("User created"));
    }

    private void onTextChanged(@NonNull final Observable observable) {
        verifyAddUserDialog();
    }


    private void verifyAddUserDialog() {
        boolean correct = true;
        for (var x: emptyList) {
            boolean bool = x.getText().isEmpty();
            red(x, bool);
            if (correct) {
                correct = !bool;
            }
        }
        boolean bool = !email.getText().matches("^.+@.+\\..+$");
        red(email, bool);
        if (bool) {
            correct = false;
        }
        if (role.valueProperty().getValue().equals("")) {
            correct = false;
        }

        if (!role.valueProperty().getValue().equals("Студент")) {
            group.setVisible(false);
            groupLabel.setVisible(false);
        } else {
            group.setVisible(true);
            groupLabel.setVisible(true);
            if (selectedGroup == null) {
                correct = false;
                group.getStyleClass().add("error");
            } else {
                group.getStyleClass().removeAll("error");
            }
        }

        okButton.setDisable(!correct);
        //role.valueProperty().getValue()
    }

    private void red(@NonNull final TextField textField, final boolean red) {
        if (red) {
            setRed(textField);
        } else {
            cancelRed(textField);
        }
    }

    // TODO these are common methods for all Add* classes, consider creating one parent class and implementing these methods there
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

    private void fillGroupPopupItems(@NonNull final String name) {
        // Studio tells me there is a duplicate code here
        final ObservableList<PeopleUnion> peopleUnions;
        if (!name.equals("")) {
            peopleUnions = peopleUnionService.searchPeopleUnions(name);
        } else {
            peopleUnions = peopleUnionService.findAll();
        }
        groupPopup.getItems().clear();
        peopleUnions.forEach(peopleUnion -> groupPopup.getItems().add(new MenuItem(peopleUnion.toString())));
    }
}
