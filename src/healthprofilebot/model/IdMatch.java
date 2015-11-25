package healthprofilebot.model;

import healthprofilebot.dao.BotDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="USER")
public class IdMatch {

	@Id
	@Column(name="telegramUserId")
	private int telegramUserId;
	
	@Column(name="healthProfileId")
	private int healthProfileId;
	
	public IdMatch(){
		
	}
	
	public int getTelegramUserId(){
		return telegramUserId;
	}
	
	public int getHealthProfileId(){
		return healthProfileId;
	}
	
	public void setTelegramUserId(int id){
		telegramUserId = id;
	}
	
	public void setHealthProfileId(int id){
		healthProfileId = id;
	}
	
    /**
     * Retrieve a Person from the database by id
     * @param personId id of the Person
     * @return The Person if exists, else null
     */
    public static IdMatch getIdMatchByTelegramUserId(int telegramUserId) {
        EntityManager em = BotDao.instance.createEntityManager();
        IdMatch match = em.find(IdMatch.class, telegramUserId);
        BotDao.instance.closeConnections(em);
        return match;
    }
    
    /**
     * Save a new Person to the database
     * @param p Person to be saved
     * @return Returns a copy of the Person object, with id set
     */
    public static IdMatch saveIdMatch(IdMatch m) {
        EntityManager em = BotDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(m);
        tx.commit();
        BotDao.instance.closeConnections(em);
        return m;
    } 
}
