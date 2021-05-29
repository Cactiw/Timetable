package Timetable.model.Windows;

import Timetable.model.Auditorium;
import Timetable.model.AuditoriumProperty;
import Timetable.model.Dialogs.ViewDialogs.ViewAuditoriumDialog;
import Timetable.model.Pair;
import Timetable.service.AuditoriumPropertyService;
import Timetable.service.AuditoriumService;
import Timetable.service.DateService;
import Timetable.service.PairService;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.springframework.lang.NonNull;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainAuditoriumWindow {

    private BorderPane auditoriumPane;
    private GridPane auditoriumGridPane;

    private StackPane modes;

    private TextField nameFilterField;
    private TextField maxStudentsFilterField;
    List<CheckBox> propertyFilterCheckBoxes;

    private AuditoriumService auditoriumService;
    private AuditoriumPropertyService auditoriumPropertyService;
    private PairService pairService;
    private ViewAuditoriumDialog viewAuditoriumDialog;

    private ScrollPane auditoriumScrollPane;

    public MainAuditoriumWindow(@NonNull final StackPane modes,
                                @NonNull final AuditoriumService auditoriumService,
                                @NonNull final AuditoriumPropertyService auditoriumPropertyService,
                                @NonNull final PairService pairService,
                                @NonNull final ViewAuditoriumDialog viewAuditoriumDialog) {
        this.modes = modes;
        this.auditoriumService = auditoriumService;
        this.auditoriumPropertyService = auditoriumPropertyService;
        this.pairService = pairService;
        this.viewAuditoriumDialog = viewAuditoriumDialog;
    }

    public Pane initiateAuditoriumWindow() {
        auditoriumPane = new BorderPane();

        auditoriumPane.rightProperty().set(getAuditoriumFilters());

        auditoriumGridPane = new GridPane();
        auditoriumGridPane.setPadding(new Insets(15, 30, 15, 15));
        auditoriumGridPane.setHgap(30);
        auditoriumGridPane.setVgap(30);
        fillAuditoriumWindow(auditoriumService.getAuditoriums());

        auditoriumScrollPane = new ScrollPane();
        auditoriumScrollPane.setFitToWidth(true);
        auditoriumScrollPane.setContent(auditoriumGridPane);
        auditoriumPane.centerProperty().set(auditoriumScrollPane);

        modes.getChildren().add(auditoriumPane); // Try to keep functions as clean in a haskell way as possible

        return auditoriumPane;
    }

    public void refresh() {
        fillAuditoriumWindow(filterAuditoriums());
    }

    private void onChanged(@NonNull Observable observable) {refresh();}

    private List<Auditorium> filterAuditoriums() {
        int maxStudents = 0;
        try {
            maxStudents = Integer.parseInt(maxStudentsFilterField.getText());
        } catch (NumberFormatException ignored) {}
        return auditoriumService.filterAuditoriums(nameFilterField.getText(),
                maxStudents,
                Set.copyOf(propertyFilterCheckBoxes.stream().filter(CheckBox::isSelected).map(checkBox ->
                        (AuditoriumProperty)checkBox.getUserData()).collect(Collectors.toList())));
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
            final Pane pane = auditoriumService.getAuditoriumPane(modes, auditorium);
            pane.setOnMouseClicked(e -> {
                if (e.getClickCount() >= 2) {  // On double click
                    viewAuditoriumDialog.show(modes, auditorium);
                    viewAuditoriumDialog.getDialog().setOnDialogClosed(skip -> refresh());
                }
            });
            pane.setMaxWidth(Double.MAX_VALUE);
            auditoriumGridPane.add(pane, i % COL_NUM, i / COL_NUM);
        }
    }

    @NonNull private Pane getAuditoriumFilters() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER_LEFT);
        root.setSpacing(5);
        root.getStyleClass().add("auditorium-filters");

        Label header = new Label("Фильтры");
        header.getStyleClass().add("auditorium-filter-name");
        Separator separator = new Separator();

        Label nameLabel = new Label("Название:");
        nameFilterField = new TextField();
        nameFilterField.setPromptText("Поиск по названию");
        nameFilterField.textProperty().addListener(this::onChanged);

        Pane empty = new Pane();
        Label maxStudentsLabel = new Label("Вместимость не менее:");
        maxStudentsFilterField = new TextField();
        maxStudentsFilterField.setPromptText("Вместимость студентов");
        maxStudentsFilterField.textProperty().addListener(this::onChanged);

        Separator separator2 = new Separator();
        propertyFilterCheckBoxes = new LinkedList<>();

        for (AuditoriumProperty property: auditoriumPropertyService.findAll()) {
            CheckBox checkBox = new CheckBox();
            checkBox.setUserData(property);
            checkBox.setText(property.getName());
            checkBox.selectedProperty().addListener(this::onChanged);

            propertyFilterCheckBoxes.add(checkBox);
        }

        root.getChildren().addAll(header, separator, nameLabel, nameFilterField, empty, maxStudentsLabel,
                maxStudentsFilterField, separator2);
        root.getChildren().addAll(propertyFilterCheckBoxes);
        return root;
    }
}
