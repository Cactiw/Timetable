package Timetable.service;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;


public abstract class WeekPicker{

    public static DatePicker getWeekPicker() {

        DatePicker datePicker = new DatePicker();

        datePicker.showingProperty().addListener((obs, b, b1) -> {
            if (b1) {
                System.out.println("Setting datepicker!");
                Node content = ((DatePickerSkin) datePicker.getSkin()).getPopupContent();
                List<DateCell> cells = content.lookupAll(".day-cell").stream()
                        //                .filter(ce->!ce.getStyleClass().contains("next-month"))
                        .map(n -> (DateCell) n)
                        .collect(Collectors.toList());
                cells.forEach(c -> c.getStyleClass().add("selected"));
                cells.forEach(ce -> ce.updateSelected(true));

                EventHandler<MouseEvent> mouseClickedEventHandler = (MouseEvent clickEvent) ->
                {
                    System.out.println("Mouse clicked");
                    if (clickEvent.getButton() == MouseButton.PRIMARY) {

                        //                this.datePicker.show();
                        //                clickEvent.consume();

                        Node n = clickEvent.getPickResult().getIntersectedNode();
                        DateCell c = null;
                        if (n instanceof DateCell) {
                            c = (DateCell) n;
                        } else if (n instanceof Text) {
                            c = (DateCell) (n.getParent());
                        }
                        if (c != null && c.getStyleClass().contains("day-cell")) {
                            final LocalDate selectedDate = datePicker.getValue();
                            final LocalDate startDate = WeekPicker.getFirstDayOfWeek(selectedDate);
                            final LocalDate endDate = startDate.plusDays(7);
                            Integer ini = startDate.getDayOfMonth();
                            Integer end = endDate.getDayOfMonth();

                            System.out.println("Ini = " + ini.toString() + "end = " + end.toString());

                            cells.forEach(ce -> ce.getStyleClass().remove("selected"));
                            cells.stream()
                                    .filter(ce -> Integer.parseInt(ce.getText()) >= ini)
                                    .filter(ce -> Integer.parseInt(ce.getText()) <= end)
//                                    .forEach(ce -> datePicker.setValue(ce.getItem()));
                                    .forEach(ce -> ce.getStyleClass().add("selected"));
//                                    .forEach(ce -> ce.updateSelected(true));
                            datePicker.setValue(startDate);
                            System.out.println("Styles set!");
                        }
                    }
                };
                content.setOnMouseClicked(mouseClickedEventHandler);
            } ;
        });
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate localDate) {
                return localDate.toString() + " — " + localDate.plusDays(6).toString();
            }

            @Override
            public LocalDate fromString(String s) {
                return null;
            }
        });
        return datePicker;
    }

    public static LocalDate getFirstDayOfWeek(@NonNull LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }
}