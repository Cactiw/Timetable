package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;


public class Main extends Application{

    Button button;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Timetable");
        primaryStage.setScene(new Scene(root, 1500, 700));

        button = new Button("Hello");
        button.setOnAction(event -> System.out.println("Hi"));

        StackPane main_layout = new StackPane();
        main_layout.getChildren().add(button);

        BorderPane root_pane = new BorderPane();
        root_pane.setLeft(sidePane());

        Scene scene = new Scene(main_layout, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox sidePane() {
        VBox vbox = new VBox();
        vbox.setPrefWidth(500);
        vbox.setStyle("-fx-background-color: #594031");
        vbox.getChildren().add(boxItem("delete"));
        return vbox;
    }

    private HBox boxItem(String item) {
        Image image = new Image(JavaFXMenu.class.getResource("/icons/" + item + ".png").toExternalForm());
        ImageView view = new ImageView(image);
        Button button = new Button();
        button.setGraphic(view);
        Pane paneIndicator = new Pane();
        paneIndicator.setPrefSize(100, 500);
        HBox box = new HBox(button);
        box.setStyle("-fx-background-color: #594031");
        return box;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
