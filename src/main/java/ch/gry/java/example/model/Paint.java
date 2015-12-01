package ch.gry.java.example.model;

import java.io.Serializable;

import ch.gry.java.example.model.type.Color;

/**
 * A data bean representing a certain amount of paint,
 * defined by {@linkplain ch.gry.java.example.model.type.Color Color} and {@link #quantity}. 
 * @author yvesgross
 */
public class Paint implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
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
	public long getQuantity() {
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
