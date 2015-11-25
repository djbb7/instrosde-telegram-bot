package healthprofilebot.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO for handling Measurement response from Server
 */
@XmlRootElement(name="measure")//TODO: Added for working with Fiorini's schema
public class Measurement {
	
	//private int mid;
	
	public Date created;
	
	@XmlElement(name="type")//TODO: Added for working with Fiorini's schema
	public String measure;
	
	public String value;
	
	
	public Measurement(){
		
	}
	
	public Measurement(String m, String v){
		measure = m;
		value = v;
	}
	
	public Measurement(String m, String v, Date d){
		measure = m;
		value = v;
		created = d;
	}
	
	/*public int getMid(){
		return mid;
	}
	
	public void setMid(int mid){
		this.mid = mid;
	}*/
}