import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnsubmittedFormsFinder {

    private static List<DeviceLogEntry> fullDeviceLogsList;
    private static List<DeviceLogEntry> deviceLogsForFormCompletion;
    private static List<SubmitHistoryEntry> submitHistoryList;

	private static void findUnsubmittedForms(String deviceLogsCsvFilename, String submitHistoryCsvFilename) {
		fullDeviceLogsList = parseDeviceLogsFromCsv(deviceLogsCsvFilename);
		deviceLogsForFormCompletion = DeviceLogEntry.getEntriesForFormCompletionOnly(fullDeviceLogsList);

        String usernameOfInterest = deviceLogsForFormCompletion.get(0).username;
        String deviceIdOfInterest = deviceLogsForFormCompletion.get(0).deviceID;
        Date startingDatetime = deviceLogsForFormCompletion.get(0).logDate;
		submitHistoryList =
                parseSubmitHistoryFromCsv(submitHistoryCsvFilename, usernameOfInterest, deviceIdOfInterest,
                        startingDatetime);

		compareListSizes();
		flagFirstMistmatch();
	}

	private static void flagFirstMistmatch() {
	    for (int i = 0; i < Math.min(deviceLogsForFormCompletion.size(), submitHistoryList.size()); i++) {
	        DeviceLogEntry formCompletionLogEntry = deviceLogsForFormCompletion.get(i);
	        SubmitHistoryEntry submission = submitHistoryList.get(i);
	        if (formCompletionLogEntry.logDate.)
        }
    }

	private static List<DeviceLogEntry> parseDeviceLogsFromCsv(String csvFilename) {
	    CsvReader<DeviceLogEntry> reader = new CsvReader<DeviceLogEntry>(csvFilename) {

            @Override
            public DeviceLogEntry processRow(String csvRow) {
                return new DeviceLogEntry(csvRow);
            }
        };

        return reader.readAndParseFile();
    }

	private static List<SubmitHistoryEntry> parseSubmitHistoryFromCsv(String csvFilename,
                                                                      final String usernameOfInterest,
                                                                      final String deviceIdOfInterest,
                                                                      final Date startingDatetime) {
        CsvReader<SubmitHistoryEntry> reader = new CsvReader<SubmitHistoryEntry>(csvFilename) {

            @Override
            public SubmitHistoryEntry processRow(String csvRow) {
                SubmitHistoryEntry entry = new SubmitHistoryEntry(csvRow);
                if (usernameOfInterest.equals(entry.username) && deviceIdOfInterest.equals(entry.deviceId)
                        && startingDatetime.before(entry.completedOnDeviceTime)) {
                    return entry;
                } else {
                    return null;
                }
            }
        };

        return reader.readAndParseFile();
    }
	
	public static void main(String[] args) {
		String deviceLogsCsvFilename = "all-data-files/kawok-atv2~~atvae272~~DEVICE_LOGS.csv"; //args[0];
		String submitHistoryCsvFilename = "all-data-files/kawok-atv2~~FORMS_EXPORT.csv"; //args[1];
		findUnsubmittedForms(deviceLogsCsvFilename, submitHistoryCsvFilename);
        //System.out.println(DeviceLogEntry.removeCommasFromDateFields("\"Jan 25, 2017 22:11 CST\",\"Jan 27, 2017 17:57 CST\",form-entry,atvae272,atvae272,866836020291621,Form Entry Completed,unknown,2.30.0"));
	}

	private static void testDateParsing() {
        DeviceLogEntry.testDateParse("Jan 25 2017 22:08 CST"); // Thu Jan 26 06:08:00 SAST 2017
        DeviceLogEntry.testDateParse("Mar 20 2017 13:39 CST"); // Mon Mar 20 21:39:00 SAST 2017
        DeviceLogEntry.testDateParse("Mar 10 2017 18:28 CST"); // before daylight savings, Sat Mar 11 02:28:00 SAST 2017
        DeviceLogEntry.testDateParse("Mar 14 2017 10:31 CST"); // after daylight savings, Tue Mar 14 18:31:00 SAST 2017
        SubmitHistoryEntry.testDateParse("2017-03-29T08:14:01.461000Z");
        System.out.println(SubmitHistoryEntry.removeMillisecondDataFromDateString("2017-03-29T00:30:47.859179Z"));
    }

    private static void compareListSizes() {
        System.out.println("Number of device log entries for form completion: " + deviceLogsForFormCompletion.size());
        System.out.println("Number of sucessfully processed form submissions: " + submitHistoryList.size());
    }

    private static void getNumUniqueDeviceIds() {
        Set<String> uniqueDeviceIds = new HashSet<>();
        for (DeviceLogEntry entry : deviceLogsForFormCompletion) {
            uniqueDeviceIds.add(entry.deviceID);
        }
        System.out.println("Num unique device IDs in device log entries: " + uniqueDeviceIds.size());

        uniqueDeviceIds.clear();
        for (SubmitHistoryEntry entry : submitHistoryList) {
            uniqueDeviceIds.add(entry.deviceId);
        }
        System.out.println("Num unique device IDs in submit history entries: " + uniqueDeviceIds.size());
    }
}