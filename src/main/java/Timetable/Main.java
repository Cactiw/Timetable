package Timetable;

import Timetable.model.Auditorium;
import Timetable.model.Dialogs.AddAuditoriumDialog;
import Timetable.model.Dialogs.AddPairDialog;
import Timetable.model.Dialogs.AddPeopleUnionDialog;
import Timetable.model.Dialogs.AddUserDialog;
import Timetable.model.Dialogs.ViewDialogs.ViewPairDialog;
import Timetable.model.Pair;
import Timetable.model.Parameters.StyleParameter;
import Timetable.model.PeopleUnion;
import Timetable.model.Properties.BorderProperties;
import Timetable.service.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import Timetable.repositories.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static javafx.scene.control.PopupControl.USE_COMPUTED_SIZE;


@Lazy
@EntityScan
@SpringBootApplication(scanBasePackages = "Timetable")
@EnableJpaRepositories("Timetable.repositories")
public class Main extends AbstractJavaFxApplicationSupport {

    Button button;
    Stage mainStage;
    HBox classesSettings;
    VBox mainBox, menu, auditoriumBox, classes;

    Label courseLabel, streamLabel;

    StackPane modes;
    GridPane classesPane;
    ScrollPane classesScrollPane;

    ChoiceBox<PeopleUnion> courseSelect, streamSelect;

    TableView<Auditorium> auditoriumTableView;
    TableView<HashMap.Entry<String, String>> auditoriumProperties;

    @Autowired
    ViewPairDialog viewPairDialog;

    @Autowired
    AddPairDialog addPairDialog;

    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    AuditoriumService auditoriumService;
    @Autowired
    PairService pairService;
    @Autowired
    PeopleUnionTypeService peopleUnionTypeService;
    @Autowired
    PeopleUnionService peopleUnionService;
    
    @Autowired
    GridPaneService gridPaneService;

    @Autowired
    EntityManager entityManager;

    private ArrayList<String> defaultTypes = new ArrayList<String>(List.of("Курс", "Поток", "Группа"));


    @Override
    public void start(Stage primaryStage) throws Exception {
        databaseInit();
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        mainStage = primaryStage;
        primaryStage.setTitle("Timetable");

        StackPane mainStack = new StackPane();
        HBox root_pane = new HBox();

        modes = new StackPane();

//        System.out.println(userRepository.getOne(1).formatFIO());

        initiateClassesWindow();
        addAuditoriumWindow();

        menu = sidePane();
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
        Menu addMenu = new Menu("Вставка");

        MenuItem addUser = new MenuItem("Создать пользователя");
        MenuItem addAuditorium = new MenuItem("Создать аудиторию");
        MenuItem addPair = new MenuItem("Создать занятие");
        MenuItem addPeopleUnion = new MenuItem("Создать группу");

        addUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new AddUserDialog(userService, peopleUnionService).show();
            }
        });
        addAuditorium.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new AddAuditoriumDialog(auditoriumService).show();
                auditoriumTableView.setItems(auditoriumService.getAuditoriums());
            }
        });
        addPair.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addPairDialog.show();
                updateClasses();
            }
        });

        //var AddPeopleUnion = FXMLLoader.load(getClass().getResource("addPeopleUnion.fxml"));
        addPeopleUnion.setOnAction((e) -> {
            new AddPeopleUnionDialog(peopleUnionTypeService, peopleUnionService).show();
        });

        addMenu.getItems().add(addUser);
        addMenu.getItems().add(addAuditorium);
        addMenu.getItems().add(addPair);
        addMenu.getItems().add(addPeopleUnion);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(addMenu);

        mainBox = new VBox();
        mainBox.getChildren().addAll(menuBar, mainStack);
        VBox.setVgrow(mainStack, Priority.ALWAYS);

        Scene scene = new Scene(mainBox, 1000, 700);
        scene.getStylesheets().add("styles.css");
        scene.getStylesheets().add("classes.css");

