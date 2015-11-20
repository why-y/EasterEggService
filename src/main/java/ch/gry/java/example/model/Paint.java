package ch.gry.java.example.model;

import ch.gry.java.example.model.type.Color;

public class Paint {
	
	private Color color;
	private long quantity;
	
	public Paint(Color color, long quantity) {
		super();
		this.color = color;
		this.quantity = quantity;
	}
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Paint [color=" + color + ", quantity=" + quantity + "]";
	}
	
}
