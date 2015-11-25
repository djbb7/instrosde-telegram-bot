package healthprofilebot.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServerResponse {

	public int chat_id;
	
	public String text;
	
	public ServerResponse(){
		
	}
}
