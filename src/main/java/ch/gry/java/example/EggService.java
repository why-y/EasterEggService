package ch.gry.java.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import rx.schedulers.Schedulers;

/**
 * Service to obtain eggs from an egg shelf.
 * An egg laying task running in the background makes sure, 
 * that eggs are being produced whenever there is available space
 * on the egg shelf.
 * @author yvesgross
 */
public class EggService extends Service {

	private static final EggService instance = new EggService();
	
	private static final AtomicInteger shelfCapacity = new AtomicInteger(10);
	private static final AtomicInteger layingInterval = new AtomicInteger(50); // milliseconds to lay an egg
	private static final BlockingQueue<Egg> eggShelf = new ArrayBlockingQueue<>(shelfCapacity.get());
	
	private ExecutorService eggProductionExecutor = null;

	// explicitly declared private
	private EggService() {
	}
	
	/**
	 * Return the instance of this Singleton
	 * @return The unique instance of this Service
	 */
	public static final EggService getInstance() {
		return instance;
	}
	
	/**
	 * Tries to put the given eggs to the egg shelf.
	 * Since the shelf capacity is limited, it cannot
	 * be guaranteed to stock all of the given eggs. 
	 * @param eggsToStock The eggs to put on the egg shelf
	 * @return The eggs that actually have been stocked
	 */
	public List<Egg> stockEggs(List<Egg> eggsToStock) {
		List<Egg> stockedEggs = new ArrayList<>();
		for (Egg eggToStock : eggsToStock) {
			if(eggShelf.offer(eggToStock)) {
				stockedEggs.add(eggToStock);
			}
		}
		return stockedEggs;
	}
		
	/**
	 * TODO:
	 * @param noOfRequestedEggs
	 * @return
	 */
	public List<Egg> pickEggs(int noOfRequestedEggs) {
		List<Egg> result = new ArrayList<>();
		while(result.size() < noOfRequestedEggs) {
			try {
				Egg eggFromShelf = eggShelf.take();
				log(String.format("PICK %s from shelf   new shelf count(%d/%d)", eggFromShelf, eggShelf.size(), shelfCapacity.get()));
				result.add(eggFromShelf);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		return result;
	}
	
	/**
	 * Picks a requested number of eggs from the egg shelf.
	 * If the requested number exceeds the number of available eggs from the shelf, 
	 * it will wait for the remaining eggs to be laid.
	 * @param noOfRequestedEggs
	 * @return
	 */
	public Observable<Egg> pickEggs_rx(int noOfRequestedEggs) {
		return Observable.range(0, noOfRequestedEggs, Schedulers.newThread()).map(i -> {
			try {
				if(eggShelf.isEmpty()) {
					log(String.format("....... waiting(ThreadId:%d) for a new egg ....... ", Thread.currentThread().getId()));
				}
				Egg eggFromShelf = eggShelf.take();
				log(String.format("PICK %s from shelf   new shelf count(%d/%d)", eggFromShelf, eggShelf.size(), shelfCapacity.get()));
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
		eggProductionExecutor = Executors.newFixedThreadPool(2);
		eggProductionExecutor.execute(() -> {
			while (!eggProductionExecutor.isShutdown()) {
				try {
					// lay an egg and wait for it (blocking)
					Egg newEgg = eggProductionExecutor.submit(layAnEggTask).get();
					
					// try to put the new egg onto the shelf (offer)
					eggShelf.offer(newEgg);
					
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}					
		});
	}
	
	void stopEggProductionTask() {
		if(eggProductionExecutor.isShutdown()) {
			log("  ... the executor has already been shutdown!", Level.WARNING);
		}
		else {
			eggProductionExecutor.shutdown();
			try {
				eggProductionExecutor.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		log("The Egg Production Task has been terminated!");
	}
	
	private Callable<Egg> layAnEggTask = () -> { 
		Thread.sleep(layingInterval.get());		
		// variance of 30.0 grams
		double variance = new Random().nextInt(300)/10.0; 
		// i.e. 40-70 grams
		double eggWeight = 40+variance;
		return new Egg(LocalDate.now(), eggWeight);
	};
	
}

