package Timetable.model;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class Config {
    static LocalTime classesBeginDefaultTime = LocalTime.of(8, 45);
    static Duration classesDefaultDuration = Duration.ofMinutes(95);
    static int defaultClassesCount = 6;
}