//        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.show();
    }


    private VBox sidePane() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(200);
        vbox.setMinWidth(200);
        //vbox.setStyle("-fx-background-color: red");
        List<String> names = Arrays.asList("Аудитории", "Занятия");
        List<Pane> panes = Arrays.asList(auditoriumBox, classes);
        for (int i = 0; i < 2; ++i) {
            vbox.getChildren().add(boxItem(String.valueOf(i), names.get(i), panes.get(i), panes));
        }
        vbox.setStyle("-fx-background-color: #2d3041");
        return vbox;
    }

    private HBox boxItem(String item, String buttonName, Pane to_pane, List<Pane> panes) {
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
        HBox box = new HBox(paneIndicator, button);
        return box;
    }

    private void menuDecorator(Button button, Pane pane) {
        button.setOnMouseEntered(value -> {
            button.setStyle("-fx-background-color: black; -fx-text-fill:white");
            pane.setStyle("-fx-background-color: white");
        });
        button.setOnMouseExited(value -> {
            button.setStyle("-fx-background-color: #212121; -fx-text-fill:white");
            pane.setStyle("-fx-background-color: #212121");
        });
    }

    private void addAuditoriumWindow() {
        /*
         * Функция, создающее окно, вызывающееся по кнопке "Аудитории"
         * */
        auditoriumBox = new VBox();

        TableColumn<Auditorium, String> nameColumn = new TableColumn<>("Название");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Auditorium, Integer> maxStudentsColumn = new TableColumn<>("Мест");
        maxStudentsColumn.setMinWidth(200);
        maxStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("maxStudents"));

