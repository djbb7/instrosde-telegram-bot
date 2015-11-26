package healthprofilebot.model;

/**
 * Used to respond to POST requests from telegram server
 * with a GET request containing a message, saving one
 * request alltogether.
 */
public class ServerResponseEfficient extends ServerResponse {

	private String method;
	
	public ServerResponseEfficient(TelegramUpdate tUpdate){
		super(tUpdate);
	}
	
	public String getMethodName(){
		return method;
	}
	
	public void setMethod(String m){
		method = m;
	}
}
