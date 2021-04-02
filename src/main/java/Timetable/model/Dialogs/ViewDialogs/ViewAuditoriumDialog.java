package Timetable.model.Dialogs.ViewDialogs;

import Timetable.model.Auditorium;
import Timetable.model.AuditoriumProperty;
import Timetable.model.Dialogs.AddDialogs.AddAuditoriumDialog;
import Timetable.model.Dialogs.DeleteDialogs.DeleteAuditoriumDialog;
import Timetable.model.Pair;
import Timetable.service.DateService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;

@Component
public class ViewAuditoriumDialog {
    private JFXDialog dialog;
    private JFXDialogLayout content;

    private StackPane container;

    private JFXButton okButton;

    private Auditorium auditorium;

    @Autowired
    AddAuditoriumDialog addAuditoriumDialog;
    @Autowired
    ViewPairDialog viewPairDialog;
    @Autowired
    DeleteAuditoriumDialog deleteAuditoriumDialog;

    public void show(@NonNull final StackPane container,
                     @NonNull final Auditorium auditorium) {
        this.auditorium = auditorium;
        this.container = container;

        dialog = new JFXDialog();
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        content = new JFXDialogLayout();
        content.prefWidthProperty().bind(dialog.widthProperty());
        dialog.setPadding(new Insets(0, 250, 0, 50));

        updateContent();

        content.setActions(okButton);
        dialog.setContent(content);

        dialog.show(container);

    }

    private void updateContent() {
        okButton = new JFXButton("Ок");
        okButton.setPrefSize(50, 25);
        okButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: green; -fx-background-color: whitesmoke");
        okButton.setOnAction(e -> this.dialog.close());

        final GridPane rootPane = new GridPane();
        final GridPane infoPane = new GridPane();

        final Text heading = new Text(auditorium.getName());
        heading.styleProperty().setValue("-fx-font-size: 14pt;");
        content.setHeading(heading);
        content.setBody(rootPane);

        infoPane.setPadding(new Insets(10, 0, 10, 0));
        infoPane.setHgap(5);
        infoPane.prefWidthProperty().bind(dialog.widthProperty());

        final Image editIcon = new Image("/icons/edit.png");
        final JFXButton editAuditoriumButton = new JFXButton("", new ImageView(editIcon));
        editAuditoriumButton.setOnAction(e -> {
            addAuditoriumDialog.showFromAuditorium(auditorium);
            this.updateContent();
        });
        infoPane.add(editAuditoriumButton, 3, 0);

        final Image deleteIcon = new Image("/icons/delete.png");
        final JFXButton deleteAuditoriumButton = new JFXButton("", new ImageView(deleteIcon));
        deleteAuditoriumButton.setOnMouseClicked(e -> {
            deleteAuditoriumDialog.show(container, auditorium);
        });
        infoPane.add(deleteAuditoriumButton, 4, 0);

        infoPane.add(new Label("Название:"), 0, 0);
        infoPane.add(new Label(auditorium.getName()), 1, 0, 2, 1);

        infoPane.add(new Label("Вместимость:"), 0, 1);
        infoPane.add(new Label(auditorium.getMaxStudents().toString()), 1, 1, 2, 1);
        int rowIndex = 2;

        if (!auditorium.getProperties().isEmpty()) {
            infoPane.add(new Separator(), 0, rowIndex, 2, 1);
            rowIndex += 1;
            for (AuditoriumProperty property: auditorium.getProperties()) {
                infoPane.add(new Label(property.getName()), 0, rowIndex, 2, 1);
                rowIndex += 1;
            }
        }
        VBox auditoriumPairs = getAuditoriumPairs();
        auditoriumPairs.prefWidthProperty().bind(dialog.widthProperty());

        ScrollPane auditoriumPairsScrollPane = new ScrollPane();
        auditoriumPairsScrollPane.setContent(auditoriumPairs);
        auditoriumPairsScrollPane.setFitToWidth(true);
        auditoriumPairsScrollPane.setPadding(new Insets(15, 15, 15, 15));

        rootPane.add(infoPane, 0, 0);
        rootPane.add(auditoriumPairsScrollPane, 1, 0);
    }

    @NonNull
    VBox getAuditoriumPairs() {
        VBox root = new VBox();
        root.setSpacing(15);
        Label titleLabel = new Label("Занятия в аудитории");
        titleLabel.getStyleClass().add("auditorium-pair-title");
        root.getChildren().add(titleLabel);
        var pairs = new ArrayList<>(auditorium.getPairs());
        pairs.sort(Comparator.comparing(Pair::getDayOfTheWeek).thenComparing(Pair::getClearBeginTIme));

        int currentDay = -1;
        for (var pair: pairs) {
            if (pair.getDayOfTheWeek().compareTo(currentDay) != 0) {
                Label dayLabel = new Label(DateService.daysOfWeek.get(pair.getDayOfTheWeek() - 1));
                dayLabel.getStyleClass().add("auditorium-pair-title");
                root.getChildren().add(dayLabel);

                currentDay = pair.getDayOfTheWeek();
            }
            HBox pairBox = new HBox();
            pairBox.setSpacing(20);
            pairBox.setOnMouseClicked( e -> {
                if (e.getClickCount() >= 2) {  // On double click
                    viewPairDialog.show(container, pair, null);
                    this.updateContent();
                }
            });

            VBox timeBox = new VBox();
            Label beginLabel = new Label(pair.getClearBeginTIme().toString());
            Label separatorLabel = new Label("|");
            Label endLabel = new Label(pair.getClearEndTIme().toString());
            timeBox.alignmentProperty().setValue(Pos.CENTER);
            timeBox.getChildren().addAll(beginLabel, separatorLabel, endLabel);

            Label pairNameLabel = new Label(pair.getSubject());
            Label pairGroupLabel = new Label(pair.getGroup().getName());
            Label pairTeacherLabel = new Label(pair.getTeacher().formatFIO());

            pairNameLabel.getStyleClass().add("auditorium-pair-title");
            pairTeacherLabel.alignmentProperty().setValue(Pos.CENTER_RIGHT);

            Region regionCenter = new Region();
            Region regionRight = new Region();
            HBox.setHgrow(regionCenter, Priority.ALWAYS);
            HBox.setHgrow(regionRight, Priority.ALWAYS);

            pairBox.getChildren().addAll(timeBox, pairNameLabel, regionCenter, pairGroupLabel, regionRight, pairTeacherLabel);
            pairBox.getStyleClass().add("auditorium-pair-pane");
            pairBox.alignmentProperty().setValue(Pos.CENTER_LEFT);

            root.getChildren().add(pairBox);
        }

        return root;
    }

    @Nullable
    public JFXDialog getDialog() {
        return dialog;
    }
}
