package healthprofilebot.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class TelegramChat {

		public int id;
		
		public TelegramChat(){
			
		}
		
}
