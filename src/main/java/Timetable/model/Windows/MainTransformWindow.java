package Timetable.model.Windows;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MainTransformWindow {

    private StackPane container;

    private VBox rootPane;
    private JFXTextField textField;

    public MainTransformWindow(
            @NonNull final StackPane container
    ) {
        this.container = container;
    }

    public Pane initiateWindow() {
        rootPane = new VBox();

        textField = new JFXTextField();
        textField.setLabelFloat(true);
        textField.setPromptText("Type Something");

        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.setSelectionHandler(event -> {
            final int caretPosition = getCaretPosition(textField);
            var tokens = getTokens(textField);
            final String replace = tokens.get(tokens.size() - 1);
            int addedLength;
            if (replace.length() > 0) {
                textField.setText(textField.getText().replace(replace, event.getObject()));
                addedLength = event.getObject().length() - replace.length();
            } else {
                textField.setText(textField.getText(0, caretPosition) + event.getObject() + textField.getText(caretPosition, textField.getLength()));
                addedLength = event.getObject().length();
            }
            textField.positionCaret(caretPosition + addedLength);

        });
        autoCompletePopup.getSuggestions().addAll("Преподаватель", "Студент");
        textField.textProperty().addListener(observable ->{
            final int caretPosition = getCaretPosition(textField);
            final String previousToken;
            var tokens = getTokens(textField);
            final String currentToken = tokens.get(tokens.size() - 1).toLowerCase(Locale.ROOT);
            if (tokens.size() > 1) {
                previousToken = tokens.get(tokens.size() - 2);
            } else {
                previousToken = "";
            }
            autoCompletePopup.getSuggestions().clear();
            System.out.println(getSuggestions(previousToken));
            System.out.println(currentToken);
            autoCompletePopup.getSuggestions().addAll(getSuggestions(previousToken));
            autoCompletePopup.filter(s -> s.toLowerCase(Locale.ROOT).contains(currentToken));
            if(!autoCompletePopup.getFilteredSuggestions().isEmpty()){
                autoCompletePopup.show(textField);
            }else{
                autoCompletePopup.hide();
            }
        });
        rootPane.getChildren().add(textField);


        return rootPane;
    }

    private int getCaretPosition(TextField textField) {
        int caretPosition = textField.caretPositionProperty().get();
        if (textField.getText().length() - caretPosition == 1) {
            caretPosition += 1;
        } else if (textField.getText().length() - caretPosition == -1) {
            caretPosition -= 1;
        }
        return caretPosition;
    }

    private List<String> getSuggestions(String token) {
        if (token.strip().length() == 0) {
            return List.of("Преподаватель", "Студент");
        }
        if (token.toLowerCase(Locale.ROOT).equals("преподаватель")) {
            return List.of("Пары_В_Неделю", "Пары_В_День");
        }
        if (List.of("пары_в_неделю", "пары_в_день").contains(token.toLowerCase(Locale.ROOT))) {
            return List.of("Всего", "Подряд_Макс");
        }
        return List.of();
    }

    private List<String> getTokens(TextField textField) {
        int caretPosition = getCaretPosition(textField);
        final var tokens = new java.util.ArrayList<>(List.of(textField.getText().substring(0, caretPosition).split("[. ;<>]")));
        if (textField.getText().length() > 0 && caretPosition != 0 && textField.getText().charAt(caretPosition - 1) == '.') {
            tokens.add("");
        }
        return tokens;
    }
}
