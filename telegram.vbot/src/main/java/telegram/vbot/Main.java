package telegram.vbot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import telegram.vbot.bots.AdministratorBot;
import telegram.vbot.bots.PublicBot;

public class Main {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new PublicBot());
			telegramBotsApi.registerBot(new AdministratorBot());
			L.getLogger(VConstants.LOG_APPLICATION).info("Application Started");
		} 
		catch (TelegramApiException e) {
			L.getLogger(VConstants.LOG_APPLICATION).error(String.format("Exception while starting application %s", e.getMessage()), e);
		}
	}

}
