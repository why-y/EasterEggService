package ch.gry.java.example.model;

import ch.gry.java.example.model.type.Color;

/**
 * Data bean representing an easter egg specified by
 * the {@linkplain ch.gry.java.example.model.type.Color Color}
 * and the attributes of an {@linkplain ch.gry.java.example.model.Egg Egg}
 * @author yvesgross
 */
public class EasterEgg extends Egg {

	private Color color;
	
	public EasterEgg(final Egg egg, final Color color) {
		super(egg.getLayingDate(), egg.getWeight());
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "EasterEgg [color=" + color + ", layingDate=" + layingDate + ", weight=" + weight + "]";
	}

}
