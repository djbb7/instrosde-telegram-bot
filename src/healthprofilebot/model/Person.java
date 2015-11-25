package healthprofilebot.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO for handling Person response from server
 */
@XmlRootElement
public class Person {

	public int id;
	
	public String firstname;
	
	public String lastname;
	
	private Date birthdate;
	
	public String email; //TODO: Added for working with Fiorini's schema
	
//    @XmlElementWrapper(name="healthProfile")
    //TODO: Changed from "measurement" to "measure" to work with Fiorini's server
    //TODO: changed from "healthProfile" to "measure" to work with Fiorini's server
	public List<Measurement> measure;
	
	public Person(){
		
	}
	
	public Date getBirthdate(){
		return birthdate;
	}
	
	public void setBirthdate(Date d){
		birthdate = d;
	}
}
