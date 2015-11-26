package healthprofilebot.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServerResponse {

	private int chat_id;
	
	private String text;
	
	private String method;
	
	public ServerResponse(){
		
	}
	
	public ServerResponse(TelegramUpdate tUpdate){
		this.chat_id = tUpdate.message.chat.id;
	}

	public int getChat_id() {
		return chat_id;
	}

	public String getText() {
		return text;
	}

	public String getMethod(){
		return method;
	}
	
	public void setChat_id(int chat_id) {
		this.chat_id = chat_id;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void setMethod(String m) {
		this.method = m;
	}
	
}
