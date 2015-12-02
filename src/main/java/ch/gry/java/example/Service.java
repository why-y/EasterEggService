package ch.gry.java.example;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.gry.java.example.model.ServiceLogFormatter;

/**
 * Generic abstract service class
 * 
 * @author yvesgross
 */
public abstract class Service {

	private Logger logger;
	private boolean shutUp = false;
	// private static final long refT = System.nanoTime();

	public Service() {
		logger = Logger.getLogger(getClass().getName());
		logger.setUseParentHandlers(false);
		ServiceLogFormatter formatter = new ServiceLogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);
		logger.addHandler(handler);
	}

	public void log(final Object obj, final Level l) {
		if (shutUp)
			return;
		logger.log(l, obj.toString());
	}

	public void log(final Object obj) {
		log(obj, Level.INFO);
	}

}
