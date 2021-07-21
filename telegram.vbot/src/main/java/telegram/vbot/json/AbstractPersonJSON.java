package telegram.vbot.json;

import org.telegram.telegrambots.meta.api.objects.User;

public abstract class AbstractPersonJSON {
	public String firstname;
	public String lastname;
	public Long id;
	
	/**
	 * 
	 * @param user
	 */
	public AbstractPersonJSON(User user) {
		this.firstname = user.getFirstName();
		this.lastname = user.getLastName();
		this.id = user.getId();
	}
}