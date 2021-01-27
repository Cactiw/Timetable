package Timetable;

import Timetable.model.Config;
import Timetable.model.Dialogs.AddDialogs.AddAuditoriumDialog;
import Timetable.model.Dialogs.AddDialogs.AddPairDialog;
import Timetable.model.Dialogs.AddDialogs.AddPeopleUnionDialog;
import Timetable.model.Dialogs.AddDialogs.AddUserDialog;
import Timetable.model.Dialogs.ViewDialogs.ViewAuditoriumDialog;
import Timetable.model.Dialogs.ViewDialogs.ViewPairDialog;
import Timetable.model.Windows.MainAuditoriumWindow;
import Timetable.model.Windows.MainClassesWindow;
import Timetable.service.*;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.coyote.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


@Lazy
@EntityScan
@SpringBootApplication(scanBasePackages = "Timetable")
@EnableJpaRepositories("Timetable.repositories")
public class Main extends AbstractJavaFxApplicationSupport {

    private StackPane modes;

    private MainAuditoriumWindow mainAuditoriumWindow;
    private MainClassesWindow mainClassesWindow;

    private Pane rootAuditoriumPane;
    private Pane rootClassesPane;

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
    @Autowired
    private AuditoriumPropertyService auditoriumPropertyService;

    @Override
    public void start(@NonNull final Stage primaryStage) throws Exception {
        databaseService.appInit();
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Timetable");

        final StackPane mainStack = new StackPane();
        final HBox root_pane = new HBox();

        modes = new StackPane();

        mainAuditoriumWindow = new MainAuditoriumWindow(modes, auditoriumService, auditoriumPropertyService,
                pairService, viewAuditoriumDialog);
        mainClassesWindow = new MainClassesWindow(modes, peopleUnionService, peopleUnionTypeService, pairService,
                viewPairDialog);
        rootClassesPane = mainClassesWindow.initiateClassesWindow();
        rootAuditoriumPane = mainAuditoriumWindow.initiateAuditoriumWindow();

        VBox menu = sidePane();
        root_pane.getChildren().add(menu);


        rootClassesPane.toBack();
        rootClassesPane.setVisible(false);

        root_pane.getChildren().add(modes);
        HBox.setHgrow(modes, Priority.ALWAYS);
        HBox.setHgrow(root_pane, Priority.ALWAYS);

        mainStack.getChildren().add(root_pane);

        // Создание меню
        final Menu addMenu = new Menu("Вставка");

        final MenuItem addUser = new MenuItem("Создать пользователя");
        final MenuItem addAuditorium = new MenuItem("Создать аудиторию");
        final MenuItem addPair = new MenuItem("Создать занятие");
        final MenuItem addPeopleUnion = new MenuItem("Создать группу");

        addUser.setOnAction((@NonNull final ActionEvent actionEvent) ->
                new AddUserDialog(userService, peopleUnionService).show());
        addAuditorium.setOnAction((@NonNull final ActionEvent actionEvent) -> {
            new AddAuditoriumDialog(auditoriumService, auditoriumPropertyService).show();
            mainAuditoriumWindow.refresh();
        });
        addPair.setOnAction((@NonNull final ActionEvent actionEvent) -> {
            addPairDialog.show();
            mainClassesWindow.updateClasses();
        });

        addPeopleUnion.setOnAction((e) -> new AddPeopleUnionDialog(peopleUnionTypeService, peopleUnionService).show());

        // There were multiple .getItems() calls
        addMenu.getItems().addAll(List.of(addUser, addAuditorium, addPair, addPeopleUnion));

        final Menu importMenu = new Menu("Импорт");
        final MenuItem importXls = new MenuItem("Из файла");
        importXls.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                System.out.println("File selected!");
                try {
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    final String url = "http://" + Config.parserIp + ":" + Config.parserPort + "/parseXls";
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("file", Base64.getEncoder().encodeToString(fileInputStream.readAllBytes()));
                    try {
                        ResponseEntity<String> response = RequestService.post(url, map);
                        JSONObject jsonObject = new JSONObject(response.getBody());

                        JSONObject timetable = (JSONObject)jsonObject.get("timetable");

                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Успех!");
                        success.setContentText(timetable.keySet().toString());
                        success.showAndWait();

                        timetable.keySet().forEach(keyStr ->
                        {
                            JSONArray pairs = (JSONArray)timetable.get(keyStr);
                            System.out.println("Processed " + keyStr + " " + pairs.toString());
                        });

                    } catch (Exception error) {
                        error.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setContentText("Произошла ошибка при парсинге.\n" +
                                "Убедитесь, что файл соответствует шаблону");
                        alert.showAndWait();
                    }
                } catch (IOException ignored) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setContentText("Файл не найден");
                    alert.showAndWait();
                }
            }
        });
        importMenu.getItems().addAll(importXls);

        final MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(addMenu, importMenu);

        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(menuBar, mainStack);
        VBox.setVgrow(mainStack, Priority.ALWAYS);

        final Scene scene = new Scene(mainBox, 1000, 700);
        scene.getStylesheets().addAll(List.of("styles/styles.css", "styles/classes.css", "styles/auditoriums.css",
                "styles/scrollpane.css", "styles/datepicker.css"));

//        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("icons/icon.png"));
        primaryStage.show();
    }


    @NonNull
    private VBox sidePane() {
        final VBox vbox = new VBox();
        vbox.setPrefWidth(200);
        vbox.setMinWidth(200);
        //vbox.setStyle("-fx-background-color: red");
        final List<String> names = Arrays.asList("Аудитории", "Занятия");
        final List<Pane> panes = Arrays.asList(rootAuditoriumPane, rootClassesPane);
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
        });
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

    public static void main(String[] args) {
        AbstractJavaFxApplicationSupport.launchApp(Main.class, args);
    }
}
