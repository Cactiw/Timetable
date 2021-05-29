package Timetable.model.Dialogs;

import Timetable.model.Pair;
import Timetable.model.Request;
import Timetable.service.AuditoriumService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ResolveRequestDialog {
    private JFXDialog dialog;
    private JFXDialogLayout content;

    private JFXButton okButton;

    private StackPane container;
    private Request request;

    private Region left;

    @Autowired
    private AuditoriumService auditoriumService;

    public void show(
            @NonNull final StackPane container,
            @NonNull final Request request
    ) {
        this.request = request;
        this.container = container;

        dialog = new JFXDialog();
        content = new JFXDialogLayout();

        updateContent();

        okButton = new JFXButton("Закрыть");
        okButton.setPrefSize(200, 25);
        okButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: green; -fx-background-color: whitesmoke");
        okButton.setOnAction(e -> this.dialog.close());

        left = new Region();
        HBox.setHgrow(left, Priority.ALWAYS);
        content.setActions(okButton);
        dialog.setContent(content);

        dialog.show(container);

    }

    private void updateContent() {
        GridPane rootPane = new GridPane();
        rootPane.setMinWidth(1000);
        rootPane.setPrefWidth(1000);
        rootPane.setPadding(new Insets(10, 10, 10, 10));
        rootPane.setHgap(30);
        rootPane.setVgap(30);

        content.setBody(rootPane);

        int COL_NUM = 3;
        for (int i = 0; i < COL_NUM; ++i) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            columnConstraints.setHalignment(HPos.CENTER);
            rootPane.getColumnConstraints().add(columnConstraints);
        }

        var auditoriums = auditoriumService.getTopMaxAvailableAuditoriums(
                request.getNewBeginTime(), request.getNewEndTime(),
                request.getRequestPair().getAuditorium() != null ? request.getRequestPair().getAuditorium().getMaxStudents() : 0,
                9
        );

        for (int i = 0; i < auditoriums.size(); ++i) {
            var auditorium = auditoriums.get(i);
            Pane pane = auditoriumService.getAuditoriumPane(container, auditorium);
            rootPane.add(pane, i % COL_NUM, i / COL_NUM);
        }
    }

}
