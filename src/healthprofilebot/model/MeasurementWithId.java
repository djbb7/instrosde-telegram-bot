package healthprofilebot.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO for handling Measurement response from Server
 */
@XmlRootElement(name="measure")//TODO: Added for working with Fiorini's schema
public class MeasurementWithId {
	
	private int mid;
	
	public Date created;
	
	@XmlElement(name="type")//TODO: Added for working with Fiorini's schema
	public String measure;
	
	public String value;
	
	public MeasurementWithId(){
		
	}
	
	public MeasurementWithId(String m, String v){
		measure = m;
		value = v;
	}
	
	public MeasurementWithId(String m, String v, Date d){
		measure = m;
		value = v;
		created = d;
	}
	
	public int getMid(){
		return mid;
	}
	
	public void setMid(int mid){
		this.mid = mid;
	}
}
