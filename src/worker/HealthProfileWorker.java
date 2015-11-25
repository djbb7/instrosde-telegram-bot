package worker;

import healthprofilebot.model.IdMatch;
import healthprofilebot.model.Person;
import healthprofilebot.model.ServerResponse;
import healthprofilebot.model.TelegramUpdate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;


public class HealthProfileWorker implements Runnable{

	private TelegramUpdate job;
	
	private static WebTarget hpService, telegramService;
	
	
	private static String telegramServer = "https://api.telegram.org/bot178041124:AAFe4zG7TpdBtrImZrv_6hNOUgYUNI4VSNQ";
	
	private static String healthProfileServer = "https://damp-cliffs-5416.herokuapp.com/rest";
	
	public HealthProfileWorker(TelegramUpdate job){
		this.job = job;
		
		ClientConfig clientConfig = new ClientConfig();
		//Long timeout in case heroku server is sleeping
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60*1000);
		Client client = ClientBuilder.newClient(clientConfig);
		
		//setup health profile server
		hpService = client.target(healthProfileServer);
		
		//setup telegram server
		telegramService = client.target(telegramServer);
		System.out.println("[slave] initialized");
	}
	
	@Override
	public void run() {
		System.out.println("[slave] Running task");
		//open message
		String message = job.message.text.trim();
		String[] parts = message.split(" ");
		String command = parts[0];
		
		ServerResponse tResponse = new ServerResponse();
		tResponse.chat_id = job.message.chat.id;
		
		System.out.println("[slave] checking db...");

		//check user id in database
		IdMatch match = IdMatch.getIdMatchByTelegramUserId(job.message.from.id);
		
		
		if(match == null && command.equals("/start")){			
			//Let user now what is happening
			tResponse.text = "Creating your health profile :). Please hold on.";
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
			
			//create remote health profile
			Person chuck = new Person();
			chuck.firstname = job.message.from.first_name;
			chuck.lastname = job.message.from.last_name;
			Response r = sendRequest(hpService, "/person", "POST", chuck); 
			System.out.println("[slave]"+r.readEntity(String.class));
			
			Person p = r.readEntity(Person.class);
			
			//store id in local database
			match = new IdMatch();
			match.setTelegramUserId(job.message.from.id);
			match.setHealthProfileId(p.id);
			IdMatch.saveIdMatch(match);
			
			//Report profile created
			tResponse.text = "Done! Check out '/help' to see a list of available commands.";
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else if (match == null && !command.equals("/start")) {
			tResponse.text = "Please type command '/start' to create your health profile";
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else if (match != null && command.equals("/start")){
			tResponse.text = "Welcome back, "+job.message.from.first_name;
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else if (command.equals("/weight")){
			
		} else if (command.equals("/height")){
			
		} else if (command.equals("/blood")){
			
		} else {
			//Error
			tResponse.text = "I don't understand this command";
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		}
    	
	}

	private static Response sendRequest(WebTarget target, String URIPath,  String method, Object requestBody){
		Response response = null;
		Invocation.Builder builder = target.path(URIPath)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.header("Content-Type", MediaType.APPLICATION_JSON);
		
		Entity<Object> body = null;
		if(requestBody != null){
			body = Entity.json(requestBody);
		}
		
		if(method.equals("GET")){
			response = builder.get();
		} else if(method.equals("POST")){
			response = builder.post(body);
		} else if(method.equals("PUT")){
			response = builder.put(body);
		} else if(method.equals("DELETE")){
			response = builder.delete();
		} else {
			throw new RuntimeException("Unexpected HTTP method: "+method);
		}
		
		response.bufferEntity();
		System.out.println(response.readEntity(String.class));
		return response;
	}
}
