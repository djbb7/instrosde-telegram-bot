package worker;

import healthprofilebot.model.IdMatch;
import healthprofilebot.model.LastCommand;
import healthprofilebot.model.Measurement;
import healthprofilebot.model.MeasurementWithId;
import healthprofilebot.model.Person;
import healthprofilebot.model.ServerResponse;
import healthprofilebot.model.TelegramUpdate;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
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
		System.out.println(">>..[slave] initialized");
	}

	@Override
	public void run() {
		System.out.println(">>..[slave] Running task");
		//open message
		String message = job.message.text.trim();
		String[] parts = message.split(" ");
		String command = parts[0];
		boolean hasErrors = false;

		ServerResponse tResponse = new ServerResponse(job);

		
		if (command.equals("/help")){
			String instructions = "Please enter one of the following commands: \n"
					+ "*/start* to initialize your health profile\n"
					+ "*/weight* to enter a weight measurement\n"
					+ "*/height* to enter a height measurement\n"
					+ "*/blood* to enter a blood pressure measurement\n"
					+ "*/weight_history* to check the list of weight measurements\n"
					+ "*/height_history* to check the list of height measurements\n"
					+ "*/blood_history* to check the list of blood pressure measurements\n";
			tResponse.setText(instructions);
			tResponse.setParse_mode("Markdown");
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		}
		
		
		System.out.println(">>..[slave] checking db...");

		//check user id in database
		IdMatch match = IdMatch.getIdMatchByTelegramUserId(job.message.from.id);

		System.out.println(">>..[slave] checking command history...");
		LastCommand lastCmd = LastCommand.getLastCommand(job.message.from.id);
		
		
		if(lastCmd != null && job.update_id<= lastCmd.getUpdate_id()){	
			System.out.println(">>..[slave] old command");
			return;
		}
		
		System.out.println(">>..[slave] processing command...");
		if(match == null){			
			//Let user now what is happening
			System.out.println(">>..[slave] Create health profile...");
			tResponse.setText("Creating your health profile :). Please hold on.");
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);

			//create remote health profile
			Person chuck = new Person();
			chuck.setFirstname(job.message.from.first_name);
			chuck.setLastname(job.message.from.last_name);

			Response r = sendRequest(hpService, "/person", "POST", chuck); 
			System.out.println(">>..[slave] Fiorini status: "+r.getStatus());
			System.out.println(">>..[slave] Fiorini response: "+r.readEntity(String.class));

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
				tResponse.setText("I could not reach the HealthProfile web server. I'm sorry :|");
				hasErrors = true;
			}
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		}
		
		if(hasErrors){
			
		} else if (command.equals("/start")){
			System.out.println(">>..[slave] Health Profile already exists: "+command);
		} else if (command.equals("/weight") || command.equals("/height") || command.equals("/blood")){
			System.out.println(">>..[slave] First step add measure: "+command);
			//save command received
			tResponse.setText("Please enter the measurement value.");
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else if (command.equals("/weight_history") || command.equals("/height_history") || command.equals("/blood_history")){ 
			System.out.println(">>..[slave] Get history: "+command);
			String measureType = null;
			if(command.equals("/weight_history")){
				measureType = "weight";
			} else if (command.equals("/weight_history")){
				measureType = "height";
			} else {
				measureType = "bloodpressure";
			}
			Response r = sendRequest(hpService, "/person/"+match.getHealthProfileId()+"/"+measureType, "GET", null);
			System.out.println(">>..[slave] Fiorini get measure history: "+r.getStatus());
			System.out.println(">>..[slave] Fiorini get measure history response: "+r.readEntity(String.class));

			if(r.getStatus() == 200){
				List<MeasurementWithId> history = r.readEntity(new GenericType<List<MeasurementWithId>>() {});
				String res = "";
				for(MeasurementWithId m : history){
					res += m.value+", ";
				}
				tResponse.setText("History: "+res);
			} else {
				tResponse.setText("I could not fetch the measure history. Could you try again?");
			}
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		} else if (lastCmd != null && (lastCmd.getCommand().equals("/weight") 
									|| lastCmd.getCommand().equals("/height") 
									|| lastCmd.getCommand().equals("/blood"))){
			System.out.println(">>..[slave] Second step add measure: "+command);

			try {
				//save measurement
				double value = Double.parseDouble(command);
				Measurement measure = new Measurement();
				measure.value = command;
				String path = lastCmd.getCommand();
				if(path.equals("/blood")) //rename to ACTUAL name on Web Server.
					path = "/bloodpressure";

				Response r = sendRequest(hpService, "/person/"+match.getHealthProfileId()+path, "POST", measure);
				System.out.println(">>..[slave] Fiorini add measure status: "+r.getStatus());
				System.out.println(">>..[slave] Fiorini add measure response: "+r.readEntity(String.class));

				if(r.getStatus() == 200){
					tResponse.setText("Great, your measurement was stored. Keep up the good work :)");
				} else {
					tResponse.setText("I could not save your measurement. Could you try again?");
					hasErrors = true;
				}
				sendRequest(telegramService, "/sendMessage", "POST", tResponse);
			} catch (NumberFormatException e) {
				System.out.println(">>..[slave] Measurment value not numeric: "+command);

				tResponse.setText("Are you trying to hack me :(? Please type a numeric value.");
				sendRequest(telegramService, "/sendMessage", "POST", tResponse);
				hasErrors = true;
			}
		} else {
			System.out.println(">>..[slave] Unknown command: "+command);

			//Error
			tResponse.setText("I don't understand this command");
			sendRequest(telegramService, "/sendMessage", "POST", tResponse);
		}
		
		if(!hasErrors){
			//update command database for user
			LastCommand cmd = new LastCommand(job.message.from.id, command);
			LastCommand.updateLastCommand(cmd);
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
