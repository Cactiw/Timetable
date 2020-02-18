package Application;

import classes.Auditorium;
import classes.Dialogs.AddAuditoriumDialog;
import classes.Dialogs.AddPairDialog;
import classes.Dialogs.AddPeopleUnionDialog;
import classes.Dialogs.AddUserDialog;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ResourceLoader;

import java.util.*;


@Lazy
@SpringBootApplication
public class Main extends AbstractJavaFxApplicationSupport {

    Button button;
    Stage mainStage;
    HBox classes;
    VBox mainBox, menu, auditoriumBox;
    
    StackPane modes;

    TableView<Auditorium> auditoriumTableView;
    TableView<HashMap.Entry<String, String>> auditoriumProperties;

    @Autowired
    ResourceLoader resourceLoader;



    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        mainStage = primaryStage;
        primaryStage.setTitle("Timetable");

        StackPane mainStack = new StackPane();

//        TableColumn<Auditorium, String> nameColumn = new TableColumn<>("Название");
//        nameColumn.setMinWidth(200);
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//
//        TableColumn<Auditorium, Integer> maxStudentsColumn = new TableColumn<>("Мест");
//        maxStudentsColumn.setMinWidth(200);
//        maxStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("maxStudents"));
//
////        TableColumn<Auditorium, Button> edit = new TableColumn<>("Ред.");
////        edit.setMinWidth(200);
////        edit.setCellFactory(TableCell.<Person>forTableColumn("Remove", (Person p) -> {
////            table.getItems().remove(p);
////            return p;
////        }));
//
//        auditoriumTableView = new TableView<>();
//        auditoriumTableView.setItems(Auditorium.getAuditoriums());
//        auditoriumTableView.getColumns().addAll(nameColumn, maxStudentsColumn);
//        auditoriumTableView.setPrefHeight(1000);
//        auditoriumTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)  && mouseEvent.getClickCount() == 1) {
//                    Auditorium auditorium = auditoriumTableView.getSelectionModel().getSelectedItem();
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("Название", auditorium.getName());
//                    map.put("Число мест", Integer.toString(auditorium.getMaxStudents()));
//                    auditoriumProperties.setItems(FXCollections.observableArrayList(
//                            map.entrySet()
//                    ));
//                } else if (mouseEvent.getButton().equals(MouseButton.PRIMARY)  && mouseEvent.getClickCount() == 2) {
//                }
//            }
//        });
//
//        //auditoriums.getChildren().addAll(auditoriumTableView);
//
//
//
//        classes = new HBox();
//        classes.toBack();
//        //classes.setPrefSize(100000, 100000);
//        classes.getChildren().add(new Text("Занятия"));
//        classes.setStyle("-fx-background-color: white");
//
//
//        auditoriumBox = new VBox();
//
//        HBox root_pane = new HBox();
//        menu = sidePane();
//        root_pane.getChildren().add(menu);
//        //mainStack.getChildren().add(root_pane);
//
//        modes = new StackPane();
//        //modes.getChildren().add(classes);
//
//        TextField auditoriumSearch = new TextField();
//        auditoriumSearch.setPromptText("Начните вводить для поиска");
//        auditoriumSearch.setMinWidth(300);
//        auditoriumSearch.textProperty().addListener((observable, oldValue, newValue) -> {
//                System.out.println("Searching");
//                String text = auditoriumSearch.getText();
//                SortedList<Auditorium> sortedData;
//                if (text.compareTo("") == 0) {
//                    sortedData = new SortedList<>(Auditorium.getAuditoriums());
//                } else {
//                    sortedData = new SortedList<>(Auditorium.searchAuditoriums(auditoriumSearch.getText()));
//                }
//                sortedData.comparatorProperty().bind(auditoriumTableView.comparatorProperty());
//                auditoriumTableView.setItems(sortedData);
//
//            }
//        );
//        Label auditoriumSearchLabel = new Label("Поиск:");
//        auditoriumSearchLabel.setMinWidth(50);
//        HBox auditoriumSearchBox = new HBox();
//        auditoriumSearchBox.getChildren().addAll(auditoriumSearchLabel, auditoriumSearch);
//
//        auditoriumProperties =  new TableView<>();
//        TableColumn<HashMap.Entry<String, String>, String> auditoriumPropertiesColumn1 = new TableColumn<>("Свойство");
//        auditoriumPropertiesColumn1.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getKey()));
//        TableColumn<HashMap.Entry<String, String>, String> auditoriumPropertiesColumn2 = new TableColumn<>("Значение");
//        auditoriumPropertiesColumn2.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
//        auditoriumProperties.getColumns().addAll(auditoriumPropertiesColumn1, auditoriumPropertiesColumn2);
//
//
//
//        HBox auditoriumInfo = new HBox();
//        auditoriumInfo.getChildren().addAll(auditoriumTableView, auditoriumProperties);
//
//        auditoriumBox.getChildren().addAll(auditoriumSearchBox, auditoriumInfo);
//        HBox.setHgrow(auditoriumInfo, Priority.ALWAYS);
//        VBox.setVgrow(auditoriumBox, Priority.ALWAYS);
//
//        modes.getChildren().addAll(classes, auditoriumBox);
//        classes.toBack();
//        classes.setVisible(false);
//
//        root_pane.getChildren().add(modes);
//        HBox.setHgrow(modes, Priority.ALWAYS);
//
//        //root_pane.setRight(classes);
//        mainStack.getChildren().add(root_pane);
//        //menu.toFront();

        // Создание меню
        Menu addMenu = new Menu("Вставка");

        MenuItem addUser = new MenuItem("Создать пользователя");
        MenuItem addAuditorium = new MenuItem("Создать аудиторию");
        MenuItem addPair = new MenuItem("Создать занятие");
        MenuItem addPeopleUnion = new MenuItem("Создать группу");

        addUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new AddUserDialog().show();
            }
        });
        addAuditorium.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new AddAuditoriumDialog().show();
                auditoriumTableView.setItems(Auditorium.getAuditoriums());
            }
        });
        addPair.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                new AddPairDialog().show();
            }
        });

        //var AddPeopleUnion = FXMLLoader.load(getClass().getResource("addPeopleUnion.fxml"));
        addPeopleUnion.setOnAction((e) -> {new AddPeopleUnionDialog().show();});

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
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                 //to_pane.toFront();
//                 classes.toBack();
//                 auditoriumBox.toFront();
//                 classes.toBack();
//                modes.getChildren().clear();
                for (var pane: panes) {
                    if (pane != to_pane) {
                        pane.toBack();
                        pane.setVisible(false);
                    }
                }
                to_pane.toFront();
                to_pane.setVisible(true);
                //modes.getChildren().add(to_pane);
            }
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




    public static void main(String[] args) {
        AbstractJavaFxApplicationSupport.launchApp(Main.class, args);}
}
