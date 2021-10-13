package Timetable.model;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Component
public class Config {
    @NonNull
    public static final LocalTime classesBeginDefaultTime = LocalTime.of(8, 45);
    @NonNull
    public static final Duration classesDefaultDuration = Duration.ofMinutes(95);
    public static final int defaultClassesCount = 6;

    @NonNull
    public static final String parserIp = "144.91.112.129";  // fixme do not commit
    @NonNull
    public static final int parserPort = 6000;
}
