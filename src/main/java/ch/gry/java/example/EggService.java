package ch.gry.java.example;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import ch.gry.java.example.model.Egg;
import rx.Observable;

/**
 * Service to obtain eggs from an egg shelf.
 * An egg laying task running in the background makes sure, 
 * that eggs are being produced whenever there is available space
 * on the egg shelf.
 * @author yvesgross
 */
public class EggService extends Service {

	private AtomicInteger shelfCapacity;
	private AtomicInteger layingInterval;
	private BlockingQueue<Egg> eggShelf;
	
	private ExecutorService eggsTreadPool = Executors.newFixedThreadPool(5);

	/**
	 * EggService constructor
	 * @param shelfSize The capacity of the egg shelf
	 * @param layingInterval Defines in what interval eggs are being laid, once the egg laying task has been started.
	 */
	public EggService(int shelfSize, int layingInterval) {
		this.layingInterval = new AtomicInteger(layingInterval);
		this.shelfCapacity = new AtomicInteger(shelfSize);
		this.eggShelf = new ArrayBlockingQueue<>(shelfCapacity.get());
	}

	/**
	 * Picks a requested number of eggs from the egg shelf.
	 * If the requested number exceeds the number of available eggs from the shelf, 
	 * it will wait for the remaining eggs to be laid.
	 * @param noOfRequestedEggs
	 * @return
	 */
	public Observable<Egg> pickEggs(final int noOfRequestedEggs) {
		return Observable.range(0, noOfRequestedEggs).map(i -> {
			try {
				Egg eggFromShelf = eggShelf.take();
				log(String.format("   >> PICK %s from shelf   new shelf count(%d/%d)", eggFromShelf, eggShelf.size(), shelfCapacity.get()));
				return eggFromShelf;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		});
	}
	
	
	
	//////////////////// private stuff /////////////////////////////

	void startEggProductionTask() {
		log("Start the Egg Production Task");
		eggsTreadPool.execute(() -> {
			while (!eggsTreadPool.isShutdown()) {
				if(eggShelf.size() < shelfCapacity.get()) {
					try {
						Egg newEgg = eggsTreadPool.submit(layAnEggTask).get();
						log(String.format("   >> PUT %s to shelf   new shelf count(%d/%d)", newEgg, eggShelf.size()+1, shelfCapacity.get()));
						eggShelf.add(newEgg);
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
			}					
		});
	}
	
	void stopEggProductionTask() {
		if(eggsTreadPool.isShutdown()) {
			log("  ... the executor has already been shutdown!", Level.WARNING);
		}
		else {
			eggsTreadPool.shutdown();
			try {
				eggsTreadPool.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		log("The Egg Production Task has been terminated!");
	}
	
	private Callable<Egg> layAnEggTask = () -> { 
		Thread.sleep(this.layingInterval.get());		
		// variance of 30.0 grams
		double variance = new Random().nextInt(300)/10.0; 
		// i.e. 40-70 grams
		double eggWeight = 40+variance;
		return new Egg(LocalDate.now(), eggWeight);
	};
	
}

