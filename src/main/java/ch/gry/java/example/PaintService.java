package ch.gry.java.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;

public class PaintService {

    private static final Logger logger = Logger.getLogger(PaintService.class.getName());

	ConcurrentHashMap<Color, Long> paintShelf = new ConcurrentHashMap<>(Color.values().length);
	AtomicLong productionQuantity;

	private CountDownLatch countDownLatch;

	public PaintService(Long productionQuantity) {
		this.productionQuantity = new AtomicLong(productionQuantity);
		Stream.of(Color.values()).forEach(c -> paintShelf.put(c, new Long(0)));
	}
	
	public Paint getPaint(final Color color, long quantity) {
		Long currentQuantity = paintShelf.get(color);
		logger.info(String.format("get %dml of %s (stock has %dml) ...", quantity, color, currentQuantity));
		if(currentQuantity>=quantity) {
			paintShelf.put(color, currentQuantity-quantity);
			Paint newPaint = new Paint(color, quantity);
			logger.info(" immediatly return " + newPaint);
			return newPaint;
		}
		else{
			logger.info("... must produce more paint ...");
			produceNewPaint(color);
			return getPaint(color, quantity);
		}
	}
	
	public void terminateProduction() {
		while (countDownLatch.getCount()>0) {
			countDownLatch.countDown();
		}
	}
	
	private void produceNewPaint(final Color color){
		countDownLatch = new CountDownLatch(1);
		
		// production time depends on the productionQuantity
		final int productivity = 2;
		long productionTime = productionQuantity.get()/productivity;
		
		logger.info("ProductionTime : " + productionTime);
		try {
			countDownLatch.await(productionTime, TimeUnit.MILLISECONDS);
			Long currentQuantity = paintShelf.get(color);
			paintShelf.put(color, currentQuantity+productionQuantity.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
