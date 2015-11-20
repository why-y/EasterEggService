/**
 * 
 */
package ch.gry.java.example;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.gry.java.example.EggService;
import ch.gry.java.example.model.Egg;

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
		service = new EggService(40, 100);
		service.startEggProduction();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		service.stopEggProduction();
	}

	@Test
	public void testStartStopEggLayingTask() throws InterruptedException {
		
		Thread.sleep(250);

		long start = System.currentTimeMillis();
		
		List<Egg> eggs =  new ArrayList<>();
		service.grabEggs(6).subscribe(
				egg -> {eggs.add(egg);System.out.println(egg + " size:" + eggs.size());},
				e -> e.printStackTrace());
		
		long duration = System.currentTimeMillis()-start;

		System.out.println(String.format("grabbed %d eggs in %d milliseconds: %s", eggs.size(), duration, eggs));	
		
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

}
