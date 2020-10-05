package Timetable.model.Dialogs.DeleteDialogs;

import Timetable.model.Auditorium;
import Timetable.model.Pair;
import Timetable.service.AuditoriumService;
import Timetable.service.PairService;
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
public class DeletePairDialog {
    JFXDialog dialog;
    JFXDialog viewDialog;
    private JFXDialogLayout content;

    JFXButton okButton;
    JFXButton cancelButton;

    private Pair pair;


    @Autowired
    PairService pairService;

    public void show(@NonNull final StackPane container,
                     @NonNull final Pair pair) {
        this.pair = pair;

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
        root.add(new Label("Вы действительно хотите удалить занятие?\n" +
                "Обратите внимание, это действие не может быть отменено.\n" +
                "Все связанные с занятием данные также будут удалены."), 0, 0);
        content.setBody(root);
    }

    private void onConfirm(@NonNull final ActionEvent actionEvent) {
        pairService.delete(pair);
        this.dialog.close();
        if (this.viewDialog != null) {
            this.viewDialog.close();
        }
    }

    public JFXDialog getDialog() {
        return dialog;
    }
}
