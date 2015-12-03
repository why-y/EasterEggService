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

	private PaintStorage paintStorage = new PaintStorage();
	
	private static final PaintService instance = new PaintService();
	
	// explicitly declared private
	private PaintService() {
	}
	
	/**
	 * Return the instance of this Singleton
	 * @return The unique instance of this Service
	 */
	public static final PaintService getInstance() {
		return instance;
	}
	
	/**
	 * TODO:
	 * @param color
	 * @param requestedQuantity
	 * @return
	 */
	public Paint getPaint(final Color color, long requestedQuantity) {
		while(paintStorage.remainingQuantity(color) < requestedQuantity){
			paintStorage.producePaint(color); // this takes time!
		}
		return paintStorage.tapPaint(color, requestedQuantity);
	}
	
	/**
	 * Delivers the paint of the requested color and quantity.
	 * If the storage doesn't contain enough of the requested paint, it will
	 * wait for the remaining paint to be produced.
	 * @param color The requested paint color
	 * @param requestedQuantity The requested paint quantity [milliliters]
	 * @return
	 */
	public Observable<Paint> getPaint_rx(final Color color, long requestedQuantity) {

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

		private static final AtomicLong productionQuantity = new AtomicLong(200l);
		
		private ConcurrentHashMap<Color, Long> paintBarrels = new ConcurrentHashMap<>(Color.values().length);
		
		public PaintStorage() {
			Arrays.asList(Color.values()).stream().forEach(c -> paintBarrels.put(c, 0L));
		}

		public long remainingQuantity(final Color color) {
			return paintBarrels.get(color);
		}
		
		public Paint tapPaint(final Color color, long requestedQuantity) {
			if(remainingQuantity(color) >= requestedQuantity) {
				paintBarrels.put(color, remainingQuantity(color)-requestedQuantity);
				return new Paint(color, requestedQuantity);
			}
			else {
				log(String.format("Not enough %s available! requested:%d, remaining:%d", color, requestedQuantity, remainingQuantity(color)), Level.WARNING);
				return null;				
			}
		}
		
		public void producePaint(final Color color){
			CountDownLatch cdl = new CountDownLatch(1);
			
			// production time depends on the quantity
			final int productivity = 1000;
			long productionTime = productionQuantity.get()*1000/productivity;
			
			try {
				log(String.format("....... waiting(ThreadId:%d) for producing %dml of %s paint ........", Thread.currentThread().getId(), productionQuantity.get(), color));
				cdl.await(productionTime, TimeUnit.MILLISECONDS);
				paintBarrels.put(color, remainingQuantity(color) + productionQuantity.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
