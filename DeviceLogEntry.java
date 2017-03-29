import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeviceLogEntry {

	private Date logDate;
	private Date logSubmissionDate;
	protected String username;
	private String deviceID;
	private String logMessage;
	// is this log entry for a "Form Entry Completed" event?
	private boolean isFormEntryCompletedEntry;

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd yyyy HH:mm z");

	public DeviceLogEntry(String csvRow) {
	    csvRow = removeCommasFromDateFields(csvRow);
		String[] tokens = csvRow.split(",");
		try {
			this.logDate = DATE_FORMAT.parse(removeLeadingAndTrailingQuotes(tokens[0]));
			this.logSubmissionDate = DATE_FORMAT.parse(removeLeadingAndTrailingQuotes(tokens[1]));
		} catch (ParseException e) {
            System.out.println("Error parsing dates in DeviceLogEntry");
		}
		this.username = tokens[3];
		this.deviceID = tokens[5];
		this.logMessage = tokens[6];
		isFormEntryCompletedEntry = logMessage.contains("Form Entry Completed");
	}

	public static void testDateParse(String dateString) {
        try {
            System.out.println(DATE_FORMAT.parse(dateString));
        } catch (ParseException e) {
            System.out.println("ERROR IN PARSE");
        }
    }

	private static String removeLeadingAndTrailingQuotes(String s) {
	    return s.substring(0+1, s.length()-1);
    }

	public static String removeCommasFromDateFields(String csvRow) {
        int firstCommaIndex = csvRow.indexOf(",");
        csvRow = removeCharAtIndex(csvRow, firstCommaIndex);
        int secondCommaIndex = csvRow.indexOf(",");
        int thirdCommaIndex = csvRow.indexOf(",", secondCommaIndex+1);
        return removeCharAtIndex(csvRow, thirdCommaIndex);
    }

    private static String removeCharAtIndex(String s, int n) {
	    return s.substring(0, n) + s.substring(n + 1);
    }

	public void printImportantInfo() {
	    System.out.println("------");
	    System.out.println("Log Date: " + logDate);
        System.out.println("Username: " + username);
        System.out.println("Message: " + logMessage);
        System.out.println("Is form entry completed msg?: " + isFormEntryCompletedEntry);
        System.out.println("------");
    }

    public static List<DeviceLogEntry> getEntriesForFormCompletionOnly(List<DeviceLogEntry> fullList) {
	    List<DeviceLogEntry> filteredList = new ArrayList<>();
	    for (DeviceLogEntry e : fullList) {
	        if (e.isFormEntryCompletedEntry) {
	            filteredList.add(e);
            }
        }
        return filteredList;
    }
}