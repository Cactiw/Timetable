package Timetable.service;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Service;

@Service
public class GridPaneService {
    public static <T extends Node> void addToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex) {
        BorderPane pane = new BorderPane();
        pane.centerProperty().set(node);
        pane.getStyleClass().add("classes-grid-cell");
        if (columnIndex == 0) {
            pane.getStyleClass().add("first-column");
        }
        if (rowIndex == 0) {
            pane.getStyleClass().add("first-row");
        }

        pane.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(pane, columnIndex, rowIndex);
    }
}
