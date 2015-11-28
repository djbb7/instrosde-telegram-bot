package worker;

import healthprofilebot.model.TelegramUpdate;

public enum HealthProfileMaster {
	INSTANCE;
		
	HealthProfileMaster(){
	}
	
	public static HealthProfileMaster getInstance(){
		return INSTANCE;
	}
	
	public void runTask(TelegramUpdate job){
		System.out.println(">>..[master] Starting worker thread");
		//start worker thread
		HealthProfileWorker worker = new HealthProfileWorker(job);
		worker.run();
	}
	
}
