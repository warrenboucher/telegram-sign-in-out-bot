package telegram.vbot.json;

import java.io.IOException;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.gson.JsonIOException;

import telegram.vbot.VConstants;
import telegram.vbot.visitors.PublicMenuVisitor;

public class VolunteerJSON extends AbstractPersonJSON {

	public int currentMenuState = VConstants.MENU_MAIN;
	public int nextMenuState = VConstants.MENU_MAIN;
		
	public String vId = null;
	public boolean active = false;
	public long activeSince;
	public String checkpointId = null;
			
	/**
	 * 
	 * @param user
	 */
	public VolunteerJSON(User user) {
		super(user);
	}
	
	/**
	 * 
	 * @param visitor
	 * @throws IOException 
	 * @throws JsonIOException 
	 */
	public void visit(PublicMenuVisitor visitor) throws TelegramApiException, JsonIOException, IOException {
		visitor.accept(this);
	}
}