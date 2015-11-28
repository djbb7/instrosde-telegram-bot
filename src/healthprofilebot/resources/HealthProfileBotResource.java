package healthprofilebot.resources;

import healthprofilebot.model.LastCommand;
import healthprofilebot.model.TelegramUpdate;

import java.io.IOException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import worker.HealthProfileMaster;

@Stateless
@LocalBean
@Path("/")
public class HealthProfileBotResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    
    /**
     * Gets called by telegram every time there is an update
     * @param update
     * @return
     * @throws IOException
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response newPerson(TelegramUpdate update)  {
    	System.out.println("\n>>[endpoint] Receiving message: "+update.message.text+" from:"+update.message.from.first_name+" update_id:"+update.update_id);
    	
    	//execute command
	//	HealthProfileMaster.getInstance().runTask(update);
    	
    	
    	try {
    		LastCommand lc = LastCommand.getLastCommand(update.message.from.id);
        	if(lc != null){
        		System.out.println(">>[endpoint] Last command: "+lc.getCommand());
        	} else {
        		System.out.println(">>[endpoint] No last command stored.");
        	}
        	
    		LastCommand.updateLastCommand(new LastCommand(update.message.from.id, update.message.text));
    		
    		lc = LastCommand.getLastCommand(update.message.from.id);
        	if(lc != null){
        		System.out.println(">>[endpoint] Last command: "+lc.getCommand());
        	} else {
        		System.out.println(">>[endpoint] No last command stored.");
        	}
        	
    	} catch (Exception e){
    		System.out.println("Something happened..."+e.getMessage());
    	}
    		System.out.println(">>[endpoint] master called");
		return Response.ok().build();
    }

}