import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by amstone326 on 3/28/17.
 */
public class SubmitHistoryEntry {

    protected String formInstanceId;
    private String domain;
    protected String username;
    protected Date completedOnDeviceTime;
    private String userId;
    private Date receivedByServerTime;
    protected String deviceId;
    private String ccVersion;

    protected boolean alreadyMatchedToDeviceLogEntry;

    private static DateFormat DATE_FORMAT_1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    private static DateFormat DATE_FORMAT_2 = new SimpleDateFormat("M/dd/yy H:mm");

    public SubmitHistoryEntry(String row) {
        String[] tokens = row.split(",");

        formInstanceId = tokens[0];
        domain = tokens[1];
        username = tokens[2];
        userId = tokens[4];

        try {
            completedOnDeviceTime = DATE_FORMAT_1.parse(removeMillisecondDataFromDateString(tokens[3]));
            receivedByServerTime = DATE_FORMAT_1.parse(removeMillisecondDataFromDateString(tokens[5]));
        } catch (ParseException e) {
            try {
                completedOnDeviceTime = DATE_FORMAT_2.parse(removeMillisecondDataFromDateString(tokens[3]));
                receivedByServerTime = DATE_FORMAT_2.parse(removeMillisecondDataFromDateString(tokens[5]));
            } catch (ParseException e2) {
                System.out.println("Error parsing dates in SubmitHistoryEntry");
            }
        }

        // Fields that won't always exist
        if (tokens.length >= 8) {
            deviceId = tokens[7];
        }
        if (tokens.length >= 8) {
            ccVersion = tokens[8];
        }
    }

    public static String removeMillisecondDataFromDateString(String dateString) {
        int indexOfPeriod = dateString.indexOf(".");
        if (indexOfPeriod >= 0) {
            return dateString.substring(0, indexOfPeriod) + dateString.substring(dateString.length() - 1);
        } else {
            return dateString;
        }
    }

    public static void testDateParse1(String dateString) {
        try {
            System.out.println(DATE_FORMAT_1.parse(removeMillisecondDataFromDateString(dateString)));
        } catch (ParseException e) {
            System.out.println("ERROR IN PARSE");
        }
    }

    public static void testDateParse2(String dateString) {
        try {
            System.out.println(DATE_FORMAT_2.parse(removeMillisecondDataFromDateString(dateString)));
        } catch (ParseException e) {
            System.out.println("ERROR IN PARSE");
        }
    }

    public void printImportantInfo() {
        System.out.println("------");
        System.out.println("Form instance id: " + formInstanceId);
        System.out.println("Username: " + username);
        System.out.println("Completed On Device Time: " + completedOnDeviceTime);
        System.out.println("------");
    }

    public void setMatchedToDeviceLogEntry() {
        alreadyMatchedToDeviceLogEntry = true;
    }
}
