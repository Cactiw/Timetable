package Timetable.service;

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
}
