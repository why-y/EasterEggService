package ch.gry.java.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import ch.gry.java.example.model.EasterEgg;
import ch.gry.java.example.model.Egg;
import ch.gry.java.example.model.Paint;
import ch.gry.java.example.model.type.Color;
import rx.Observable;

/**
 * Service to obtain easter eggs according a the desired color setting
 * @author yvesgross
 */
public class EasterEggService extends Service  {
	
	private static final int COLORING_DURATION = 200; // [ms]
	
	private EggService eggService =  EggService.getInstance();
	private PaintService paintService = PaintService.getInstance();
	
	/**
	 * EasterEgg constructor
	 */
	public EasterEggService() {
		eggService.startEggProductionTask();
	}
	
	// TODO:
	public List<EasterEgg> getEasterEggs(final Map<Color,Integer> colorSetting) {
		List<EasterEgg> result = new ArrayList<>();
		for (Color color : colorSetting.keySet()) {
			List<Egg> eggs = eggService.pickEggs(colorSetting.get(color));
			for (Egg egg : eggs) {
				long paintQuantity = calculatePaintQuantity(egg);
				Paint paint = paintService.getPaint(color, paintQuantity);
				result.add(colorizeEgg(egg, paint));
			}
		}
		return result;
	}
	
	/**
	 * Returns easter eggs according to a desired color setting, 
	 * e.g (two blue eggs, three red eggs and six green eggs).
	 * @param colorSetting
	 * @return
	 */
	public Observable<EasterEgg> getEasterEggs_rx(final Map<Color,Integer> colorSetting) {
		
		// flatten the colorSetting-map from e.g. {(BLUE:3),(RED:2),(GREEN:4)}
		// to {BLUE,BLUE,BLUE,RED,RED,GREEN,GREEN,GREEN,GREEN}
		Observable<Color> flatColorSetting = Observable
				.from(colorSetting.keySet())
				.flatMap(c-> Observable.just(c).repeat(colorSetting.get(c)));

		Integer noOfEggs = colorSetting.values().stream().reduce(0, (a, b) -> a+b);
		Observable<Egg> eggs = eggService.pickEggs_rx(noOfEggs);
		
		Observable<Observable<EasterEgg>> easterEggsObservables = eggs.zipWith(flatColorSetting, (egg, color) -> {
			long paintQuantity = calculatePaintQuantity(egg);
			return paintService
					.getPaint_rx(color, paintQuantity)
					.map(p -> colorizeEgg(egg, p));
		});
		
		// flatten Observable of Observabless
		Observable<EasterEgg> easterEggs = Observable.switchOnNext(easterEggsObservables);
				
		return easterEggs;
	}
	
	
	//////////////////// private stuff /////////////////////////////
	
	EasterEgg colorizeEgg(final Egg egg, final Paint paint) {
		log(String.format("colorizeEgg: %s with %s", egg, paint));
		long requiredQuantity = calculatePaintQuantity(egg);
		if(paint.getQuantity() < requiredQuantity) {
			log(String.format("Not enough paint for the given %s! Required:%dml, Received:%dml. -> Return null.", egg, requiredQuantity, paint.getQuantity()),  Level.WARNING);
			return null;
		}
		try {
			Thread.sleep(COLORING_DURATION);
			return new EasterEgg(egg, paint.getColor());
		} catch (InterruptedException e) {
			log(String.format("Coloring %s has been interrupted! return null.", egg), Level.SEVERE);
			return null;
		}
	}
	
	/**
	 * Determines the required paint quantity[ml], depending on the given egg.
	 * @param egg
	 * @return paint quantity in milliliters
	 */
	long calculatePaintQuantity(final Egg egg) {
		int factor = 1;
		long ret = (long) Math.ceil(egg.getWeight()*factor);
//		log(String.format("============= calculated paint for %s is %d", egg, ret));
		return ret;
	}
}
