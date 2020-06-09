package Timetable.service;

import Timetable.model.Properties.BorderProperties;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Service;

@Service
public class GridPaneService {
    public static <T extends Node> void addToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex) {
        Pane pane = finalAddToGridPane(gridPane, node, columnIndex, rowIndex, 1);
    }

    public static <T extends Node> void addToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex,
                                                      int columnSpan) {
        Pane pane = finalAddToGridPane(gridPane, node, columnIndex, rowIndex, columnSpan);
    }

    public static <T extends Node> void addToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex,
                                                      BorderProperties borderProperties) {
        Pane pane = finalAddToGridPane(gridPane, node, columnIndex, rowIndex, 1);
        if (!borderProperties.getRightBorder()) {
            pane.getStyleClass().add("-no-right-border");
        }
        if (!borderProperties.getLeftBorder()) {
            pane.getStyleClass().add("-no-left-border");
        }
    }

    private static <T extends Node> Pane finalAddToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex,
                                                            int columnSpan) {
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

        gridPane.add(pane, columnIndex, rowIndex, columnSpan, 1);
        return pane;
    }

    public static void fillRowEmpty(GridPane gridPane, int rowIndex, int colLimit) {
        for (int colIndex = 0; colIndex <= colLimit; ++colIndex) {
            addToGridPane(gridPane, new Label(""), colIndex, rowIndex);
        }
    }

}
