package ch.gry.java.example;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;
import rx.Observable;

/**
 * Service to obtain a desired color and amount of paint from the managed PaintStorage.
 * @author yvesgross
 */
public class PaintService extends Service {

	private PaintStorage paintStorage; 

	/**
	 * PaintService constructor
	 * @param productionQuantity Define the quantity[milliliters] of paint to be reproduced, 
	 * once the requested paint cannot be served from the paint storage.
	 */
	public PaintService(Long productionQuantity) {
		paintStorage = new PaintStorage(productionQuantity);
	}
	
	/**
	 * Delivers the paint of the requested color and quantity.
	 * If the storage doesn't contain enough of the requested paint, it will
	 * wait for the remaining paint to be produced.
	 * @param color The requested paint color
	 * @param requestedQuantity The requested paint quantity [milliliters]
	 * @return
	 */
	public Observable<Paint> getPaint(final Color color, long requestedQuantity) {

		return Observable.create(observer -> {
			log(String.format("request %dml of %s (PaintStorage has %dml remaining)", requestedQuantity, color, paintStorage.remainingQuantity(color)));
			while(paintStorage.remainingQuantity(color) < requestedQuantity){
				paintStorage.producePaint(color); // this takes time!
			}
			if(!observer.isUnsubscribed()) {
				observer.onNext(paintStorage.tapPaint(color, requestedQuantity));
				observer.onCompleted();
			}
		});
		
	}
	
	
	
	//////////////////// private stuff /////////////////////////////

	private static class PaintStorage extends Service {

		AtomicLong productionQuantity;
		
		private ConcurrentHashMap<Color, Long> paintBarrels = new ConcurrentHashMap<>(Color.values().length);
		
		public PaintStorage(Long productionQuantity) {
			this.productionQuantity = new AtomicLong(productionQuantity);
			Arrays.asList(Color.values()).stream().forEach(c -> paintBarrels.put(c, 0L));
		}

		public long remainingQuantity(final Color color) {
			return paintBarrels.get(color);
		}
		
		public Paint tapPaint(final Color color, long requestetQuantity) {
			if(remainingQuantity(color) >= requestetQuantity) {
				paintBarrels.put(color, remainingQuantity(color)-requestetQuantity);
				return new Paint(color, requestetQuantity);
			}
			else {
				log(String.format("Not enough %s available! requested:%d, remaining:%d", color, requestetQuantity, remainingQuantity(color)), Level.WARNING);
				return null;				
			}
		}
		
		public void producePaint(final Color color){
			CountDownLatch cdl = new CountDownLatch(1);
			
			// production time depends on the quantity
			final int productivity = 2;
			long productionTime = productionQuantity.get()/productivity;
			
			try {
				log(String.format(" ----> about to produce %dml of %s", productionQuantity.get(), color));
				cdl.await(productionTime, TimeUnit.MILLISECONDS);
				paintBarrels.put(color, remainingQuantity(color) + productionQuantity.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
