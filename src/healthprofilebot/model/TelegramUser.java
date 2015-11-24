package healthprofilebot.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TelegramUser {

	public int id;
	public String first_name;
	public String last_name;
	public String username;
	
	public TelegramUser(){
		
	}
}
