package healthprofilebot.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TelegramMessage {

	public int message_id;
	public TelegramUser from;
	public int date;
	public TelegramChat chat;
	public String text;
	
	public TelegramMessage(){
		
	}
}
