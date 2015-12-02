package ch.gry.java.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.gry.java.example.model.EasterEgg;
import ch.gry.java.example.model.Egg;
import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;
import rx.schedulers.Schedulers;

public class TestEasterEggService {

	private EasterEggService service = new EasterEggService();
		
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
		Instant start = Instant.now();
		EasterEgg easterEgg = service.colorizeEgg(new Egg(LocalDate.now(), 53.3), new Paint(Color.BLUE, 60));
		assertEquals(now, easterEgg.getLayingDate());
		assertEquals(53.3d, easterEgg.getWeight(), 0.0001d);
		assertEquals(Color.BLUE, easterEgg.getColor());
		Duration duration = Duration.between(start, Instant.now());
		assertTrue(duration.getNano()>=200000000 & duration.getNano()<220000000);		
	}

	@Test
	public void testEasterEggs() throws InterruptedException {
		System.out.println(String.format("======= START TEST ========="));
		Thread.sleep(1000);
		Map<Color, Integer> colorSetting = new HashMap<>();
		
		///////////// order //////////////
		colorSetting.put(Color.RED, 2);
		colorSetting.put(Color.BLUE, 1);
		colorSetting.put(Color.YELLOW, 3);
		colorSetting.put(Color.GREEN, 4);
		//////////////////////////////////
		
		List<EasterEgg> easterEggs = service.getEasterEggs(colorSetting);
		for (EasterEgg easterEgg : easterEggs) {
			System.out.println(easterEgg);
		}
		
		System.out.println(String.format("======= STOP TEST ========="));		
	}
	
	@Test
	public void testEasterEggs_rx() throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(1);
		System.out.println(String.format("======= START TEST rx ======"));
		Thread.sleep(1000);
		Map<Color, Integer> colorSetting = new HashMap<>();
		
		///////////// order //////////////
		colorSetting.put(Color.RED, 2);
		colorSetting.put(Color.BLUE, 1);
		colorSetting.put(Color.YELLOW, 3);
		colorSetting.put(Color.GREEN, 4);
		//////////////////////////////////
		
		service.getEasterEggs_rx(colorSetting)
			.subscribeOn(Schedulers.newThread())
			.subscribe(
					egg -> {
						System.out.println(String.format(" -> Received: %s", egg));
					},
					e -> e.printStackTrace(),
					() -> cdl.countDown());
		cdl.await();
		System.out.println(String.format("======= STOP TEST rx ======"));
	}
	
}
