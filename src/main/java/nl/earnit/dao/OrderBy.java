package nl.earnit.dao;

import nl.earnit.exceptions.InvalidOrderByException;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * The type Order by.
 */
public class OrderBy {
    private static final String regex = "([a-zA-Z_.]+:(desc|asc))";
    private static final Pattern pattern = Pattern.compile(regex);

    private final Map<String, String> allowedColumns;

    /**
     * Instantiates a new Order by.
     *
     * @param allowedColumns the allowed columns
     */
    public OrderBy(Map<String, String> allowedColumns) {
        this.allowedColumns = allowedColumns;
    }

    private static boolean isValidQuery(String query) {
        String[] orders = query.split(",");
        if (orders.length < 1) return false;

        for (String order : orders) {
            if(!pattern.matcher(order).matches()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Is valid boolean.
     *
     * @param query the query
     * @return the boolean
     */
    public boolean isValid(String query) {
        if (query.length() < 1) return true;

        if (!isValidQuery(query)) {
            return false;
        }

        String[] orders = query.split(",");

        for (String order : orders) {
            String[] items = order.split(":");
            String direction = items[1];

            if (!direction.equals("asc") && !direction.equals("desc")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the order by e.g.: week.year:desc -> w.year desc
     *
     * @param query               to convert
     * @param convertToOrderBySql the convert to order by sql
     * @return Order by
     * @throws InvalidOrderByException If query is invalid
     */
    public String getSQLOrderBy(String query, boolean convertToOrderBySql) throws InvalidOrderByException {
        if (!isValid(query)){
            throw new InvalidOrderByException("Query does not match '<column>:<asc|desc>,<column>:<asc|desc>'");
        }

        String[] orders = query.split(",");
        StringBuilder sqlBuilder = new StringBuilder();
        for (String order : orders) {
            if (order.length() < 1) continue;

            String[] items = order.split(":");
            String column = items[0];
            String direction = items[1];

            if (!allowedColumns.containsKey(column)) continue;

            sqlBuilder.append(allowedColumns.get(column)).append(" ").append(direction).append(",");
        }

        String sql = sqlBuilder.toString();
        String sqlOrderBy = sql.length() == 0
            ? null
            : sql.substring(0, sql.length() - 1);

        if (convertToOrderBySql) {
            return convertToOrderBySQL(sqlOrderBy);
        }

        return sqlOrderBy;
    }

    /**
     * Convert to order by sql string.
     *
     * @param sqlOrderBy the sql order by
     * @return the string
     */
    public static String convertToOrderBySQL(String sqlOrderBy) {
        return sqlOrderBy == null ? "" : " ORDER BY " + sqlOrderBy;
    }
}
