package Timetable.model.Dialogs.ViewDialogs;

import Timetable.model.Pair;
import com.jfoenix.animation.alert.CenterTransition;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.awt.*;

public class ViewPairDialog {
    JFXDialog dialog;
    StackPane container;
    JFXDialogLayout content;
    GridPane rootPane;

    JFXButton okButton;

    public ViewPairDialog(StackPane container) {
        this.container = container;
    }

    public void show(Pair pair) {
        dialog = new JFXDialog();
        content = new JFXDialogLayout();
        rootPane = new GridPane();

        okButton = new JFXButton("Ок");
        okButton.setPrefSize(50, 25);
        okButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: green; -fx-background-color: whitesmoke");
        okButton.setOnAction(e -> {
            this.dialog.close();
        });

        content.setHeading(new Text(pair.formatPair()));
        content.setBody(rootPane);

        rootPane.add(new Label("Предмет:"), 0, 0);
        rootPane.add(new Label(pair.getSubject()), 1, 0);
        rootPane.setPadding(new Insets(10, 0, 10, 0));
        rootPane.setHgap(10);



        content.setActions(okButton);
        dialog.setContent(content);

        dialog.show(container);

    }
}
