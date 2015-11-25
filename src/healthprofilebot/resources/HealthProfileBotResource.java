package healthprofilebot.resources;

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
    	System.out.println("Receiving message: "+update.message.text+" from:"+update.message.from.first_name+" chat_id:"+update.message.chat.id);
    	
    	//parse message content
    	String content = update.message.text.trim();
    	
    	if(content.startsWith("/")){
        	//execute command
    		HealthProfileMaster.getInstance().runTask(update);
    	} 
    	
		return Response.ok().build();
    }

}