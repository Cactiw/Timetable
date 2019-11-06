package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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

        Scene scene = new Scene(main_layout, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
