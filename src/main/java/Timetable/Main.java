package Timetable;

import Timetable.model.Auditorium;
import Timetable.model.Dialogs.AddAuditoriumDialog;
import Timetable.model.Dialogs.AddPairDialog;
import Timetable.model.Dialogs.AddPeopleUnionDialog;
import Timetable.model.Dialogs.AddUserDialog;
import Timetable.model.Dialogs.ViewDialogs.ViewAuditoriumDialog;
import Timetable.model.Dialogs.ViewDialogs.ViewPairDialog;
import Timetable.model.Pair;
import Timetable.model.Parameters.StyleParameter;
import Timetable.model.PeopleUnion;
import Timetable.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Lazy
@EntityScan
@SpringBootApplication(scanBasePackages = "Timetable")
@EnableJpaRepositories("Timetable.repositories")
public class Main extends AbstractJavaFxApplicationSupport {

    private HBox classesSettings;
    private VBox classes;

    private StackPane modes;
    private GridPane classesPane;
    private GridPane auditoriumPane;

    private ChoiceBox<PeopleUnion> courseSelect;
    private ChoiceBox<PeopleUnion> streamSelect;

    @Autowired
    private ViewPairDialog viewPairDialog;
    @Autowired
    private ViewAuditoriumDialog viewAuditoriumDialog;

    @Autowired
    private AddPairDialog addPairDialog;
    @Autowired
    private AddAuditoriumDialog addAuditoriumDialog;

    @Autowired
    private DatabaseService databaseService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuditoriumService auditoriumService;
    @Autowired
    private PairService pairService;
    @Autowired
    private PeopleUnionTypeService peopleUnionTypeService;
    @Autowired
    private PeopleUnionService peopleUnionService;

    @Override
    public void start(@NonNull final Stage primaryStage) throws Exception {
        databaseService.appInit();
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Timetable");

        final StackPane mainStack = new StackPane();
        final HBox root_pane = new HBox();

        modes = new StackPane();

//        System.out.println(userRepository.getOne(1).formatFIO());

        initiateClassesWindow();
        initiateAuditoriumWindow();
//        addAuditoriumWindow();

        VBox menu = sidePane();
        root_pane.getChildren().add(menu);
        //mainStack.getChildren().add(root_pane);

        //modes.getChildren().add(classes);


        classes.toBack();
        classes.setVisible(false);

        root_pane.getChildren().add(modes);
        HBox.setHgrow(modes, Priority.ALWAYS);
        HBox.setHgrow(root_pane, Priority.ALWAYS);

        //root_pane.setRight(classes);
        mainStack.getChildren().add(root_pane);
        //menu.toFront();

        // Создание меню
        final Menu addMenu = new Menu("Вставка");

        final MenuItem addUser = new MenuItem("Создать пользователя");
        final MenuItem addAuditorium = new MenuItem("Создать аудиторию");
        final MenuItem addPair = new MenuItem("Создать занятие");
        final MenuItem addPeopleUnion = new MenuItem("Создать группу");

        addUser.setOnAction((@NonNull final ActionEvent actionEvent) ->
                new AddUserDialog(userService, peopleUnionService).show());
        addAuditorium.setOnAction((@NonNull final ActionEvent actionEvent) -> {
            new AddAuditoriumDialog(auditoriumService).show();
            fillAuditoriumWindow(auditoriumService.getAuditoriums());
        });
        addPair.setOnAction((@NonNull final ActionEvent actionEvent) -> {
            addPairDialog.show();
            updateClasses();
        });

        //var AddPeopleUnion = FXMLLoader.load(getClass().getResource("addPeopleUnion.fxml"));
        addPeopleUnion.setOnAction((e) -> new AddPeopleUnionDialog(peopleUnionTypeService, peopleUnionService).show());

        // There were multiple .getItems() calls
        addMenu.getItems().addAll(List.of(addUser, addAuditorium, addPair, addPeopleUnion));

        final MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(addMenu);

        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(menuBar, mainStack);
        VBox.setVgrow(mainStack, Priority.ALWAYS);

        final Scene scene = new Scene(mainBox, 1000, 700);
        scene.getStylesheets().addAll(List.of("styles.css", "styles/classes.css", "styles/auditoriums.css"));

//        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.show();
    }


