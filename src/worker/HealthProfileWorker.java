package worker;

import healthprofilebot.model.ServerResponse;
import healthprofilebot.model.TelegramUpdate;

public class HealthProfileWorker implements Runnable{

	private TelegramUpdate job;
	
	public HealthProfileWorker(TelegramUpdate job){
		this.job = job;
	}
	
	@Override
	public void run() {
		//open message
		String message = job.message.text.trim();
		String[] parts = message.split(" ");
		String command = parts[0];
		
		ServerResponse tResponse = new ServerResponse();
		tResponse.chat_id = job.message.chat.id;
		tResponse.method = "sendMessage";
		
		if(command.equals("/start")){
			
		} else if (command.equals("/weight")){
			
		} else if (command.equals("/height")){
			
		} else if (command.equals("/blood")){
			
		} else {
			//Error
			tResponse.text = "I don't understand this command";
		}
		
		//check user id in database
		
		//execute request against remote server
		
		//return result
		tResponse.text = "Welcome to the Health Manager!";
    	
	}

}
