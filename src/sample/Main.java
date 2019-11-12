package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.stage.Window;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;


public class Main extends Application{

    Button button;
    Stage mainStage;
    HBox auditoriums, classes;
    VBox menu;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        mainStage = primaryStage;
        primaryStage.setTitle("Timetable");

        button = new Button("Hello");
        button.setOnAction(event -> System.out.println("Hi"));

        StackPane main_layout = new StackPane();
        //main_layout.getChildren().add(button);

        auditoriums = new HBox();
        auditoriums.toFront();
        //auditoriums.setPrefSize(100000, 100000);
        auditoriums.getChildren().add(new Text("Аудитории"));
        auditoriums.setStyle("-fx-background-color: red");


        classes = new HBox();
        classes.toBack();
        //classes.setPrefSize(100000, 100000);
        classes.getChildren().add(new Text("Занятия"));
        classes.setStyle("-fx-background-color: green");



        HBox root_pane = new HBox();
        menu = sidePane();
        root_pane.getChildren().add(menu);
        //main_layout.getChildren().add(root_pane);

        var modes = new StackPane();
        modes.getChildren().add(classes);
        modes.getChildren().add(auditoriums);

        root_pane.getChildren().add(modes);
        HBox.setHgrow(modes, Priority.ALWAYS);

        //root_pane.setRight(classes);
        main_layout.getChildren().add(root_pane);
        //menu.toFront();

        Scene scene = new Scene(main_layout, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox sidePane() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(200);
        //vbox.setStyle("-fx-background-color: red");
        List<String> names = Arrays.asList("Аудитории", "Занятия");
        List<Pane> panes = Arrays.asList(auditoriums, classes);
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
                to_pane.toFront();
                //menu.setViewOrder(1.0);
                //menu.toFront();
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
        launch(args);
    }
}
