import java.util.*;

public class DeviceLogFilterer {

	public static void main(String[] args) {
		String deviceLogsCsvFilename = args[0];
		String filterType = args[1];
		String filterArg = args.length > 2 ? args[2] : null;
		if ("instanceId".equals(filterType)) {
			filterForInstanceId(deviceLogsCsvFilename, filterArg);
		} else if ("filterOutMessage".equals(filterType)) {
			filterOutLogsWithMessage(deviceLogsCsvFilename, filterArg);
		} else if ("findDuplicates".equals(filterType)) {
			findDuplicateLogs(deviceLogsCsvFilename, filterArg);
		}
		
	}

	private static void filterForInstanceId(String deviceLogsCsvFilename, String instanceIdToFind) {
		List<DeviceLogEntry> deviceLogs = UnsubmittedFormsFinder.parseDeviceLogsFromCsv(deviceLogsCsvFilename);
		for (int i = 1; i < deviceLogs.size(); i++) {
			String logMessage = deviceLogs.get(i).logMessage;
			if (logMessage.contains(instanceIdToFind)) {
				System.out.println("FOUND log for instance id: " + logMessage + ", on date: " + deviceLogs.get(i).logDate);
			}
		}
	}

	private static void filterOutLogsWithMessage(String deviceLogsCsvFilename, String undesiredLogMessage) {
		List<DeviceLogEntry> deviceLogs = UnsubmittedFormsFinder.parseDeviceLogsFromCsv(deviceLogsCsvFilename);
		for (int i = 1; i < deviceLogs.size(); i++) {
			String logMessage = deviceLogs.get(i).logMessage;
			if (!logMessage.contains(undesiredLogMessage)) {
				System.out.println("");
				System.out.println("Message: " + logMessage + ", on date: " + deviceLogs.get(i).logDate);
			}
		}
	}

	private static void findDuplicateLogs(String deviceLogsCsvFilename, String logTypeToPrint) {
		List<DeviceLogEntry> deviceLogs = UnsubmittedFormsFinder.parseDeviceLogsFromCsv(deviceLogsCsvFilename);
		List<DeviceLogEntry> duplicates = new ArrayList<>();
		Map<String, Integer> usernameToDuplicatesCount = new HashMap<>();
		Set<String> logTypesWithDuplicates = new HashSet<>();
		String lastLogMessage = null;
		Date lastLogDate = null;
		for (int i = 1; i < deviceLogs.size(); i++) {
			DeviceLogEntry thisLog = deviceLogs.get(i);
			String message = thisLog.logMessage;
			Date date = thisLog.logDate;
			if (message.equals(lastLogMessage) && date.equals(lastLogDate)) {
				duplicates.add(thisLog);
				logTypesWithDuplicates.add(thisLog.logType);
				if (thisLog.username != "Unknown") {
					Integer currentCount = usernameToDuplicatesCount.get(thisLog.username);
					usernameToDuplicatesCount.put(thisLog.username, currentCount == null ? 1 : currentCount + 1);
				}
			}
			lastLogMessage = message;
			lastLogDate = date;
		}

		System.out.println("Log Types with duplicates:");
		for (String type : logTypesWithDuplicates) {
			System.out.println(type);
		}

		Set<String> usersWithManyDuplicates = findUsersWithManyDuplicates(usernameToDuplicatesCount);

		System.out.println();
		for (DeviceLogEntry log : duplicates) {
			if ((logTypeToPrint == null || log.logType.equals(logTypeToPrint)) &&
					!usersWithManyDuplicates.contains(log.username)) {
				log.printImportantInfo();
			}
		}
	}

	private static Set<String> findUsersWithManyDuplicates(Map<String, Integer> usernameToDuplicatesCount) {
		Set<String> usersWithManyDuplicates = new HashSet<>();
		for (String username : usernameToDuplicatesCount.keySet()) {
			if (usernameToDuplicatesCount.get(username) >= 10) {
				usersWithManyDuplicates.add(username);
			}
		}
		return usersWithManyDuplicates;
	}

}