    @NonNull
    private VBox sidePane() {
        final VBox vbox = new VBox();
        vbox.setPrefWidth(200);
        vbox.setMinWidth(200);
        //vbox.setStyle("-fx-background-color: red");
        final List<String> names = Arrays.asList("Аудитории", "Занятия");
        final List<Pane> panes = Arrays.asList(auditoriumPane, classes);
        for (int i = 0; i < 2; ++i) {
            vbox.getChildren().add(boxItem(String.valueOf(i), names.get(i), panes.get(i), panes));
        }
        vbox.setStyle("-fx-background-color: #2d3041");
        return vbox;
    }

    @NonNull
    private HBox boxItem(@NonNull final String item,
                         @NonNull final String buttonName,
                         @NonNull final Pane to_pane,
                         @NonNull final List<Pane> panes) {
        Image image = new Image(Main.class.getResourceAsStream("/icons/" + item + ".png"));
        ImageView view = new ImageView(image);
        Button button = new Button();
        button.setPrefSize(200, 100);
        button.setGraphic(view);
        button.setText(buttonName);
        button.setOnAction(actionEvent -> {
            for (var pane : panes) {
                if (pane != to_pane) {
                    pane.toBack();
                    pane.setVisible(false);
                }
            }
            to_pane.toFront();
            to_pane.setVisible(true);
            //modes.getChildren().add(to_pane);
        });
        //button.setStyle("-fx-graphic-text-gap:white");
        button.setStyle("-fx-background-color: #212121; -fx-text-fill:white");
        Pane paneIndicator = new Pane();
        paneIndicator.setPrefSize(10, 100);
        paneIndicator.setStyle("-fx-background-color: #212121");
        menuDecorator(button, paneIndicator);
        return new HBox(paneIndicator, button);
    }

    private void menuDecorator(@NonNull final Button button, @NonNull final Pane pane) {
        button.setOnMouseEntered(value -> {
            button.setStyle("-fx-background-color: black; -fx-text-fill:white");
            pane.setStyle("-fx-background-color: white");
        });
        button.setOnMouseExited(value -> {
            button.setStyle("-fx-background-color: #212121; -fx-text-fill:white");
            pane.setStyle("-fx-background-color: #212121");
        });
    }

    private void initiateAuditoriumWindow() {
        auditoriumPane = new GridPane();
        auditoriumPane.setPadding(new Insets(15, 15, 15, 15));
        auditoriumPane.setHgap(30);
        auditoriumPane.setVgap(30);
        fillAuditoriumWindow(auditoriumService.getAuditoriums());
        modes.getChildren().add(auditoriumPane); // Try to keep functions as clean in a haskell way as possible
    }

