package Timetable.model.Windows;

import Timetable.model.AuditoriumProperty;
import Timetable.model.Dialogs.ResolveRequestDialog;
import Timetable.service.DateService;
import Timetable.service.RequestService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.springframework.lang.NonNull;

import java.util.LinkedList;


public class MainRequestsWindow {

    private BorderPane rootPane;
    private VBox rootVboxPane;

    private StackPane container;

    private RequestService requestService;
    private ResolveRequestDialog resolveRequestDialog;
    private MainClassesWindow mainClassesWindow;

    private TextField teacherFilterField;
    private TextField auditoriumFilterField;

    public MainRequestsWindow(
            @NonNull final StackPane container,
            @NonNull final RequestService requestService,
            @NonNull final ResolveRequestDialog resolveRequestDialog,
            @NonNull final MainClassesWindow mainClassesWindow
    ) {
        this.container = container;
        this.requestService = requestService;
        this.resolveRequestDialog = resolveRequestDialog;
        this.mainClassesWindow = mainClassesWindow;
    }

    public Pane initiateWindow() {
        rootPane = new BorderPane();
        rootVboxPane = new VBox();
        rootPane.setCenter(rootVboxPane);
        rootPane.setRight(getFilters());

        rootVboxPane.spacingProperty().setValue(10);


        updateRequests();
        planUpdateTask();

        return rootPane;
    }

    private void planUpdateTask() {
        Timeline fiveSecondsWonder = new Timeline(
                new KeyFrame(Duration.seconds(15),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("this is called every 5 seconds on UI thread");
                                updateRequests();
                            }
                        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();
    }

    public void updateRequests() {
        rootVboxPane.getChildren().clear();
        Label headerLabel = new Label("Запросы на перенос занятий:");
        headerLabel.getStyleClass().add("request-header-label");
        rootVboxPane.getChildren().add(headerLabel);

        var requests = requestService.getLastRequests();

        for (var request: requests) {
            HBox requestBox = new HBox();
            requestBox.setAlignment(Pos.CENTER_LEFT);
            requestBox.spacingProperty().setValue(30);
            requestBox.setPadding(new Insets(10, 10, 10, 50));
            requestBox.getStyleClass().add("request-pane");

            VBox timeBox = new VBox();
            Label label1 = new Label(
                    DateService.formatRussian(request.getChangeDate()) + " " +
                            request.getRequestPair().getClearBeginTime().toString() +
                            " - " + request.getRequestPair().getClearEndTime().toString()
                    );
            Label label2 = new Label("--->");
            label2.rotateProperty().setValue(90);
//            label2.prefWidthProperty().bind(timeBox.widthProperty());
            Label label3 = new Label(
                    DateService.formatRussian(request.getNewBeginTime().toLocalDate()) + " " +
                    request.getClearNewBeginTime().toString() + " - " + request.getClearNewEndTime().toString()
            );
            timeBox.getChildren().addAll(label1, label2, label3);
            label1.getStyleClass().add("request-pair-time");
            label2.getStyleClass().add("request-pair-time");
            label3.getStyleClass().add("request-pair-time");

            Label pairName = new Label(request.getRequestPair().getSubject());
            pairName.getStyleClass().add("request-pair-name");

            Label pairAuditoriumName = new Label(request.getRequestPair().getAuditoriumName());
            pairAuditoriumName.getStyleClass().add("request-pair-auditorium-name");

            VBox pairNameBox = new VBox();
            pairNameBox.setAlignment(Pos.CENTER);
            pairNameBox.getChildren().addAll(pairName, pairAuditoriumName);

            Label teacherFIO = new Label(request.getRequestPair().getTeacherFIO());
            teacherFIO.getStyleClass().add("request-pair-teacher");

            Region regionCenter = new Region();
            Region regionRight = new Region();
            HBox.setHgrow(regionCenter, Priority.ALWAYS);
            HBox.setHgrow(regionRight, Priority.ALWAYS);

            requestBox.getChildren().addAll(timeBox,
                    regionCenter,
                    pairNameBox,
                    regionRight,
                    teacherFIO
            );

            if (request.getProcessed()) {
                requestBox.getStyleClass().add("grey");
            } else {
                requestBox.getStyleClass().add("red");
                requestBox.setOnMouseClicked(e -> {
                    if (e.getClickCount() >= 2) {  // On double click);
                        resolveRequestDialog.show(container, request, mainClassesWindow, this);
                    }
                });
            }

            rootVboxPane.getChildren().add(requestBox);
        }

    }

    @NonNull private Pane getFilters() {
        VBox root = new VBox();
        root.setPrefWidth(300);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setSpacing(5);
        root.getStyleClass().add("auditorium-filters");

        Label header = new Label("Фильтры");
        header.getStyleClass().add("auditorium-filter-name");
        Separator separator = new Separator();

        Label nameLabel = new Label("Преподаватель:");
        teacherFilterField = new TextField();
        teacherFilterField.setPromptText("Поиск по ФИО преподавателя");
//        teacherFilterField.textProperty().addListener(this::onChanged);

        Pane empty = new Pane();
        Label maxStudentsLabel = new Label("Аудитория:");
        auditoriumFilterField = new TextField();
        auditoriumFilterField.setPromptText("Поиск по названию");
//        auditoriumFilterField.textProperty().addListener(this::onChanged);

        Separator separator2 = new Separator();

        root.getChildren().addAll(header, separator, nameLabel, teacherFilterField, empty, maxStudentsLabel,
                auditoriumFilterField, separator2);
        return root;
    }

}
