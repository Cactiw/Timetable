package Timetable.service;

import Timetable.model.Parameters.StyleParameter;
import Timetable.model.Properties.BorderProperties;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Service;

@Service
public class GridPaneService {
    public static <T extends Node> Pane addToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex) {
        return finalAddToGridPane(gridPane, node, columnIndex, rowIndex, 1, new StyleParameter());
    }

    public static <T extends Node> Pane addToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex,
                                                      int columnSpan) {
        return finalAddToGridPane(gridPane, node, columnIndex, rowIndex, columnSpan, new StyleParameter());
    }

    public static <T extends Node> Pane addToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex,
                                                      int columnSpan, StyleParameter addStyle) {
        applyStyle(node, addStyle);
        return finalAddToGridPane(gridPane, node, columnIndex, rowIndex, columnSpan, new StyleParameter());
    }

    public static <T extends Node> Pane addToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex,
                                                      StyleParameter addStyle) {
        applyStyle(node, addStyle);
        return finalAddToGridPane(gridPane, node, columnIndex, rowIndex, 1, addStyle);

    }

    private static <T extends Node> void applyStyle(T node, StyleParameter addStyle) {
        if (addStyle.hasLabelStyle()) {
            node.getStyleClass().add(addStyle.getLabelStyle());
        }
    }

    private static <T extends Node> Pane finalAddToGridPane(GridPane gridPane, T node, int columnIndex, int rowIndex,
                                                            int columnSpan, StyleParameter addStyle) {
        BorderPane pane = new BorderPane();
        pane.centerProperty().set(node);
        pane.getStyleClass().add("classes-grid-cell");
        if (columnIndex == 0) {
            pane.getStyleClass().add("first-column");
        }
        if (rowIndex == 0) {
            pane.getStyleClass().add("first-row");
        }

        if (addStyle.hasPaneStyle()) {
            pane.getStyleClass().add(addStyle.getPaneStyle());
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

    public static void fillRowEmpty(GridPane gridPane, int rowIndex, int colLimit, StyleParameter addStyle) {
        for (int colIndex = 0; colIndex <= colLimit; ++colIndex) {
            addToGridPane(gridPane, new Label(""), colIndex, rowIndex, addStyle);
        }
    }

}
