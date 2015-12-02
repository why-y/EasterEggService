/**
 * 
 */
package ch.gry.java.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.gry.java.example.model.Egg;
import rx.schedulers.Schedulers;

/**
 * @author yvesgross
 *
 */
public class TestEggService {
	
	private EggService service = EggService.getInstance();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service.startEggProductionTask();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		service.stopEggProductionTask();
	}

	@Test
	public void testStartStopEggLayingTask() throws InterruptedException {
		
		CountDownLatch cdl = new CountDownLatch(1);
		
		Thread.sleep(650);

		long start = System.currentTimeMillis();
		
		List<Egg> eggs =  new ArrayList<>();
		service.pickEggs_rx(16)
			.subscribeOn(Schedulers.newThread())
			.subscribe(
				egg -> {eggs.add(egg);System.out.println("Received " + egg + " noOfEggs:" + eggs.size());},
				e -> e.printStackTrace(),
				() -> cdl.countDown());
		
		cdl.await(10, TimeUnit.SECONDS);
		
		long duration = System.currentTimeMillis()-start;
		System.out.println(String.format("grabbed %d eggs in %d milliseconds: %s", eggs.size(), duration, eggs));	
		
		// give some time to fill the shelf again
		Thread.sleep(550);
		
	}

}
