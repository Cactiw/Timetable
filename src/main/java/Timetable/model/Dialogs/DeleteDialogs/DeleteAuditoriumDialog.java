package Timetable.model.Dialogs.DeleteDialogs;

import Timetable.model.Auditorium;
import Timetable.service.AuditoriumService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DeleteAuditoriumDialog {
    JFXDialog dialog;
    JFXDialog viewDialog;
    private JFXDialogLayout content;

    JFXButton okButton;
    JFXButton cancelButton;

    private Auditorium auditorium;


    @Autowired
    AuditoriumService auditoriumService;

    public void show(@NonNull final StackPane container,
                     @NonNull final Auditorium auditorium) {
        this.auditorium = auditorium;

        dialog = new JFXDialog();
        content = new JFXDialogLayout();
        dialog.setContent(content);

        setActions();
        setContent();

        this.dialog.show(container);
    }

    private void setActions() {
        okButton = new JFXButton("Да");
        okButton.setPrefSize(50, 25);
        okButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: red; -fx-background-color: whitesmoke");
        okButton.setOnAction(this::onConfirm);

        cancelButton = new JFXButton("Отмена");
        cancelButton.setPrefSize(150, 25);
        cancelButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: green; " +
                "-fx-background-color: whitesmoke");
        cancelButton.setOnAction(e -> this.dialog.close());

        content.setActions(okButton, cancelButton);
    }

    private void setContent() {
        content.setHeading(new Label("Удаление аудитории"));
        GridPane root = new GridPane();
        root.add(new Label("Вы действительно хотите удалить аудиторию?\n" +
                "Обратите внимание, это действие не может быть отменено.\n" +
                "Все занятия, проходящие в данной аудитории также будут удалены."), 0, 0);
        content.setBody(root);
    }

    private void onConfirm(@NonNull final ActionEvent actionEvent) {
        auditoriumService.delete(auditorium);
        this.dialog.close();
        if (this.viewDialog != null) {
            this.viewDialog.close();
        }
    }

    public JFXDialog getDialog() {
        return dialog;
    }
}
