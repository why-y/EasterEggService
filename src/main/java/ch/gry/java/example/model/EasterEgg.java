package ch.gry.java.example.model;

import ch.gry.java.example.model.type.Color;

/**
 * Data bean representing an easter egg specified by
 * the {@linkplain ch.gry.java.example.model.type.Color Color}
 * and an {@linkplain ch.gry.java.example.model.Egg Egg}
 * @author yvesgross
 */
public class EasterEgg {

	private Egg egg;
	private Color color;
	
	public EasterEgg(final Egg egg, final Color color) {
		this.egg = egg;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public Egg getEgg() {
		return egg;
	}
	
	@Override
	public String toString() {
		return "EasterEgg [id=" + egg.getId() + ", color=" + color + "]";
	}

}
