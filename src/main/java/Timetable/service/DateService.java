package Timetable.service;

import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class DateService {
    public static List<String> daysOfWeek = Arrays.asList(
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Воскресенье"
    );

    public static boolean isBetween(int value, int min, int max)
    {
        return((value > min) && (value < max));
    }

    public static LocalDate getFirstDayOfWeek(@NonNull LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }
}
