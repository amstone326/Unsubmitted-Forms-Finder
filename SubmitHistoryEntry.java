import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by amstone326 on 3/28/17.
 */
public class SubmitHistoryEntry {

    private String formInstanceId;
    private String domain;
    private String username;
    private Date completedOnDeviceTime;
    private String userId;
    private Date receivedByServerTime;
    private String deviceId;
    private String ccVersion;

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

    public SubmitHistoryEntry(String row) {
        String[] tokens = row.split(",");

        formInstanceId = tokens[0];
        domain = tokens[1];
        username = tokens[2];
        userId = tokens[4];

        try {
            completedOnDeviceTime = DATE_FORMAT.parse(removeMillisecondDataFromDateString(tokens[3]));
            receivedByServerTime = DATE_FORMAT.parse(removeMillisecondDataFromDateString(tokens[5]));
        } catch (ParseException e) {
            System.out.println("Error parsing dates in SubmitHistoryEntry");
        }

        // Fields that won't always exist
        if (tokens.length >= 7) {
            deviceId = tokens[6];
        }
        if (tokens.length >= 8) {
            ccVersion = tokens[7];
        }
    }

    public static String removeMillisecondDataFromDateString(String dateString) {
        int indexOfPeriod = dateString.indexOf(".");
        return dateString.substring(0, indexOfPeriod) + dateString.substring(dateString.length()-1);
    }

    public static void testDateParse(String dateString) {
        try {
            System.out.println(DATE_FORMAT.parse(removeMillisecondDataFromDateString(dateString)));
        } catch (ParseException e) {
            System.out.println("ERROR IN PARSE");
        }
    }
}
