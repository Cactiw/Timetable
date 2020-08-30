package Timetable.service;

import Timetable.model.Parameters.StyleParameter;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class GridPaneService {
    @NonNull
    public static <T extends Node> Pane addToGridPane(@NonNull final GridPane gridPane,
                                                      @NonNull final T node,
                                                      final int columnIndex,
                                                      final int rowIndex) {
        return finalAddToGridPane(gridPane, node, columnIndex, rowIndex, 1, new StyleParameter());
    }

    @NonNull
    public static <T extends Node> Pane addToGridPane(@NonNull final GridPane gridPane,
                                                      @NonNull final T node,
                                                      final int columnIndex,
                                                      final int rowIndex,
                                                      final int columnSpan) {
        return finalAddToGridPane(gridPane, node, columnIndex, rowIndex, columnSpan, new StyleParameter());
    }

    @NonNull
    public static <T extends Node> Pane addToGridPane(@NonNull final GridPane gridPane,
                                                      @NonNull final T node,
                                                      final int columnIndex,
                                                      final int rowIndex,
                                                      final int columnSpan,
                                                      @NonNull final StyleParameter addStyle) {
        applyStyle(node, addStyle);
        return finalAddToGridPane(gridPane, node, columnIndex, rowIndex, columnSpan, new StyleParameter());
    }

    @NonNull
    public static <T extends Node> Pane addToGridPane(@NonNull final GridPane gridPane,
                                                      @NonNull final T node,
                                                      final int columnIndex,
                                                      final int rowIndex,
                                                      @NonNull final StyleParameter addStyle) {
        applyStyle(node, addStyle);
        return finalAddToGridPane(gridPane, node, columnIndex, rowIndex, 1, addStyle);

    }
    private static <T extends Node> void applyStyle(@NonNull final T node, @NonNull final StyleParameter addStyle) {
        if (addStyle.hasLabelStyle()) {
            node.getStyleClass().add(addStyle.getLabelStyle());
        }
    }

    @NonNull
    private static <T extends Node> Pane finalAddToGridPane(@NonNull final GridPane gridPane,
                                                            @NonNull final T node,
                                                            final int columnIndex,
                                                            final int rowIndex,
                                                            final int columnSpan,
                                                            @NonNull final StyleParameter addStyle) {
        final BorderPane pane = new BorderPane();
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

    public static void fillRowEmpty(@NonNull final GridPane gridPane, final int rowIndex, final int colLimit) {
        for (int colIndex = 0; colIndex <= colLimit; ++colIndex) {
            addToGridPane(gridPane, new Label(""), colIndex, rowIndex);
        }
    }

    public static void fillRowEmpty(@NonNull final GridPane gridPane,
                                    final int rowIndex,
                                    final int colLimit,
                                    @NonNull final StyleParameter addStyle) {
        for (int colIndex = 0; colIndex <= colLimit; ++colIndex) {
            addToGridPane(gridPane, new Label(""), colIndex, rowIndex, addStyle);
        }
    }

}
