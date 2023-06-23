package nl.earnit.resources.companies;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

public class ISOWeek {
    public static LocalDate getMonday(int year, int week) {
        return LocalDate.of(year, Month.JUNE, 1)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week);
    }

    public static LocalDate getNextMonday(int year, int week) {
        return getMonday(year, week).plusDays(7);
    }
}
