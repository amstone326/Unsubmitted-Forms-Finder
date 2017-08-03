import java.util.*;

public class DeviceLogForInstanceFinder {

	public static void main(String[] args) {
		String deviceLogsCsvFilename = args[0];
		String instanceIdToFind = args[1];
		List<DeviceLogEntry> deviceLogs = UnsubmittedFormsFinder.parseDeviceLogsFromCsv(deviceLogsCsvFilename);
		for (int i = 1; i < deviceLogs.size(); i++) {
			String logMessage = deviceLogs.get(i).logMessage;
			if (logMessage.contains(instanceIdToFind)) {
				System.out.println("FOUND log for instance id: " + logMessage + ", on date: " + deviceLogs.get(i).logDate);
			}
		}
	}

}