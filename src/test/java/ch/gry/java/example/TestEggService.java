/**
 * 
 */
package ch.gry.java.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.gry.java.example.EggService;
import ch.gry.java.example.model.Egg;
import rx.schedulers.Schedulers;

/**
 * @author yvesgross
 *
 */
public class TestEggService {
	
	private EggService service;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service = new EggService(4, 100);
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
		service.pickEggs(16)
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
	
	
	@Test
	public void testThreadEnd() throws InterruptedException {
		System.out.println(String.format("Thread:%d START", Thread.currentThread().getId()));
		
		Thread testThread = new Thread(() -> {
			synchronized (this) {
				System.out.println(String.format("  Thread:%d START", Thread.currentThread().getId()));
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(String.format("  Thread:%d STOP", Thread.currentThread().getId()));
				notify();
			}
		});
		
		testThread.start();
		
		synchronized (testThread) {
			testThread.wait();
			System.out.println(String.format("Thread:%d STOP", Thread.currentThread().getId()));		
		}
	}

	@Test
	public void bla() throws InterruptedException {
		System.out.println("====== START BLA =========");
		Thread.sleep(4000);
		System.out.println("====== STOP BLA =========");
	}
}
