import java.util.*;

public class DeviceLogFilterer {

	public static void main(String[] args) {
		String deviceLogsCsvFilename = args[0];
		String filterType = args[1];
		String filterArg = args[2];
		if ("instanceId".equals(filterType)) {
			filterForInstanceId(deviceLogsCsvFilename, filterArg);
		} else if ("filterOutMessage".equals(filterType)) {
			filterOutLogsWithMessage(deviceLogsCsvFilename, filterArg);
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

}