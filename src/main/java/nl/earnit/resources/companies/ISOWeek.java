package nl.earnit.resources.companies;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

/**
 * The type Iso week.
 */
public class ISOWeek {
    /**
     * Gets monday.
     *
     * @param year the year
     * @param week the week
     * @return the monday
     */
    public static LocalDate getMonday(int year, int week) {
        return LocalDate.of(year, Month.JUNE, 1)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week);
    }

    /**
     * Gets next monday.
     *
     * @param year the year
     * @param week the week
     * @return the next monday
     */
    public static LocalDate getNextMonday(int year, int week) {
        return getMonday(year, week).plusDays(7);
    }
}
