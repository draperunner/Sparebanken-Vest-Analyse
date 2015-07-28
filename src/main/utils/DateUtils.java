package main.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mats on 22.07.2015.
 */
public class DateUtils {

    public static LocalDate stringToLocalDate(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(time, formatter);
    }

    public static long getDaysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public static List<LocalDate> getDatesBetween(LocalDate start, LocalDate end) {
        long days = (int) getDaysBetween(start, end);
        List<LocalDate> dates = new ArrayList<>((int) days);
        for (int i = 0; i < days; i++) {
            dates.add(start.plusDays(i));
        }
        return dates;
    }

    public static List<YearMonth> getYearMonthsBetween(LocalDate start, LocalDate end) {
        List<LocalDate> dates = getDatesBetween(start, end);
        Set<YearMonth> yearMonthsSet = new HashSet<>();
        dates.forEach(date -> yearMonthsSet.add(YearMonth.of(date.getYear(), date.getMonth())));
        return new ArrayList<>(yearMonthsSet);
    }
}
