package Timetable.model.Windows;

import Timetable.model.Dialogs.ResolveRequestDialog;
import Timetable.service.DateService;
import Timetable.service.RequestService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


public class MainRequestsWindow {

    private VBox rootPane;

    private StackPane container;

    private RequestService requestService;
    private ResolveRequestDialog resolveRequestDialog;
    private MainClassesWindow mainClassesWindow;

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
        rootPane = new VBox();
        rootPane.spacingProperty().setValue(10);


        updateRequests();
        return rootPane;
    }

    public void updateRequests() {
        rootPane.getChildren().clear();
        Label headerLabel = new Label("Запросы на перенос занятий:");
        headerLabel.getStyleClass().add("request-header-label");
        rootPane.getChildren().add(headerLabel);

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

            rootPane.getChildren().add(requestBox);
        }

    }
}