    private void fillAuditoriumWindow(@NonNull final List<Auditorium> auditoriums) {
        auditoriumPane.getChildren().clear();
        auditoriumPane.getColumnConstraints().clear();
        int COL_NUM = 4;
        for (int i = 0; i < COL_NUM; ++i) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            columnConstraints.setHalignment(HPos.CENTER);
            auditoriumPane.getColumnConstraints().add(columnConstraints);
        }
        for (int i = 0; i < auditoriums.size(); ++i) {
            // Never use var in java code, keep everything as clear as possible
            final Auditorium auditorium = auditoriums.get(i);
            final Pane pane = getAuditoriumPane(modes, auditorium);
            pane.setMaxWidth(Double.MAX_VALUE);
            auditoriumPane.add(pane, i % COL_NUM, i / COL_NUM);
        }
    }


    private void initiateClassesWindow() {

        classes = new VBox();
        classes.toBack();
        classes.setStyle("-fx-background-color: white");

        final ObservableList<PeopleUnion> courses = peopleUnionService
                .findAllByTypeEquals(peopleUnionTypeService.checkOrCreateType("Курс"));
        final ObservableList<PeopleUnion> streams = peopleUnionService
                .findAllByTypeEquals(peopleUnionTypeService.checkOrCreateType("Поток"));

        classesSettings = new HBox();
        classesSettings.setPadding(new Insets(25, 10, 0, 10));
        classesSettings.setAlignment(Pos.CENTER_LEFT);

        classesSettings.spacingProperty().setValue(10);

        Label courseLabel = new Label("Курс:");
        Label streamLabel = new Label("Поток:");

        courseSelect = new ChoiceBox<>(courses);
        if (!courses.isEmpty()) {
            courseSelect.setValue(courses.get(0));
        }
        streamSelect = new ChoiceBox<>(streams);
        if (!streams.isEmpty()) {
            streamSelect.setValue(streams.get(0));
        }

        courseSelect.setOnAction(e -> {
//            streams = peopleUnionService.findAllByTypeEquals(courseSelect.getValue());
            final ObservableList<PeopleUnion> currentStreams = FXCollections
                    .observableArrayList(courseSelect.getValue().getChildrenUnions());
            streamSelect.setItems(currentStreams);
            if (!currentStreams.isEmpty()) {
                streamSelect.setValue(currentStreams.get(0));
            }
        });

        streamSelect.setOnAction(e -> updateClasses());

        classesSettings.getChildren().addAll(courseLabel, courseSelect, new Label("      "),
                streamLabel, streamSelect);

        classes.getChildren().add(classesSettings);

        addClassesWindow();

        ScrollPane classesScrollPane = new ScrollPane();
        classesScrollPane.setContent(classes);
        classesScrollPane.setFitToWidth(true);

        modes.getChildren().add(classesScrollPane);
    }

    private void updateClasses() {
        classes.getChildren().clear();
        classes.getChildren().add(classesSettings);
        addClassesWindow();
    }

    private void addClassesWindow() {
        /*
        Функция, создающее окно, вызывающееся по кнопке "Занятия"
         */

        if (classes != null && classesPane != null) {
            classes.getChildren().remove(classesPane);
        }

        classesPane = getClassesPane();
        classes.getChildren().add(classesPane);
        HBox.setHgrow(classesPane, Priority.ALWAYS);

    }

    @NonNull
    private GridPane getClassesPane() {

        classesPane = new GridPane();

        classesPane.getStyleClass().add("classes-grid");

        int mode = 0;

        // Always true, consider removing
        if (mode == 0) {
            final PeopleUnion fatherPeopleUnion = streamSelect.getValue();
            if (fatherPeopleUnion == null) {
                return classesPane;
            }
            final List<PeopleUnion> groups = fatherPeopleUnion.getChildrenUnions();
            final int groupsCount = groups.size();

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            classesPane.getColumnConstraints().add(columnConstraints);

            for (PeopleUnion ignored : groups) {
                classesPane.getColumnConstraints().add(columnConstraints);
            }

            final ObservableList<PeopleUnion> groupsTmp = FXCollections.observableArrayList(groups);
            groupsTmp.add(fatherPeopleUnion);

            GridPaneService.addToGridPane(classesPane, new Label(" "), 0, 0);
            final ObservableList<ObservableList<Pair>> week = pairService.getDefaultWeekForStream(groupsTmp);
            if (week.isEmpty()) {
                return classesPane;
            }
            int currentRow = 0;
            final StyleParameter labelStyle = new StyleParameter();
            labelStyle.setLabelStyle("white-text");
            for (int dayIndex = 0; dayIndex < DateService.daysOfWeek.size(); ++dayIndex) {
                final ObservableList<Pair> currentDayPairs = week.get(dayIndex);
                final String dayName = DateService.daysOfWeek.get(dayIndex);

                // Записываем имя дня
                GridPaneService.fillRowEmpty(classesPane, currentRow, groupsCount, new
                        StyleParameter("grey"));
                GridPaneService.addToGridPane(classesPane, new Label(dayName), 0, currentRow, labelStyle);
                writeAllGroups(classesPane, groups, currentRow, 1, labelStyle);
                currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);

                if (!currentDayPairs.isEmpty()) {
                    LocalDateTime currentTime = currentDayPairs.get(0).getEndTime();
                    GridPaneService.addToGridPane(classesPane, new Label(currentDayPairs.get(0).formatPairTime()),
                            0, currentRow);
                    for (Pair pair : currentDayPairs) {
                        if (pair.getEndTime().toLocalTime().compareTo(currentTime.toLocalTime()) > 0) {
                            // Пара следующая по времени
                            currentTime = pair.getEndTime();
                            currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
                            GridPaneService.addToGridPane(classesPane, new Label(pair.formatPairTime()), 0,
                                    currentRow);
                        }

                        Label pairLabel = new Label(pair.formatPair());
                        pairLabel.setTextAlignment(TextAlignment.CENTER);
                        final StyleParameter style = new StyleParameter();
                        // No need to make it null, just initialize it before first read
                        final Pane pairPane;
                        if (pair.getGroup().equals(fatherPeopleUnion)) {
                            // Общепоточная пара
                            classesPane.getChildren().remove(classesPane.getChildren().size() -
                                    groupsCount - 1, classesPane.getChildren().size() - 1);  // Удаляю пустые поля
                            style.setLabelStyle("big");
                            pairLabel.setText(pair.formatStreamPair());
                            pairPane = GridPaneService.addToGridPane(classesPane, pairLabel, 1,
                                    currentRow, groupsCount, style);
                        } else {
                            // Пара отдельной группы
                            style.setLabelStyle("pair");
                            pairPane = GridPaneService.addToGridPane(classesPane, pairLabel,
                                    groups.indexOf(pair.getGroup()) + 1, currentRow, style);
                        }
                        pairPane.setOnMouseClicked(e -> {
                            if (e.getClickCount() >= 2) {  // On double click
                                viewPairDialog.show(modes, pair);
                                viewPairDialog.getDialog().setOnDialogClosed(skip -> updateClasses());
                            }
                        });
                    }
                }

                if (dayIndex != DateService.daysOfWeek.size() - 1) {
                    currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
                }
            }
        }
        return classesPane;
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
        info.setAlignment(Pos.CENTER);
        info.prefWidthProperty().bind(root.widthProperty());
        Label name = new Label(auditorium.getName());
        name.setMaxHeight(Double.MAX_VALUE);
        name.getStyleClass().add("auditorium-name");
        Separator separator = new Separator();
        separator.setPrefWidth(name.getPrefWidth());
        separator.getStyleClass().add("auditorium-separator");
        Label infoLabel = new Label("Test info");
        info.getChildren().addAll(name, separator, infoLabel);

        top.getChildren().addAll(imageView, info);
        HBox availability = getAuditoriumAvailability(pairService, auditorium);
        availability.prefWidthProperty().bind(root.widthProperty());
        root.getChildren().addAll(top, availability);

        root.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {  // On double click
                viewAuditoriumDialog.show(container, auditorium);
//                viewAuditoriumDialog.
            }
        });

        return root;
    }

    public HBox getAuditoriumAvailability(PairService pairService, Auditorium auditorium) {
        HBox root = new HBox();
        root.setSpacing(3);
        root.setPadding(new Insets(0, 30, 0,30));
        ObservableList<Pair> pairs = pairService.getAuditoriumPairs(auditorium);

        for (int dayIndex = 0; dayIndex < DateService.daysOfWeek.size(); ++dayIndex) {
            String dayName = DateService.daysOfWeek.get(dayIndex);

            VBox node = new VBox();
            VBox availability = new VBox();
            LocalTime endTime = LocalTime.of(21, 0);
            for (LocalTime beginTime = LocalTime.of(9, 0); beginTime.compareTo(endTime) < 0;
                 beginTime = beginTime.plusHours(2)) {
                Pane pane = new Pane();
                pane.setPrefSize(10, 10);
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
            availability.prefWidthProperty().bind(root.widthProperty());
            root.getChildren().add(node);
        }
        return root;
    }

    private int increaseGridRowIndex(@NonNull final GridPane gridPane,
                                     int rowIndex,
                                     final int increaseValue, // It's always 1, consider removing it
                                     final int groupsCount) {
        for (int i = 0; i < increaseValue; ++i) {
            rowIndex += 1;
            GridPaneService.fillRowEmpty(gridPane, rowIndex, groupsCount);
        }
        return rowIndex;
    }

    private void writeAllGroups(@NonNull final GridPane gridPane,
                                @NonNull final List<PeopleUnion> groups,
                                final int rowIndex,
                                final int beginColumn,  // It's always 1, consider removing it
                                @NonNull final StyleParameter style) {
        int colIndex = beginColumn;
        for (final PeopleUnion group: groups) {
            GridPaneService.addToGridPane(gridPane, new Label(group.getName()), colIndex, rowIndex, style);
            colIndex += 1;
        }
    }

    public static void main(String[] args) {
        AbstractJavaFxApplicationSupport.launchApp(Main.class, args);
    }
}
