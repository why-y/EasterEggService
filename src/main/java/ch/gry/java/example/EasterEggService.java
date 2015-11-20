package ch.gry.java.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import ch.gry.java.example.model.EasterEgg;
import ch.gry.java.example.model.Egg;
import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;

public class EasterEggService {
	
    private static final Logger logger = Logger.getLogger(EasterEggService.class.getName());

	private static final int COLORING_DURATION = 200; // [ms]
	
	private EggService eggService =  new EggService(10, 1000);
	private PaintService paintService = new PaintService(500L);
	
	public EasterEggService() {
		eggService.startEggProduction();
	}
	
	public List<EasterEgg> getEasterEggs(final Map<Color,Integer> order) {
		
		Integer noOfAllEggs = order.values().stream().reduce(0, (a, b) -> a+b);
		
		List<EasterEgg> easterEggs = new ArrayList<>(noOfAllEggs);
		
		for (Color color : order.keySet()) {
			logger.info("Color: " + color);
			Integer noOfEggs = order.get(color);
			if(noOfEggs>0) {
				eggService.grabEggs(noOfEggs).subscribe(egg -> {
					logger.info("   Egg: " + egg);
					Paint requiredPaint = paintService.getPaint(color, calculatePaintQuantity(egg));
					easterEggs.add(colorizeEgg(egg, requiredPaint));
				});
			}
		}
		
		logger.info("EasterEggs: " + easterEggs);
		return null;
	}
	
	public EasterEgg colorizeEgg(final Egg egg, final Paint paint) {
		if(paint.getQuantity() < calculatePaintQuantity(egg)) {
			logger.warning("Not enough paint for the given egg! Return null.");
			return null;
		}
		try {
			Thread.sleep(COLORING_DURATION);
			return new EasterEgg(egg, paint.getColor());
		} catch (InterruptedException e) {
			logger.severe(String.format("Coloring %s has been interrupted! return null.", egg));
			return null;
		}
	}
	
	/**
	 * Determines the required paint quantity[ml], depending on the given egg.
	 * @param egg
	 * @return paint quantity in milliliters
	 */
	public long calculatePaintQuantity(final Egg egg) {
		int factor = 1;
		// round up to be on the safe side
		return (long) Math.ceil(egg.getWeight()*factor);
	}
}
