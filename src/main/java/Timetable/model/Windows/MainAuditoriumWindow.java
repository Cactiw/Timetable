package Timetable.model.Windows;

import Timetable.model.Auditorium;
import Timetable.model.Dialogs.ViewDialogs.ViewAuditoriumDialog;
import Timetable.model.Pair;
import Timetable.service.AuditoriumService;
import Timetable.service.DateService;
import Timetable.service.PairService;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class MainAuditoriumWindow {

    private BorderPane auditoriumPane;
    private GridPane auditoriumGridPane;

    private StackPane modes;

    private AuditoriumService auditoriumService;
    private PairService pairService;
    private ViewAuditoriumDialog viewAuditoriumDialog;

    public MainAuditoriumWindow(@NonNull final StackPane modes,
                                @NonNull final AuditoriumService auditoriumService,
                                @NonNull final PairService pairService,
                                @NonNull final ViewAuditoriumDialog viewAuditoriumDialog) {
        this.modes = modes;
        this.auditoriumService = auditoriumService;
        this.pairService = pairService;
        this.viewAuditoriumDialog = viewAuditoriumDialog;
    }

    public Pane initiateAuditoriumWindow() {
        auditoriumPane = new BorderPane();

        auditoriumGridPane = new GridPane();
        auditoriumGridPane.setPadding(new Insets(15, 30, 15, 15));
        auditoriumGridPane.setHgap(30);
        auditoriumGridPane.setVgap(30);
        fillAuditoriumWindow(auditoriumService.getAuditoriums());
        auditoriumPane.centerProperty().set(auditoriumGridPane);
        modes.getChildren().add(auditoriumPane); // Try to keep functions as clean in a haskell way as possible

        return auditoriumPane;
    }

    public void refresh() {
        fillAuditoriumWindow(auditoriumService.getAuditoriums());
    }

    private void fillAuditoriumWindow(@NonNull final List<Auditorium> auditoriums) {
        auditoriumGridPane.getChildren().clear();
        auditoriumGridPane.getColumnConstraints().clear();
        int COL_NUM = 4;
        for (int i = 0; i < COL_NUM; ++i) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            columnConstraints.setHalignment(HPos.CENTER);
            auditoriumGridPane.getColumnConstraints().add(columnConstraints);
        }
        for (int i = 0; i < auditoriums.size(); ++i) {
            // Never use var in java code, keep everything as clear as possible
            final Auditorium auditorium = auditoriums.get(i);
            final Pane pane = getAuditoriumPane(modes, auditorium);
            pane.setMaxWidth(Double.MAX_VALUE);
            auditoriumGridPane.add(pane, i % COL_NUM, i / COL_NUM);
        }
    }

    public Pane getAuditoriumPane(@NonNull final StackPane container,
                                  @NonNull final Auditorium auditorium) {
        VBox root = new VBox();
        root.getStyleClass().add("auditorium-pane");
        root.setFillWidth(true);
        root.setSpacing(15);

        HBox top = new HBox();
        top.setFillHeight(true);
        var image = new Image("auditorium.jpg");
        var imageView = new ImageView(image);
        imageView.setFitWidth(75);
        imageView.setFitHeight(75);

        var info = new VBox();
        info.setFillWidth(true);
        info.setAlignment(Pos.TOP_CENTER);
        info.prefWidthProperty().bind(root.widthProperty());
        Label name = new Label(auditorium.getName());
        name.setMaxHeight(Double.MAX_VALUE);
        name.getStyleClass().add("auditorium-name");
        Separator separator = new Separator();
        separator.setPrefWidth(name.getPrefWidth());
        separator.getStyleClass().add("auditorium-separator");
        List<Label> infoLabels = auditorium.getProperties().stream().map(
                auditoriumProperty -> new Label(auditoriumProperty.getName())).collect(Collectors.toList());
        info.getChildren().addAll(name, separator);
//        info.getChildren().addAll(infoLabels);
        VBox infoVbox = new VBox();
        infoVbox.getChildren().addAll(infoLabels);
        infoVbox.setAlignment(Pos.CENTER);

        top.getChildren().addAll(imageView, info);
        HBox availability = getAuditoriumAvailability(pairService, auditorium);

        GridPane lower = new GridPane();
        lower.add(availability, 0, 0);
        lower.add(new Separator(Orientation.VERTICAL), 1, 0, 1, 2);
        lower.add(infoVbox, 1, 0, 2, 2);
        lower.prefWidthProperty().bind(root.widthProperty());
        lower.setPadding(new Insets(0, 10, 0, 10));
        ColumnConstraints grow = new ColumnConstraints();
        grow.setHgrow(Priority.ALWAYS);
        ColumnConstraints stay = new ColumnConstraints();
        stay.setHgrow(Priority.SOMETIMES);
        lower.getColumnConstraints().addAll(grow, stay, grow);

        root.getChildren().addAll(top, lower);

        root.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {  // On double click
                viewAuditoriumDialog.show(container, auditorium);
                viewAuditoriumDialog.getDialog().setOnDialogClosed(skip -> this.fillAuditoriumWindow(
                        auditoriumService.getAuditoriums()));
            }
        });
        return root;
    }

    public HBox getAuditoriumAvailability(PairService pairService, Auditorium auditorium) {
        HBox root = new HBox();
        root.setSpacing(3);
        root.setPadding(new Insets(0, 10, 0,0));
        ObservableList<Pair> pairs = pairService.getAuditoriumPairs(auditorium);

        for (int dayIndex = 0; dayIndex < DateService.daysOfWeek.size(); ++dayIndex) {
            String dayName = DateService.daysOfWeek.get(dayIndex);

            VBox node = new VBox();
            VBox availability = new VBox();
            LocalTime endTime = LocalTime.of(21, 0);
            for (LocalTime beginTime = LocalTime.of(9, 0); beginTime.compareTo(endTime) < 0;
                 beginTime = beginTime.plusHours(2)) {
                Pane pane = new Pane();
                pane.setPrefSize(11, 8);
                LocalTime finalBeginTime = beginTime;
                int finalDayIndex = dayIndex;
                pane.getStyleClass().add(pairs.filtered(
                        pair -> pairService.checkConflict(pair, finalDayIndex + 1, finalBeginTime, endTime)).size() > 0 ?
                        "auditorium-busy": "auditorium-free");
                availability.getChildren().add(pane);
            }
            Label dayLabel = new Label(dayName.substring(0, 1));
            dayLabel.alignmentProperty().set(Pos.CENTER);
            dayLabel.prefWidthProperty().bind(node.widthProperty());
            node.getChildren().addAll(availability, dayLabel);
//            availability.prefWidthProperty().bind(root.widthProperty());
            root.getChildren().add(node);
        }
        return root;
    }
}
