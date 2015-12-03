package ch.gry.java.example;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ServiceLogFormatter extends Formatter {

	private static final long t0 = System.currentTimeMillis();
	
	public ServiceLogFormatter() {
		super();
	}
	
	@Override
	public String format(LogRecord record) {
		long diff = System.currentTimeMillis()-t0;
		String[] chunks = record.getLoggerName().split("\\.|\\$");
        return String.format("%10s [%4.3f] [%2d] [%18s] [%s]\n",
        		record.getLevel(),
        		(diff%10000)/1000.0,
        		record.getThreadID(),
        		chunks[chunks.length-1],
        		record.getMessage());
	}

}
