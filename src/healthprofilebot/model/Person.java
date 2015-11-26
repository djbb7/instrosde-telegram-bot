package healthprofilebot.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO for handling Person response from server
 */
@XmlRootElement
public class Person {

	private int id;
	
	private String firstname;
	
	public String lastname;
	
	private Date birthdate;
	
	private String email; //TODO: Added for working with Fiorini's schema
	
//    @XmlElementWrapper(name="healthProfile")
    //TODO: Changed from "measurement" to "measure" to work with Fiorini's server
    //TODO: changed from "healthProfile" to "measure" to work with Fiorini's server
	public List<Measurement> measure;
	
	public Person(){
		
	}
	
	public int getId() {
		return id;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getEmail() {
		return email;
	}

	public List<Measurement> getMeasure() {
		return measure;
	}

	public Date getBirthdate(){
		return birthdate;
	}
	
	
	public void setBirthdate(Date d){
		birthdate = d;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setFirstname(String firstname) {
		if(firstname == null)
			firstname = "";
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		if(lastname == null)
			lastname = "";
		this.lastname = lastname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setMeasure(List<Measurement> measure) {
		this.measure = measure;
	}
	
	
}
