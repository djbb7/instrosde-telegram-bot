package worker;

import healthprofilebot.model.IdMatch;
import healthprofilebot.model.Measurement;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


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
		
		ServerResponse tResponse = new ServerResponse(job);
		
		System.out.println("[slave] checking db...");

		//check user id in database
		IdMatch match = IdMatch.getIdMatchByTelegramUserId(job.message.from.id);
		
		
		if(match == null && command.equals("/start")){			
			//Let user now what is happening
			tResponse.setText("Creating your health profile :). Please hold on.");
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
			
			//create remote health profile
			Person chuck = new Person();
			chuck.setFirstname(job.message.from.first_name);
			chuck.setLastname(job.message.from.last_name);
			//chuck.setEmail("");
			//chuck.measure = new ArrayList<Measurement>();
			//chuck.setBirthdate(new Date(2000, 4, 12));
			
			Response r = sendRequest(hpService, "/person", "POST", chuck); 
			System.out.println("[slave]"+r.getStatus());
			System.out.println("[slave]"+r.readEntity(String.class));
			
			if(r.getStatus() == 201){
				Person p = r.readEntity(Person.class);
				
				//store id in local database
				match = new IdMatch();
				match.setTelegramUserId(job.message.from.id);
				match.setHealthProfileId(p.getId());
				IdMatch.saveIdMatch(match);
			
				//Report profile created
				tResponse.setText("Done! Check out '/help' to see a list of available commands.");
			} else {
				tResponse.setText("I could not reach the HealthProfile web server. I'm sorry :)");
			}
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else if (match == null && !command.equals("/start")) {
			tResponse.setText("Please type command '/start' to create your health profile");
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else if (match != null && command.equals("/start")){
			tResponse.setText("Welcome back, "+job.message.from.first_name);
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else if (command.equals("/weight") || command.equals("/height") || command.equals("/blood")){
			boolean hasErrors = false;
			if(parts.length < 2){
				tResponse.setText("Be sure tu include the command name followed by the measurement value. Like this: \n"+
								  command+" 67");
				hasErrors = true;
			}
			try {
				Double.parseDouble(parts[1]);
			} catch (NumberFormatException e) {
				tResponse.setText("Are you trying to hack me :(? Please type the command followed by a numeric value. Like this: \n"+
								  command+" 68");
				hasErrors = true;
			}
			
			if(!hasErrors){
				String path = "/person/"+match.getHealthProfileId()+command;
				Measurement measure = new Measurement();
				measure.value = parts[1];
				Response r = sendRequest(hpService, path, "POST", measure);
				System.out.println("[slave]"+r.getStatus());
				System.out.println("[slave]"+r.readEntity(String.class));
				
				if(r.getStatus() == 200){
					tResponse.setText("Great, your measurement was stored. Keep up the good work :)");
				} else {
					tResponse.setText("I could not save your measurement. Could you try again?");
				}
			}
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else {
			//Error
			tResponse.setText("I don't understand this command");
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
			ObjectMapper mapper = new ObjectMapper();
			try {
				String debug = mapper.writeValueAsString(requestBody);

				System.out.println("[slave][request] JSON Content: "+ debug);
			} catch (JsonProcessingException e ) {
				System.out.println("Error processing JSON object");
			}
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
		System.out.println("[slave][request]["+response.getStatus()+"] "+response.readEntity(String.class));
		return response;
	}
}
