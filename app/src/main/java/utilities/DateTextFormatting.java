package utilities;

import java.text.SimpleDateFormat;

public class DateTextFormatting {
    public static String cardPattern = "MMM d yyyy";
    private static String fullPattern = "MM/dd/yyyy";
    public static SimpleDateFormat cardDateFormat = new SimpleDateFormat(cardPattern);
    public static SimpleDateFormat fullDateFormat = new SimpleDateFormat(fullPattern);
}

