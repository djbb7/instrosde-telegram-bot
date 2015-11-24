package healthprofilebot.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class TelegramUpdate {

	public int update_id;
	
	public TelegramMessage message;
	
	public TelegramUpdate(){
		
	}
}
