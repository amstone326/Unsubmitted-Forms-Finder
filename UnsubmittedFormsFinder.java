import java.util.*;

public class UnsubmittedFormsFinder {

    private static List<DeviceLogEntry> fullDeviceLogsList;
    private static List<DeviceLogEntry> deviceLogsForFormCompletion;
    private static List<SubmitHistoryEntry> submitHistoryList;

    private static Map<String, String[]> domainToCsvFilesMap;
    private static Map<String, SubmitHistoryEntry> instanceIdToSubmissionMap;


    static {
        domainToCsvFilesMap = new HashMap<>();

        domainToCsvFilesMap.put("oaf-burundi-qdv",
                new String[]{"data-files/oaf-burundi-qdv~~oaf_burundi_38~~DEVICE_LOGS.csv",
                             "data-files/oaf-burundi-qdv~~FORMS_EXPORT.csv"});

        // RESULT: All logs had a match
        domainToCsvFilesMap.put("kawok-atv2",
                new String[]{"data-files/kawok-atv2~~atvae272~~DEVICE_LOGS.csv",
                             "data-files/kawok-atv2~~FORMS_EXPORT.csv"});
    }

    private static void findUnsubmittedForms(String deviceLogsCsvFilename, String submitHistoryCsvFilename) {
		fullDeviceLogsList = parseDeviceLogsFromCsv(deviceLogsCsvFilename);
		deviceLogsForFormCompletion = DeviceLogEntry.getEntriesForFormCompletionOnly(fullDeviceLogsList);

        String usernameOfInterest = deviceLogsForFormCompletion.get(0).username;
        String deviceIdOfInterest = deviceLogsForFormCompletion.get(0).deviceID;
        Date startingDatetime = deviceLogsForFormCompletion.get(0).logDate;
		submitHistoryList =
                parseSubmitHistoryFromCsv(submitHistoryCsvFilename, usernameOfInterest, deviceIdOfInterest,
                        startingDatetime);
		createInstanceIdToSubmissionMap();

		flagIfMultipleDeviceIDs();
		compareListSizes();
		flagMissingSubmissions();
	}

	private static void createInstanceIdToSubmissionMap() {
        instanceIdToSubmissionMap = new HashMap<>();
        for (SubmitHistoryEntry submission : submitHistoryList) {
            instanceIdToSubmissionMap.put(submission.formInstanceId, submission);
        }
    }

    private static void flagIfMultipleDeviceIDs() {
        Set<String> uniqueDeviceIds = new HashSet<>();
        for (DeviceLogEntry entry : deviceLogsForFormCompletion) {
            if (entry.deviceID != null) {
                uniqueDeviceIds.add(entry.deviceID);
            }
        }
        if (uniqueDeviceIds.size() > 1) {
            System.out.println("FLAGGING: More than 1 device ID in device log entries: " + uniqueDeviceIds);
        }

        uniqueDeviceIds.clear();
        for (SubmitHistoryEntry entry : submitHistoryList) {
            if (entry.deviceId != null) {
                uniqueDeviceIds.add(entry.deviceId);
            }
        }
        if (uniqueDeviceIds.size() > 1) {
            System.out.println("FLAGGING: More than 1 device ID in submit history entries: " + uniqueDeviceIds);
        }
    }

    private static void compareListSizes() {
        System.out.println("Number of device log entries for form completion: " + deviceLogsForFormCompletion.size());
        System.out.println("Number of successfully processed form submissions: " + submitHistoryList.size());
    }

	private static void flagMissingSubmissions() {
	    for (DeviceLogEntry logEntry : deviceLogsForFormCompletion) {
            logEntry.findClosestMatchingSubmissionOrFlag(instanceIdToSubmissionMap);
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
                if (usernameOfInterest.equals(entry.username) && startingDatetime.before(entry.completedOnDeviceTime)
                        && (entry.deviceId == null || entry.deviceId.equals(deviceIdOfInterest))) {
                    return entry;
                } else {
                    return null;
                }
            }
        };

        return reader.readAndParseFile();
    }
	
	public static void main(String[] args) {
        String domain = args[0];
        String[] csvFiles = domainToCsvFilesMap.get(domain);
        if (csvFiles == null) {
            System.out.println("Invalid domain provided");
            return;
        }
		String deviceLogsCsvFilename = csvFiles[0];
		String submitHistoryCsvFilename = csvFiles[1];
		findUnsubmittedForms(deviceLogsCsvFilename, submitHistoryCsvFilename);
	}

	private static void testDateParsing() {
        DeviceLogEntry.testDateParse("Jan 25 2017 22:08 CST"); // Thu Jan 26 06:08:00 SAST 2017
        DeviceLogEntry.testDateParse("Mar 20 2017 13:39 CST"); // Mon Mar 20 21:39:00 SAST 2017
        DeviceLogEntry.testDateParse("Mar 10 2017 18:28 CST"); // before daylight savings, Sat Mar 11 02:28:00 SAST 2017
        DeviceLogEntry.testDateParse("Mar 14 2017 10:31 CST"); // after daylight savings, Tue Mar 14 18:31:00 SAST 2017
        SubmitHistoryEntry.testDateParse("2017-03-29T08:14:01.461000Z");
        System.out.println(SubmitHistoryEntry.removeMillisecondDataFromDateString("2017-03-29T00:30:47.859179Z"));
    }

    private static void listFirstN(int N) {
        for (int i = 0; i < N; i++) {
            System.out.println("-----");
            DeviceLogEntry formCompletionLogEntry = deviceLogsForFormCompletion.get(i);
            SubmitHistoryEntry submission = submitHistoryList.get(i);
            System.out.println("Device Log DateTime: " + formCompletionLogEntry.logDate);
            System.out.println("Submission Completed DateTime: " + submission.completedOnDeviceTime);
            System.out.println("-----");
        }
    }

}