package ch.gry.java.example;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.gry.java.example.model.EasterEgg;
import ch.gry.java.example.model.Egg;
import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;
import rx.schedulers.Schedulers;

public class TestEasterEggService {

	private EasterEggService service;
	
	private static final Map<Color, Integer> colorSetting = new HashMap<>();
	
	private Instant start;
	
	static {
		colorSetting.put(Color.RED, 6);
		colorSetting.put(Color.BLUE, 4);
		colorSetting.put(Color.YELLOW, 6);
		colorSetting.put(Color.GREEN, 7);		
	}
	
	@Before
	public void testStarts() {
		System.out.println(String.format("\n======= START TEST ========="));
		start = Instant.now();
		service = new EasterEggService();
	}
	
	@After
	public void testStops() {
//		service.terminate();
		System.out.println(String.format("======= STOP TEST (Duration: %s) ======\n", 
				formatDuration(Duration.between(start, Instant.now()))));
	}
	
	@Test
	public void testCalculatePaintQuantity() {
		LocalDate now  = LocalDate.now();
		assertEquals(1, service.calculatePaintQuantity(new Egg(now, 0.1)));
		assertEquals(1, service.calculatePaintQuantity(new Egg(now, 0.9)));
		assertEquals(20, service.calculatePaintQuantity(new Egg(now, 20.0000)));
		assertEquals(21, service.calculatePaintQuantity(new Egg(now, 20.0001)));
		assertEquals(21, service.calculatePaintQuantity(new Egg(now, 20.9999)));
	}
	
	@Test
	public void testColorizeEgg() {
		LocalDate now = LocalDate.now();
		EasterEgg easterEgg = service.colorizeEgg(new Egg(LocalDate.now(), 53.3), new Paint(Color.BLUE, 60));
		assertEquals(now, easterEgg.getEgg().getLayingDate());
		assertEquals(53.3d, easterEgg.getEgg().getWeight(), 0.0001d);
		assertEquals(Color.BLUE, easterEgg.getColor());
	}

	@Test
	public void testEasterEggs() throws InterruptedException {
		System.out.println(String.format("    Test EasterEggService.getEasterEggs() with colorSetting: %s", colorSetting));
		List<EasterEgg> easterEggs = service.getEasterEggs(colorSetting);
		for (EasterEgg egg : easterEggs) {
			System.out.println(String.format(" -> Received after %ss: %s", formatDuration(Duration.between(start, Instant.now())), egg));
		}
		
	}
	
	@Test
	public void testEasterEggs_rx() throws InterruptedException {
		System.out.println(String.format("    Test EasterEggService.getEasterEggs_rx() with colorSetting: %s", colorSetting));
		CountDownLatch cdl = new CountDownLatch(1);
		service.getEasterEggs_rx(colorSetting)
			.subscribeOn(Schedulers.newThread())
			.subscribe(
					egg -> {
						System.out.println(String.format(" -> Received after %ss: %s", formatDuration(Duration.between(start, Instant.now())), egg));
					},
					e -> e.printStackTrace(),
					() -> cdl.countDown());
		cdl.await();
	}
	
	private String formatDuration(final Duration d) {
		return String.format("%d.%d", d.getSeconds(), d.getNano()/1000000);
	}
}