//        TableColumn<Auditorium, Button> edit = new TableColumn<>("Ред.");
//        edit.setMinWidth(200);
//        edit.setCellFactory(TableCell.<Person>forTableColumn("Remove", (Person p) -> {
//            table.getItems().remove(p);
//            return p;
//        }));

        auditoriumTableView = new TableView<>();
        auditoriumTableView.setItems(auditoriumService.getAuditoriums());
        auditoriumTableView.getColumns().addAll(nameColumn, maxStudentsColumn);
        auditoriumTableView.setPrefHeight(1000);
        auditoriumTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
                    Auditorium auditorium = auditoriumTableView.getSelectionModel().getSelectedItem();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Название", auditorium.getName());
                    map.put("Число мест", Integer.toString(auditorium.getMaxStudents()));
                    auditoriumProperties.setItems(FXCollections.observableArrayList(
                            map.entrySet()
                    ));
                } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                }
            }
        });

        //auditoriums.getChildren().addAll(auditoriumTableView);

        TextField auditoriumSearch = new TextField();
        auditoriumSearch.setPromptText("Начните вводить для поиска");
        auditoriumSearch.setMinWidth(300);
        auditoriumSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                    System.out.println("Searching");
                    String text = auditoriumSearch.getText();
                    SortedList<Auditorium> sortedData;
                    if (text.compareTo("") == 0) {
                        sortedData = new SortedList<>(auditoriumService.getAuditoriums());
                    } else {
                        sortedData = new SortedList<>(auditoriumService.searchAuditoriums(auditoriumSearch.getText()));
                    }
                    sortedData.comparatorProperty().bind(auditoriumTableView.comparatorProperty());
                    auditoriumTableView.setItems(sortedData);

                }
        );
        Label auditoriumSearchLabel = new Label("Поиск:");
        auditoriumSearchLabel.setMinWidth(50);
        auditoriumSearchLabel.setAlignment(Pos.CENTER);
        HBox auditoriumSearchBox = new HBox();
        auditoriumSearchBox.getChildren().addAll(auditoriumSearchLabel, auditoriumSearch);
        HBox.setHgrow(auditoriumSearchBox, Priority.ALWAYS);

        auditoriumProperties = new TableView<>();
        TableColumn<HashMap.Entry<String, String>, String> auditoriumPropertiesColumn1 =
                new TableColumn<>("Свойство");
        auditoriumPropertiesColumn1.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue().getKey()));
        TableColumn<HashMap.Entry<String, String>, String> auditoriumPropertiesColumn2 =
                new TableColumn<>("Значение");
        auditoriumPropertiesColumn2.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
        auditoriumProperties.getColumns().addAll(auditoriumPropertiesColumn1, auditoriumPropertiesColumn2);
        auditoriumProperties.setPrefWidth(2000);
        auditoriumProperties.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        auditoriumProperties.setPrefWidth(Region.USE_COMPUTED_SIZE);
        auditoriumProperties.setPrefHeight(Region.USE_COMPUTED_SIZE);


        HBox auditoriumInfo = new HBox();
        auditoriumInfo.getChildren().addAll(auditoriumTableView, auditoriumProperties);

        auditoriumBox.getChildren().addAll(auditoriumSearchBox, auditoriumInfo);
        HBox.setHgrow(auditoriumInfo, Priority.ALWAYS);
        VBox.setVgrow(auditoriumBox, Priority.ALWAYS);

        modes.getChildren().add(auditoriumBox);
    }


    private void initiateClassesWindow() {

        classes = new VBox();
        classes.toBack();
        classes.setStyle("-fx-background-color: white");

        var courses = peopleUnionService.findAllByTypeEquals(peopleUnionTypeService.checkOrCreateType("Курс"));
        var streams = peopleUnionService.findAllByTypeEquals(peopleUnionTypeService.checkOrCreateType("Поток"));

        classesSettings = new HBox();
        classesSettings.setPadding(new Insets(25, 10, 0, 10));
        classesSettings.setAlignment(Pos.CENTER_LEFT);

        classesSettings.spacingProperty().setValue(10);

        courseLabel = new Label("Курс:");
        streamLabel = new Label("Поток:");

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
            var currentStreams = FXCollections.observableArrayList(courseSelect.getValue().getChildrenUnions());
            streamSelect.setItems(currentStreams);
            if (!currentStreams.isEmpty()) {
                streamSelect.setValue(currentStreams.get(0));
            }
        });

        streamSelect.setOnAction(e -> {
            updateClasses();
        });

        classesSettings.getChildren().addAll(courseLabel, courseSelect, new Label("      "),
                streamLabel, streamSelect);

        classes.getChildren().add(classesSettings);

        addClassesWindow();

        classesScrollPane = new ScrollPane();
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

    private GridPane getClassesPane() {

        classesPane = new GridPane();

        classesPane.getStyleClass().add("classes-grid");

        int mode = 0;
        var days = Arrays.asList("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье");

        if (mode == 0) {
            var fatherPeopleUnion = streamSelect.getValue();
            var groups = fatherPeopleUnion.getChildrenUnions();
            var groupsCount = groups.size();

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            classesPane.getColumnConstraints().add(columnConstraints);

            for (int groupIndex = 0; groupIndex < groups.size(); ++groupIndex) {
                var group = groups.get(groupIndex);
                classesPane.getColumnConstraints().add(columnConstraints);
            }

            var groupsTmp = FXCollections.observableArrayList(groups);
            groupsTmp.add(fatherPeopleUnion);

            GridPaneService.addToGridPane(classesPane, new Label(" "), 0, 0);
            var week = pairService.getDefaultWeekForStream(groupsTmp);
            if (week.isEmpty()) {
                return classesPane;
            }
            int currentRow = 0;
            var labelStyle = new StyleParameter();
            labelStyle.setLabelStyle("white-text");
            for (int dayIndex = 0; dayIndex < days.size(); ++dayIndex) {
                var currentDayPairs = week.get(dayIndex);
                var dayName = days.get(dayIndex);

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
                    for (int pairIndex = 0; pairIndex < currentDayPairs.size(); ++pairIndex) {
                        var pair = currentDayPairs.get(pairIndex);
                        if (pair.getEndTime().toLocalTime().compareTo(currentTime.toLocalTime()) > 0) {
                            // Пара следующая по времени
                            currentTime = pair.getEndTime();
                            currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
                            GridPaneService.addToGridPane(classesPane, new Label(pair.formatPairTime()), 0,
                                    currentRow);
                        }

                        Label pairLabel = new Label(pair.formatPair());
                        pairLabel.setTextAlignment(TextAlignment.CENTER);
                        var style = new StyleParameter();
                        Pane pairPane = null;
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
                                updateClasses();
                            }
                        });
                    }
                }

                if (dayIndex != days.size() - 1) {
                    currentRow = increaseGridRowIndex(classesPane, currentRow, 1, groupsCount);
                }
            }
        }
        return classesPane;
    }

    private int increaseGridRowIndex(GridPane gridPane, int rowIndex, int increaseValue, int groupsCount) {
        for (int i = 0; i < increaseValue; ++i) {
            rowIndex += 1;
            GridPaneService.fillRowEmpty(gridPane, rowIndex, groupsCount);
        }
        return rowIndex;
    }

    private void writeAllGroups(GridPane gridPane, List<PeopleUnion> groups, int rowIndex, int beginColumn,
                                StyleParameter style) {
        int colIndex = beginColumn;
        for (var group: groups) {
            GridPaneService.addToGridPane(gridPane, new Label(group.getName()), colIndex, rowIndex, style);
            colIndex += 1;
        }
    }

    private void databaseInit() {
        peopleUnionTypeService.createListOfDefaultTypes(defaultTypes);
    }


    public static void main(String[] args) {
        AbstractJavaFxApplicationSupport.launchApp(Main.class, args);
    }
}
