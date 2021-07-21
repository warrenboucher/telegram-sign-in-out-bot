package telegram.vbot.bots;

import java.util.UUID;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import telegram.vbot.DataAccessObject;
import telegram.vbot.L;
import telegram.vbot.VConstants;

public class AdministratorBot extends TelegramLongPollingBot {
	
	/**
	 * 
	 */
	@Override
	public String getBotToken() {
		return DataAccessObject.getInstance().getConfiguration().telegram.administratorBot.botToken;
	}

	/**
	 * 
	 */
	public String getBotUsername() {
		return DataAccessObject.getInstance().getConfiguration().telegram.administratorBot.botUsername;
	}

	/**
	 * 
	 */
	public void onUpdateReceived(Update update) {
		final String correlationId = UUID.randomUUID().toString();
		try {
			// We check if the update has a message and the message has text.
			if (!update.hasMessage() && update.getMessage().hasText()) {
				return;
			}
			
			final User user = update.getMessage().getFrom();
			
		} 
		catch (Exception e) {
			L.getLogger(VConstants.LOG_ADMINISTRATOR).error(String.format("%s There was an error while processing the received message. %s", correlationId, e.getMessage()));
		}	
	}
}