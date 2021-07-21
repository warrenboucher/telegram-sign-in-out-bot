package telegram.vbot.bots;

import java.util.UUID;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import telegram.vbot.DataAccessObject;
import telegram.vbot.L;
import telegram.vbot.VConstants;
import telegram.vbot.json.VolunteerJSON;
import telegram.vbot.visitors.PublicMenuVisitor;

/**
 * Public Bot.  Restricted functionality can be used by anyone.
 */
public class PublicBot extends TelegramLongPollingBot {	

	/**
	 * 
	 */
	@Override
	public String getBotToken() {
		return DataAccessObject.getInstance().getConfiguration().telegram.publicBot.botToken;
	}

	/**
	 * 
	 */
	public String getBotUsername() {
		return DataAccessObject.getInstance().getConfiguration().telegram.publicBot.botUsername;
	}

	/**
	 * 
	 * @param update
	 */
	public void onUpdateReceived(Update update) {
		final String correlationId = UUID.randomUUID().toString();
		try {
			// We check if the update has a message and the message has text.
			if (!update.hasMessage() && update.getMessage().hasText()) {
				return;
			}
			final User user = update.getMessage().getFrom();
			L.getLogger(VConstants.LOG_PUBLIC).info(String.format("%s RX %d %s", correlationId, user.getId(), update.getMessage().getText()));
			final VolunteerJSON v = getVolunteer(user); 							
			v.visit(new PublicMenuVisitor(correlationId, update, this));
			v.currentMenuState = v.nextMenuState;
		}
		catch (Exception e) {
			L.getLogger(VConstants.LOG_PUBLIC).error(String.format("%s There was an error while processing the received message. %s", correlationId, e.getMessage()), e);
		}
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public VolunteerJSON getVolunteer(User user) {
		VolunteerJSON v = (VolunteerJSON) DataAccessObject.getInstance().getVolunteer(user.getId()); 
		if (null == v) {
			v = new VolunteerJSON(user);
			DataAccessObject.getInstance().addVolunteer(user.getId(), v);
			// Don't flush volunteers yet.  We don't need to keep state until they have done something.
		}
		return v;
	}
}