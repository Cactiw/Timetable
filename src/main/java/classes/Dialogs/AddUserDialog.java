package classes.Dialogs;

import classes.HibernateUtil;
import classes.User;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AddUserDialog {
    TextField firstName, lastName, surName, email;
    List<TextField> emptyList;
    ChoiceBox<String> role;
    Button okButton;

    public void show() {

        // Create the custom dialog.
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Добавление пользователя");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        okButton = (Button) dialog.getDialogPane().lookupButton(loginButtonType);
        okButton.setDisable(true);

        GridPane gridPane = new GridPane();
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
        role = new ChoiceBox<String>();
        role.setItems(FXCollections.observableArrayList("Преподаватель", "Студент"));
        role.setValue("Преподаватель");
        role.valueProperty().addListener(this::onTextChanged);

        emptyList = Arrays.asList(firstName, lastName, surName, email);


        gridPane.add(firstName, 1, 0);
        gridPane.add(new Label("Имя:"), 0, 0);
        gridPane.add(new Label("Фамилия:"), 0, 1);
        gridPane.add(lastName, 1, 1);
        gridPane.add(new Label("Отчество"), 0, 2);
        gridPane.add(surName, 1, 2);
        gridPane.add(new Label("Email"), 0, 3);
        gridPane.add(email, 1, 3);
        gridPane.add(new Label("Роль"), 0, 4);
        gridPane.add(role, 1, 4);


        gridPane.getStylesheets().add(getClass().getResource("../../styles.css").toExternalForm());

        dialog.getDialogPane().setContent(gridPane);
        verifyAddUserDialog();

        // Request focus on the username field by default.
        Platform.runLater(() -> firstName.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                User user = new User();
                user.setName(firstName.getText());
                user.setLastName(lastName.getText());
                user.setSurName(surName.getText());
                user.setEmail(email.getText());
                user.setRole(role.getSelectionModel().getSelectedIndex() + 1);
                int code = HibernateUtil.createObject(user);
                if (code == -1) {
                    System.out.println("Error");
                } else {
                    return user;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            return result.get();
//        }
        result.ifPresent(pair -> {
            System.out.println("User created");
        });
        return;
    }

    private void onTextChanged(Observable observable) {
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
        if (role.valueProperty().getValue() == "") {
            correct = false;
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
        if (! styleClass.contains("error")) {
            styleClass.add("error");
        }
    }

    private void cancelRed(TextField textField) {
        ObservableList<String> styleClass = textField.getStyleClass();
        if (styleClass.contains("error")) {
            styleClass.removeAll("error");
        }
    }



    private int createUser(User user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try  {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return -1;
        }
        finally {
            session.close();
        }
        return 0;
    }
}
