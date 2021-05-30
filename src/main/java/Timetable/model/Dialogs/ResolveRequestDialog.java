package Timetable.model.Dialogs;

import Timetable.model.Config;
import Timetable.model.Pair;
import Timetable.model.Request;
import Timetable.model.Windows.MainClassesWindow;
import Timetable.model.Windows.MainRequestsWindow;
import Timetable.service.AuditoriumService;
import Timetable.service.NetworkRequestService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ResolveRequestDialog {
    private JFXDialog dialog;
    private JFXDialogLayout content;

    private JFXButton okButton;

    private StackPane container;
    private Request request;

    private Region left;

    private MainClassesWindow mainClassesWindow;
    private MainRequestsWindow mainRequestsWindow;

    @Autowired
    private AuditoriumService auditoriumService;

    public void show(
            @NonNull final StackPane container,
            @NonNull final Request request,
            @NonNull final MainClassesWindow mainClassesWindow,
            @NonNull final MainRequestsWindow mainRequestsWindow
    ) {
        this.request = request;
        this.container = container;
        this.mainClassesWindow = mainClassesWindow;
        this.mainRequestsWindow = mainRequestsWindow;

        dialog = new JFXDialog();
        content = new JFXDialogLayout();

        updateContent();

        okButton = new JFXButton("Закрыть");
        okButton.setPrefSize(200, 25);
        okButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: green; -fx-background-color: whitesmoke");
        okButton.setOnAction(e -> this.dialog.close());

        left = new Region();
        HBox.setHgrow(left, Priority.ALWAYS);
        content.setActions(okButton);
        dialog.setContent(content);

        dialog.show(container);

    }

    private void updateContent() {
        GridPane rootPane = new GridPane();
        rootPane.setMinWidth(1000);
        rootPane.setPrefWidth(1000);
        rootPane.setPadding(new Insets(10, 10, 10, 10));
        rootPane.setHgap(30);
        rootPane.setVgap(30);

        content.setBody(rootPane);

        int COL_NUM = 3;
        for (int i = 0; i < COL_NUM; ++i) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHgrow(Priority.ALWAYS);
            columnConstraints.setHalignment(HPos.CENTER);
            rootPane.getColumnConstraints().add(columnConstraints);
        }

        var auditoriums = auditoriumService.getTopMaxAvailableAuditoriums(
                request.getNewBeginTime(), request.getNewEndTime(),
                request.getRequestPair().getAuditorium() != null ? request.getRequestPair().getAuditorium().getMaxStudents() : 0,
                9
        );

        for (int i = 0; i < auditoriums.size(); ++i) {
            var auditorium = auditoriums.get(i);
            Pane pane = auditoriumService.getAuditoriumPane(container, auditorium);
            pane.setOnMouseClicked(e -> {
                if (e.getClickCount() >= 2) {  // On double click);
                    final String url = "http://" + Config.parserIp + ":" + Config.parserPort + "/requests/resolve";
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("request_id", request.getId().toString());
                    map.put("auditorium_id", auditorium.getId().toString());

                    try {
                        NetworkRequestService.post(url, map);
                        this.dialog.close();
                        mainRequestsWindow.updateRequests();
                        mainClassesWindow.updateClasses();

                    } catch (Exception error) {
                        error.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setContentText("Произошла сетевая ошибка при запросе.");
                        alert.showAndWait();
                    }
                }
            });
            rootPane.add(pane, i % COL_NUM, i / COL_NUM);
        }
    }

}
