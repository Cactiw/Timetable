package Timetable;

import Timetable.model.Auditorium;
import Timetable.model.Dialogs.AddAuditoriumDialog;
import Timetable.model.Dialogs.AddPairDialog;
import Timetable.model.Dialogs.AddPeopleUnionDialog;
import Timetable.model.Dialogs.AddUserDialog;
import Timetable.service.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import Timetable.repositories.UserRepository;

import javax.persistence.EntityManager;
import java.util.*;


@Lazy
@EntityScan
@SpringBootApplication(scanBasePackages = "Timetable")
@EnableJpaRepositories("Timetable.repositories")
public class Main extends AbstractJavaFxApplicationSupport {

    Button button;
    Stage mainStage;
    HBox classes;
    VBox mainBox, menu, auditoriumBox;
    
    StackPane modes;
    GridPane classesPane;

    TableView<Auditorium> auditoriumTableView;
    TableView<HashMap.Entry<String, String>> auditoriumProperties;

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
    EntityManager entityManager;

    private ArrayList<String> defaultTypes = new ArrayList<String>(List.of("Курс", "Поток", "Группа"));


    @Override
    public void start(Stage primaryStage) throws Exception{
        databaseInit();
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        mainStage = primaryStage;
        primaryStage.setTitle("Timetable");

        StackPane mainStack = new StackPane();
        HBox root_pane = new HBox();

        modes = new StackPane();

//        System.out.println(userRepository.getOne(1).formatFIO());

        addClassesWindow();
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
                new AddPairDialog(userService, auditoriumService, pairService, peopleUnionService).show();
            }
        });

        //var AddPeopleUnion = FXMLLoader.load(getClass().getResource("addPeopleUnion.fxml"));
        addPeopleUnion.setOnAction((e) -> {new AddPeopleUnionDialog(peopleUnionTypeService, peopleUnionService).show();});

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
        scene.getStylesheets().add(resourceLoader.getResource("styles.css").getInputStream().toString());
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.show();
    }



    private VBox sidePane() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(200);
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
            for (var pane: panes) {
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
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)  && mouseEvent.getClickCount() == 1) {
                    Auditorium auditorium = auditoriumTableView.getSelectionModel().getSelectedItem();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Название", auditorium.getName());
                    map.put("Число мест", Integer.toString(auditorium.getMaxStudents()));
                    auditoriumProperties.setItems(FXCollections.observableArrayList(
                            map.entrySet()
                    ));
                } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY)  && mouseEvent.getClickCount() == 2) {
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

        auditoriumProperties =  new TableView<>();
        TableColumn<HashMap.Entry<String, String>, String> auditoriumPropertiesColumn1 = new TableColumn<>("Свойство");
        auditoriumPropertiesColumn1.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKey()));
        TableColumn<HashMap.Entry<String, String>, String> auditoriumPropertiesColumn2 = new TableColumn<>("Значение");
        auditoriumPropertiesColumn2.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
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

    private void addClassesWindow() {
        /*
        Функция, создающее окно, вызывающееся по кнопке "Занятия"
         */
        classes = new HBox();
        classes.toBack();
        //classes.setPrefSize(100000, 100000);
        classes.getChildren().add(new Text("Занятия"));
        classes.setStyle("-fx-background-color: white");

        classesPane = new GridPane();
//        classesPane.setHgap(10);
        classesPane.setVgap(10);
        classesPane.setPadding(new Insets(20, 150, 10, 0));
        classesPane.setGridLinesVisible(true);

        int mode = 0;

        if (mode == 0) {
            var fatherPeopleUnion = peopleUnionService.getByName("1 поток");
            var groups = fatherPeopleUnion.getChildrenUnions();
            var groupsCount = groups.size();
            for (int i = 0; i < groupsCount; ++i) {
                classesPane.getColumnConstraints().add(new ColumnConstraints(100, 100, 10000,
                        Priority.SOMETIMES, HPos.CENTER, true));
                classesPane.add(new Label(groups.get(i).toString()), i, 0);
            }


            classes.getChildren().add(classesPane);
        }

        modes.getChildren().add(classes);
    }

    private void databaseInit() {
        peopleUnionTypeService.createListOfDefaultTypes(defaultTypes);
    }


    public static void main(String[] args) {
        AbstractJavaFxApplicationSupport.launchApp(Main.class, args);}
}
