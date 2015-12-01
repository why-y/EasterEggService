package ch.gry.java.example;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic abstract service class
 * @author yvesgross
 */
public abstract class Service {
	
    private Logger logger;
    private boolean shutUp = true;
    private static final long refT = System.nanoTime();
    
	public Service() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	public void log(final Object obj, final Level l) {
		if(shutUp) return;
		long millis = (System.nanoTime()-refT)/1000000;
		logger.log(l, String.format("@Thread[%3d]:%8dms >  %s", Thread.currentThread().getId(), millis, obj));		
	}
	
	public void log(final Object obj) {
		log(obj, Level.INFO);
	}
}
