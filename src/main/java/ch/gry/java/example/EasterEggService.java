package ch.gry.java.example;

import java.util.Map;
import java.util.logging.Logger;

import ch.gry.java.example.model.EasterEgg;
import ch.gry.java.example.model.Egg;
import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;
import rx.Observable;

public class EasterEggService {
	
    private static final Logger logger = Logger.getLogger(EasterEggService.class.getName());

	private static final int COLORING_DURATION = 200; // [ms]
	
	private EggService eggService =  new EggService(10, 1000);
	private PaintService paintService = new PaintService(500L);
	
	public EasterEggService() {
		eggService.startEggProduction();
	}
	
	public Observable<EasterEgg> getEasterEggs(final Map<Color,Integer> colorSetting) {
		
		// flatten the colorSetting-map from e.g. {(BLUE:3),(RED:2),(GREEN:4)}
		// to {BLUE,BLUE,BLUE,RED,RED,GREEN,GREEN,GREEN,GREEN}
		Observable<Color> flatColorSetting = Observable
				.from(colorSetting.keySet())
				.flatMap(c-> Observable.just(c).repeat(colorSetting.get(c)));

		Integer noOfEggs = colorSetting.values().stream().reduce(0, (a, b) -> a+b);
		
		Observable<Egg> eggs = eggService.grabEggs(noOfEggs);
		
		Observable<EasterEgg> easterEggs = eggs.zipWith(flatColorSetting, (egg, color) -> {
			long paintQuantity = calculatePaintQuantity(egg);
			
			// TODO: get paint from paintService instead:
			Paint paint = new Paint(color, paintQuantity);
			
			return colorizeEgg(egg, paint);
		});
		
		return easterEggs;
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
