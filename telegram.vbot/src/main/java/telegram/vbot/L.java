package telegram.vbot;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Logger.
 */
public class L {

	private static final java.util.Map<String, Logger> fileLoggers = new HashMap<>();

	/**
	 * 
	 */
	private L() {
		
	}
	
	/***
	 * 
	 * @param loggerName
	 * @return
	 */
	public static synchronized Logger getLogger(String loggerName) {
		if (fileLoggers.containsKey(loggerName)) {
			return fileLoggers.get(loggerName);
		}
		return initLogger(loggerName);
	}

	/**
	 * 
	 * @param loggerName
	 */
	private static Logger initLogger(String loggerName) {
		final String FILE_APPENDER_NAME = loggerName + "FileAppender";
		final String logLevel = "INFO";
		final Properties loggerProperties = new Properties();
		Logger logger;

		loggerProperties.setProperty("log4j.appender." + FILE_APPENDER_NAME, "org.apache.log4j.RollingFileAppender");
		loggerProperties.setProperty("log4j.appender." + FILE_APPENDER_NAME + ".File", "./logs/" + loggerName + ".log");
		loggerProperties.setProperty("log4j.appender." + FILE_APPENDER_NAME + ".MaxFileSize", "20MB");
		loggerProperties.setProperty("log4j.appender." + FILE_APPENDER_NAME + ".MaxBackupIndex", "3");
		loggerProperties.setProperty("log4j.appender." + FILE_APPENDER_NAME + ".Append", "true");
		loggerProperties.setProperty("log4j.appender." + FILE_APPENDER_NAME + ".bufferedIO", "false");
		loggerProperties.setProperty("log4j.appender." + FILE_APPENDER_NAME + ".layout", "org.apache.log4j.PatternLayout");
		loggerProperties.setProperty("log4j.appender." + FILE_APPENDER_NAME + ".layout.ConversionPattern", "[%d{HH:mm:ss.SSS}][%-5p] %X{id} %m%n");

		loggerProperties.setProperty("log4j.logger." + loggerName, logLevel + ", " + loggerName + "FileAppender");

		PropertyConfigurator.configure(loggerProperties);
		logger = LogManager.getLogger(loggerName);
		logger.setAdditivity(true);

		fileLoggers.put(loggerName, logger);
		return logger;
	}
}
