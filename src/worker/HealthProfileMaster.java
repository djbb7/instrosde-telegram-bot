package worker;

import healthprofilebot.model.TelegramUpdate;

import java.util.Arrays;

public enum HealthProfileMaster {
	INSTANCE;

	private static String[] validCommands = {"/start", "/weight", "/height", "/blood"};
		
	HealthProfileMaster(){
	}
	
	public static HealthProfileMaster getInstance(){
		return INSTANCE;
	}
	
	public void runTask(TelegramUpdate job){
		System.out.println("Starting worker thread");
		//start worker thread
		HealthProfileWorker worker = new HealthProfileWorker(job);
		worker.run();
	}
	
	public static boolean isValidCommand(String command){
		String[] parts = command.split(" ");
		return Arrays.asList(validCommands).contains(parts[0]);
	}
}
