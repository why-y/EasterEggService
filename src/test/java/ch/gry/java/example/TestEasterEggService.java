package ch.gry.java.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.gry.java.example.EasterEggService;
import ch.gry.java.example.model.EasterEgg;
import ch.gry.java.example.model.Egg;
import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;
import rx.Observable;

public class TestEasterEggService {

	private EasterEggService service;
	
	@Before
	public void setUp() throws Exception {
		service = new EasterEggService();
	}

	@After
	public void tearDown() throws Exception {
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
		Instant start = Instant.now();
		EasterEgg easterEgg = service.colorizeEgg(new Egg(LocalDate.now(), 53.3), new Paint(Color.BLUE, 60));
		assertEquals(now, easterEgg.getLayingDate());
		assertEquals(53.3d, easterEgg.getWeight(), 0.0001d);
		assertEquals(Color.BLUE, easterEgg.getColor());
		Duration duration = Duration.between(start, Instant.now());
		assertTrue(duration.getNano()>200000000 & duration.getNano()<220000000);		
	}

	@Test
	public void testEasterEggs() throws InterruptedException {
		Thread.sleep(10);
		Map<Color, Integer> colorSetting = new HashMap<>();
		colorSetting.put(Color.RED, 1);
		colorSetting.put(Color.BLUE, 2);
		colorSetting.put(Color.YELLOW, 3);
		Instant start = Instant.now();
		service.getEasterEggs(colorSetting).subscribe(System.out::println);
		Duration duration = Duration.between(start, Instant.now());
		System.out.println("Duration: " + duration);
	}

	@Test
	public void dummy() {
		Map<Color, Integer> colorSetting = new HashMap<>();
		colorSetting.put(Color.RED, 1);
		colorSetting.put(Color.BLUE, 2);
		colorSetting.put(Color.YELLOW, 3);
		Observable.from(colorSetting.keySet()).flatMap(c-> Observable.just(c).repeat(colorSetting.get(c)))
			.subscribe(System.out::println);
		
	}
}
