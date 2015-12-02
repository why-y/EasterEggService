/**
 * 
 */
package ch.gry.java.example;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import ch.gry.java.example.model.type.Color;

/**
 * @author yvesgross
 */
public class TestPaintService {

	PaintService service = PaintService.getInstance();
	
	@Test
	public void testSomePaint() {
		for(int i=0; i<8; ++i) {
			Instant start = Instant.now();
			service.getPaint_rx(Color.YELLOW, 120)
				.subscribe(paint -> System.out.println("Received -> " + paint));
			System.out.println("getting yellow took " + Duration.between(start, Instant.now()));
		}
	}

}
