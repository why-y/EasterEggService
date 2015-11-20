package ch.gry.java.example.model;

import ch.gry.java.example.model.type.Color;

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
