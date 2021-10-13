package Timetable.service;

import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static List<String> months = Arrays.asList(
            "Января",
            "Февраля",
            "Марта",
            "Апреля",
            "Мая",
            "Июня",
            "Июля",
            "Августа",
            "Сентября",
            "Октября",
            "Ноября",
            "Декабря"
    );

    public static String formatRussian(@NonNull final LocalDateTime date) {
        return formatRussian(date.toLocalDate());
    }

    public static String formatRussian(@NonNull final LocalDate date) {
        return date.getDayOfMonth() + " " + months.get(date.getMonth().getValue() - 1) + " (" + daysOfWeek.get(
                date.getDayOfWeek().getValue()) + ")";
    }

    public static boolean isBetween(int value, int min, int max)
    {
        return((value >= min) && (value <= max));
    }

    public static boolean isBetween(long value, int min, int max)
    {
        return((value >= min) && (value <= max));
    }

    public static LocalDate getFirstDayOfWeek(@NonNull LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }
}
