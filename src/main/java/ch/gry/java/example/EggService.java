package ch.gry.java.example;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import ch.gry.java.example.model.Egg;
import rx.Observable;

public class EggService {

    private static final Logger logger = Logger.getLogger(EggService.class.getName());

	private AtomicInteger shelfCapacity;
	private AtomicInteger layingInterval;
	private BlockingQueue<Egg> eggShelf;
	
	private CountDownLatch countDownLatch;
	
	public EggService(int shelfSize, int layingInterval) {
		this.layingInterval = new AtomicInteger(layingInterval);
		this.shelfCapacity = new AtomicInteger(shelfSize);
		this.eggShelf = new ArrayBlockingQueue<>(shelfCapacity.get());
	}
	
	private Thread eggLayingTask = new Thread(() -> {
		countDownLatch = new CountDownLatch(1);
		logger.info(String.format("Thread: %3d | START eggLayingTask!", Thread.currentThread().getId()));
		try {
			while(!countDownLatch.await(this.layingInterval.get(), TimeUnit.MILLISECONDS)){
				layEgg();
			}
			logger.info(String.format("Thread: %3d | STOP eggLayingTask", Thread.currentThread().getId()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	});
	
	public void startEggProduction() {
		eggLayingTask.start();
	}
	
	public void stopEggProduction() {
		countDownLatch.countDown();
	}
	
	public Observable<Egg> grabEggs(final int noOfRequestedEggs) {
		return Observable.create((observer)->{
			try {
				if(!observer.isUnsubscribed()) {
					for (int i = 0; i < noOfRequestedEggs; i++) {
						observer.onNext(eggShelf.take());
					}
					observer.onCompleted();
				}				
			} catch (Throwable e) {
				observer.onError(e);
			}
		});
	}
	
//	private Egg grabEgg() {
//		try {
//			Egg grabbedEgg = eggShelf.take();
//			logger.info(String.format("Thread: %3d |   << TAKE %s from shelf   new shelf count(%d/%d)", Thread.currentThread().getId(), grabbedEgg, eggShelf.size(), shelfCapacity.get()));
//			return grabbedEgg;
//		} catch (InterruptedException e) {
//			logger.severe("Grabbing the next egg has been interrupted! returns null.");
//			return null;
//		}		
//	}

	void layEgg() {
		Random rand = new Random();
		// variance of 30.0 grams
		double variance = rand.nextInt(300)/10.0; 
		// i.e. 40-70 grams
		double eggWeight = 40+variance;
		Egg newEgg = new Egg(LocalDate.now(), eggWeight);
		if(eggShelf.size()<shelfCapacity.get()) {
			logger.info(String.format("Thread: %3d |   >> PUT %s to shelf   new shelf count(%d/%d)", Thread.currentThread().getId(), newEgg, eggShelf.size()+1, shelfCapacity.get()));
			eggShelf.add(newEgg);
		}		
		else {
			logger.warning(String.format("Thread: %3d |   WARN: Shelf is full. Discard new egg!", Thread.currentThread().getId()));
		}
	}
	
}

