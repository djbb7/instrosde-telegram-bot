package healthprofilebot.model;

import healthprofilebot.dao.BotDao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="COMMAND")
public class LastCommand {

	@Id
	@Column(name="telegramUserid")
	private int telegramUserId;
	
	@Column(name="command")
	private String command;
	
	public LastCommand(){
		
	}
	
	public LastCommand(int id, String command){
		this.telegramUserId = id;
		this.command = command;
	}

	public int getTelegramUserId() {
		return telegramUserId;
	}

	public String getCommand() {
		return command;
	}

	public void setTelegramUserId(int telegramUserId) {
		this.telegramUserId = telegramUserId;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	

    public static LastCommand getLastCommand(int telegramUserId) {
        EntityManager em = BotDao.instance.createEntityManager();
        LastCommand match = em.find(LastCommand.class, telegramUserId);
        BotDao.instance.closeConnections(em);
        return match;
    }
	
    public static LastCommand updateLastCommand(LastCommand c) {
        EntityManager em = BotDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.merge(c);
        tx.commit();
        BotDao.instance.closeConnections(em);
        return c;
    } 
}
