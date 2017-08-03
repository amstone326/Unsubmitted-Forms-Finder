import java.util.*;

public class SkippedFormNumberFinder {

	// csv file names

	public static void main(String[] args) {
		String deviceLogsCsvFilename = args[0];
		boolean foundSubmitSuccessMessageForCurrentNumber = false;
		List<DeviceLogEntry> deviceLogs = UnsubmittedFormsFinder.parseDeviceLogsFromCsv(deviceLogsCsvFilename);
		int lastSubmissionOrderingNumber = parseOutSubmissionNumber(deviceLogs.get(0));
		for (int i = 1; i < deviceLogs.size(); i++) {
			
			int submissionNumber = parseOutSubmissionNumber(deviceLogs.get(i));
			//System.out.println(submissionNumber);

			// When we move to our next number, check if we found a submit message for the prior one
			if (submissionNumber > lastSubmissionOrderingNumber) {
				if (!foundSubmitSuccessMessageForCurrentNumber) {
					System.out.println("FOUND NO SUBMIT: " + lastSubmissionOrderingNumber + ", on " + deviceLogs.get(i).logDate);
				} 
				// now reset it
				foundSubmitSuccessMessageForCurrentNumber = false;
			} 

			if (deviceLogs.get(i).logMessage.contains("Successfully submitted")) {
				//System.out.println("found submit success msg");
				foundSubmitSuccessMessageForCurrentNumber = true;
			}
			
			if (submissionNumber > lastSubmissionOrderingNumber + 1) {
				System.out.println("FOUND SKIP: Last submission number was " 
					+ lastSubmissionOrderingNumber + ", this one is " + submissionNumber 
					+ ", on " + deviceLogs.get(i).logDate);
			}
			
			lastSubmissionOrderingNumber = submissionNumber;
		}
	}

	private static int parseOutSubmissionNumber(DeviceLogEntry log) {
		String logMessage = log.logMessage;
		String submissionNumberString = logMessage.substring(logMessage.lastIndexOf(" ") + 1);
		return Integer.parseInt(submissionNumberString);
	}
}