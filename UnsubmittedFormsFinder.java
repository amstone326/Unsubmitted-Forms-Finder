import java.util.ArrayList;
import java.util.List;

public class UnsubmittedFormsFinder {

	private static void findUnsubmittedForms(String deviceLogsCsvFilename, String submitHistoryCsvFilename) {
		List<DeviceLogEntry> fullDeviceLogsList = parseDeviceLogsFromCsv(deviceLogsCsvFilename);
		List<DeviceLogEntry> deviceLogsForFormCompletion = DeviceLogEntry.getEntriesForFormCompletionOnly(fullDeviceLogsList);
		String usernameOfInterest = deviceLogsForFormCompletion.get(0).username;
		List<SubmitHistoryEntry> submitHistoryList = parseSubmitHistoryFromCsv(submitHistoryCsvFilename, usernameOfInterest);
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
                                                                      final String usernameOfInterest) {
        CsvReader<SubmitHistoryEntry> reader = new CsvReader<SubmitHistoryEntry>(csvFilename) {

            @Override
            public SubmitHistoryEntry processRow(String csvRow) {
                String username = csvRow.split(",")[2];
                if (usernameOfInterest.equals(username)) {
                    return new SubmitHistoryEntry(csvRow);
                } else {
                    return null;
                }
            }
        };

        return reader.readAndParseFile();
    }
	
	public static void main(String[] args) {
		String deviceLogsCsvFilename = "data-files/kawok-atv2~~atvae272~~DEVICE_LOGS.csv"; //args[0];
		String submitHistoryCsvFilename = ""; //args[1];
		//findUnsubmittedForms(deviceLogsCsvFilename, submitHistoryCsvFilename);
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
}