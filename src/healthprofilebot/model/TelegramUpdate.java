package healthprofilebot.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TelegramUpdate {

	public int update_id;
	
	public TelegramMessage message;
}
