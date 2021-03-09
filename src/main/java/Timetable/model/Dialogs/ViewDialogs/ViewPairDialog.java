package Timetable.model.Dialogs.ViewDialogs;

import Timetable.model.Dialogs.AddDialogs.AddPairDialog;
import Timetable.model.Dialogs.DeleteDialogs.DeleteAuditoriumDialog;
import Timetable.model.Dialogs.DeleteDialogs.DeletePairDialog;
import Timetable.model.Pair;
import Timetable.service.DateService;
import Timetable.service.PairService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ViewPairDialog {
    private JFXDialog dialog;
    private JFXDialogLayout content;

    private JFXButton okButton;
    private JFXButton cancelPairButton;
    private Region left;

    private StackPane container;
    private Pair pair;

    @Autowired
    AddPairDialog addPairDialog;
    @Autowired
    ViewAuditoriumDialog viewAuditoriumDialog;
    @Autowired
    DeletePairDialog deletePairDialog;
    @Autowired
    PairService pairService;

    public void show(@NonNull final StackPane container, @NonNull final Pair pair) {
        this.pair = pair;
        this.container = container;

        dialog = new JFXDialog();
        content = new JFXDialogLayout();

        updateContent();

        left = new Region();
        HBox.setHgrow(left, Priority.ALWAYS);
        content.setActions(okButton);
        dialog.setContent(content);

        dialog.show(container);

    }

    private void updateContent() {
        okButton = new JFXButton("Ок");
        okButton.setPrefSize(50, 25);
        okButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: green; -fx-background-color: whitesmoke");
        okButton.setOnAction(e -> this.dialog.close());


        cancelPairButton = new JFXButton("Отменить");
//        cancelPairButton.setPrefSize(50, 25);
        cancelPairButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: orangered");
        cancelPairButton.setOnAction(e -> this.cancelPair());

        final GridPane rootPane = new GridPane();

        final Text heading = new Text(pair.formatPair());
        heading.styleProperty().setValue("-fx-font-size: 14pt;");
        content.setHeading(heading);
        content.setBody(rootPane);

        rootPane.setPadding(new Insets(10, 0, 10, 0));
        rootPane.setHgap(10);

        final Image editIcon = new Image("/icons/edit.png");
        final JFXButton editPairButton = new JFXButton("", new ImageView(editIcon));
        editPairButton.setOnAction(e -> {
            addPairDialog.showFromPair(pair);
            this.updateContent();
        });
        rootPane.add(editPairButton, 3, 0);

        final Image deleteIcon = new Image("/icons/delete.png");
        final JFXButton deletePairButton = new JFXButton("", new ImageView(deleteIcon));
        deletePairButton.setOnMouseClicked(e -> {
            deletePairDialog.show(container, pair);
        });
        rootPane.add(deletePairButton, 4, 0);

        rootPane.add(new Label("Предмет:"), 0, 0);
        rootPane.add(new Label(pair.getSubject()), 1, 0, 2, 1);

        rootPane.add(new Label("Преподаватель:"), 0, 1);
        rootPane.add(new Label(pair.getTeacher().formatFIO()), 1, 1, 2, 1);

        rootPane.add(new Label("Аудитория:"), 0, 2);
        final Label auditoriumLabel = new Label(pair.getAuditorium().getName());
        auditoriumLabel.underlineProperty().setValue(true);
        auditoriumLabel.cursorProperty().setValue(Cursor.HAND);
        auditoriumLabel.setOnMouseClicked(e -> {
            viewAuditoriumDialog.show(container, pair.getAuditorium());
        });
        rootPane.add(auditoriumLabel, 1, 2, 2, 1);

        rootPane.add(new Label("День недели:"), 0, 3);
        rootPane.add(new Label(DateService.daysOfWeek.get(pair.getDayOfTheWeek() - 1)),
                1, 3, 2, 1);

        rootPane.add(new Label("Продолжительность:"), 0, 4);
        rootPane.add(new Label(pair.formatPairTime()), 1, 4, 2, 1);

        rootPane.add(cancelPairButton, 0, 5, 2, 1);
    }

    private void cancelPair() {
        Pair cancel = pair.cancelPair();
        pairService.save(cancel);
    }

    @Nullable
    public JFXDialog getDialog() {
        return dialog;
    }
}
