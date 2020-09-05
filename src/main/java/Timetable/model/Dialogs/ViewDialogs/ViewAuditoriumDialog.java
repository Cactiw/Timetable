package Timetable.model.Dialogs.ViewDialogs;

import Timetable.model.Auditorium;
import Timetable.model.Dialogs.AddAuditoriumDialog;
import Timetable.model.Pair;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ViewAuditoriumDialog {
    private JFXDialog dialog;
    private JFXDialogLayout content;

    private JFXButton okButton;

    private Auditorium auditorium;

    @Autowired
    AddAuditoriumDialog addAuditoriumDialog;

    public void show(@NonNull final StackPane container, @NonNull final Auditorium auditorium) {
        this.auditorium = auditorium;

        dialog = new JFXDialog();
        content = new JFXDialogLayout();

        updateContent();

        content.setActions(okButton);
        dialog.setContent(content);

        dialog.show(container);

    }

    private void updateContent() {
        okButton = new JFXButton("Ок");
        okButton.setPrefSize(50, 25);
        okButton.styleProperty().setValue("-fx-font-size: 13pt; -fx-text-fill: green; -fx-background-color: whitesmoke");
        okButton.setOnAction(e -> this.dialog.close());

        final GridPane rootPane = new GridPane();

        final Text heading = new Text(auditorium.getName());
        heading.styleProperty().setValue("-fx-font-size: 14pt;");
        content.setHeading(heading);
        content.setBody(rootPane);

        rootPane.setPadding(new Insets(10, 0, 10, 0));
        rootPane.setHgap(10);

        final Image editIcon = new Image("/icons/edit.png");
        final JFXButton editPairButton = new JFXButton("", new ImageView(editIcon));
        editPairButton.setOnAction(e -> {
            addAuditoriumDialog.showFromAuditorium(auditorium);
            this.updateContent();
        });
        rootPane.add(editPairButton, 3, 0);

        rootPane.add(new Label("Название:"), 0, 0);
        rootPane.add(new Label(auditorium.getName()), 1, 0, 2, 1);

        rootPane.add(new Label("Вместимость:"), 0, 1);
        rootPane.add(new Label(auditorium.getMaxStudents().toString()), 1, 1, 2, 1);
    }

    @Nullable
    public JFXDialog getDialog() {
        return dialog;
    }
}
