/**
 * 
 */
package ch.gry.java.example;

import java.time.Duration;
import java.time.Instant;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.gry.java.example.PaintService;
import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;

/**
 * @author yvesgross
 *
 */
public class TestPaintService {

	PaintService service;
	
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
		service = new PaintService(500L);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSomePaint() {
		for(int i=0; i<8; ++i) {
			Instant start = Instant.now();
			service.getPaint(Color.YELLOW, 120)
				.subscribe(paint -> System.out.println("Received -> " + paint));
			System.out.println("getting yellow took " + Duration.between(start, Instant.now()));
		}
	}

}
